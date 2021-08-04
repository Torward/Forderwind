import Model.UploadFileMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;

@Slf4j
public class ByteInboundHandler extends ChannelInboundHandlerAdapter {
    private  int readByte;
    private volatile  int start = 0;
    private String filePath/* = "server/serverFiles"*/;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Клиент подключён...");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.debug("Связь с Клиентом прекращена!");
        ctx.flush();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf)msg;
//        log.debug(" Получен новый файл {}", msg);
//        while (buf.readableBytes() > 0){
        if (msg instanceof UploadFileMsg){
           // msg = buf.readByte();
            UploadFileMsg uf = (UploadFileMsg) msg;

            byte[] bytes = uf.getBytes();
            readByte = uf.getEnd();
            filePath = uf.getFilePath();
            String md5 = uf.getFile_md5();
            String path = filePath + File.separator + md5;
            File file = new File(path);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(start);
            randomAccessFile.write(bytes);
            start = start + readByte;
            if(readByte != 1024*8){
                Thread.sleep(1000);
                ctx.flush();
            }else {
                ctx.close();
            }
        }

    }
}
