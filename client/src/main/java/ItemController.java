import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Setting;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class ItemController {
    private String sroot = "server/serverFiles";
    @FXML
    private ImageView fileView;

    @FXML
    private Label fileName;

    public void setData(Setting setting){

        Image image = new Image(getClass().getResourceAsStream(setting.getFileViewSrc()));
        fileView.setImage(image);
        fileName.setText(setting.getFileName());

    }
}
