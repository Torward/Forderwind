import Model.UploadFileMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

@Slf4j
public class ClientUploadFileHandler extends ChannelInboundHandlerAdapter {
    private int readByte;
    private volatile int start = 0;
    private volatile int lastLength = 0;
    public RandomAccessFile randomAccessFile;
    private UploadFileMsg uploadFileMsg;
    public TextArea log_area;
    private String path = "server/serverFiles";

    public ClientUploadFileHandler(UploadFileMsg fu) {
        if (fu.getFile().exists()) {
            if (!fu.getFile().isFile()) {
                log_area.appendText("Это не файл: " + fu.getFile());
                return;
            }
        }
        this.uploadFileMsg = fu;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //log_area.appendText("Передача окончена!");
        log.debug("Клиент закончил передачу!");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Данные поступили...");
        try {
            randomAccessFile = new RandomAccessFile(uploadFileMsg.getFile(), "r");
            randomAccessFile.seek(uploadFileMsg.getStart());
            lastLength = 1024 * 8;
            byte[] bytes = new byte[lastLength];
            if ((readByte = randomAccessFile.read(bytes)) != -1) {
                uploadFileMsg.setEnd(readByte);
                uploadFileMsg.setBytes(bytes);
                ctx.writeAndFlush(uploadFileMsg);//отправляет на сервер
            }
            log.debug("Файл" + readByte + "был прочитан методом channelActive()!");
        } catch (FileNotFoundException e) {
            log.debug("Файл не найден");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("Работа метода channelActive() завершена!");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       uploadFileMsg.getBytes();
        if (msg instanceof Integer) {
            start = (Integer) msg;
            if (start != -1) {
                randomAccessFile = new RandomAccessFile(uploadFileMsg.getFile(), "r");
                randomAccessFile.seek(start);
                int a = (int) (randomAccessFile.length() - start);
                int b = (int) (randomAccessFile.length() / 1024 * 10);
                if (a < lastLength) {
                    lastLength = a;
                }
                log.debug("Длина файла" + (randomAccessFile.length()) + ", start: " + start + ", a: " + a + ", b: " + b+", lastLength"+ lastLength);
                byte[] bytes = new byte[lastLength];
                if ((readByte = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0) {
                    uploadFileMsg.setEnd(readByte);
                    uploadFileMsg.setBytes(bytes);
                    try {
                        ctx.writeAndFlush(uploadFileMsg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    randomAccessFile.close();
                    ctx.close();
                    log.debug(readByte + " был прочитан!");
                }
            }

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
