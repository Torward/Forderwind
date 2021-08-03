import Model.UploadFileMsg;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {
    private UploadFileMsg uploadFileMsg;

    public Server()  {
        EventLoopGroup auth = new NioEventLoopGroup(1);//тяжеловесный воркер и лёгкий воркер
        EventLoopGroup worker = new NioEventLoopGroup();
        try {


        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(auth, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(
                                new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                new ByteInboundHandler()
                        );
                        //можно добавить хендлеры
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(8287).sync();
        log.debug("Server started...");
        channelFuture.channel().closeFuture().sync();//блокирующая операция
    }catch (Exception e){
            log.error("",e);
        }finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }


    }

    public static void main(String[] args) {
        new Server();
    }
}
