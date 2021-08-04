import Model.UploadFileMsg;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyNetwork {
    private SocketChannel channel;
    private UploadFileMsg uploadFileMsg;
    ChannelPromise channelPromise;

    public NettyNetwork(UploadFileMsg uploadFileMsg) {
        this.uploadFileMsg = uploadFileMsg;
        /*Thread thread = new Thread(()->{*/
            EventLoopGroup worker = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(worker)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                channel = ch;
                                ch.pipeline().addLast(
                                        new ObjectEncoder(),
                                        new ObjectDecoder(ClassResolvers.weakCachingResolver(ClientUploadFileHandler.class.getClassLoader())),
                                        new ClientUploadFileHandler(uploadFileMsg)
                                );
                            }
                        });
                ChannelFuture future = bootstrap.connect("localhost", 8287).sync();
                future.channel().closeFuture().sync();
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Ошибка с ChannelFuture");
            }finally {
                worker.shutdownGracefully();
            }
       /* });
        thread.setDaemon(true);
        thread.start();*/
    }
    public void writeMessage(UploadFileMsg message){
        channel.writeAndFlush(message);
    }

}
