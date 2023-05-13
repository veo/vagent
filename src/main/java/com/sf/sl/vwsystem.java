//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.AsynchronousSocketChannel;
//import java.nio.channels.CompletionHandler;
//import java.util.HashMap;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//import javax.websocket.*;
//
//public class vwsystem extends Endpoint implements MessageHandler.Whole<ByteBuffer>,CompletionHandler<Integer, Session> {
//
//    private Session session;
//    private Boolean first;
//    private AsynchronousSocketChannel client = null;
//    private final ByteBuffer buffer = ByteBuffer.allocate(1024*64);
//    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    private final HashMap<String,AsynchronousSocketChannel> map = new HashMap<String,AsynchronousSocketChannel>();
//
//    @Override
//    public void completed(Integer result, Session channel) {
//        buffer.clear();
//        try {
//            if(buffer.hasRemaining() && result>=0)
//            {
//                byte[] arr = new byte[result];
//                ByteBuffer b = buffer.get(arr,0,result);
//                baos.write(arr,0,result);
//                ByteBuffer q = ByteBuffer.wrap(baos.toByteArray());
//                if (channel.isOpen()) {
//                    channel.getBasicRemote().sendBinary(q);
//                }
//                baos = new ByteArrayOutputStream();
//                readFromServer(channel,client);
//            }else{
//                if(result > 0)
//                {
//                    byte[] arr = new byte[result];
//                    ByteBuffer b = buffer.get(arr,0,result);
//                    baos.write(arr,0,result);
//                    readFromServer(channel,client);
//                }
//            }
//        } catch (Exception ignored) {
//        }
//    }
//
//    @Override
//    public void failed(Throwable t, Session channel) {
//        t.printStackTrace();
//    }
//
//    @Override
//    public void onMessage(ByteBuffer message) {
//        try {
//            message.clear();
//            process(message,session);
//            this.first = false;
//        } catch (Exception ignored) {
//        }
//    }
//
//    @Override
//    public void onOpen(Session session, EndpointConfig endpointConfig) {
//        this.first = true;
//        this.session = session;
//        session.setMaxBinaryMessageBufferSize(1024*64);
//        session.setMaxTextMessageBufferSize(1024*64);
//        session.addMessageHandler(this);
//    }
//    @Override
//    public void onClose(Session session, CloseReason closeReason) {
//        try {
//            session.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void readFromServer(Session channel,final AsynchronousSocketChannel client){
//        this.client = client;
//        buffer.clear();
//        client.read(buffer, channel, this);
//    }
//
//    private void process(ByteBuffer z,Session channel) {
//        try{
//            if(this.first) {
//                String values = new String(z.array());
//                String[] array = values.split(" ");
//                String[] addrarray = array[1].split(":");
//                AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
//                int po = Integer.parseInt(addrarray[1]);
//                InetSocketAddress hostAddress = new InetSocketAddress(addrarray[0], po);
//                Future<Void> future = client.connect(hostAddress);
//                try {
//                    future.get(10, TimeUnit.SECONDS);
//                } catch(Exception ignored){
//                    channel.getBasicRemote().sendText("HTTP/1.1 503 Service Unavailable\r\n\r\n");
//                    return;
//                }
//                map.put(channel.getId(), client);
//                readFromServer(channel,client);
//                channel.getBasicRemote().sendText("HTTP/1.1 200 Connection Established\r\n\r\n");
//
//            } else {
//                AsynchronousSocketChannel client = map.get(channel.getId());
//                client.write(z).get();
//                readFromServer(channel,client);
//            }
//        }catch(Exception ignored){
//        }
//    }
//}