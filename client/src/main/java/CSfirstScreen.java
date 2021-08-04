import Model.UploadFileMsg;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Setting;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class CSfirstScreen implements Initializable {
    public Button download_btn;
    public Button upload_btn;
    public TextArea log_area;
    public AnchorPane dragAndDropPane;
    public TilePane serverView;
    public Label text;
    public AnchorPane person;
    public ImageView ava;
    public Label nickname;
    private Pane leftScene;
    private Desktop desktop = Desktop.getDesktop();
    private NettyNetwork network;
    private UploadFileMsg uploadFileMsg = new UploadFileMsg();
    private ClientUploadFileHandler clientUploadFileHandler;
    private String root = "client/clientFiles";
    private String sroot = "server/serverFiles";
    private int readByte;
    private volatile int start = 0;
    @FXML
    private GridPane setGrid;
    private List<Setting> settings;
    private Setting set;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settings = new ArrayList<>(data());
        int col = 0;
        int row = 1;
        try {
            for (int i = 0; i < settings.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("item.fxml"));
                HBox settingItem = fxmlLoader.load();
                ItemController itemController = fxmlLoader.getController();
                if (col == 3){
                    col =0;
                    ++row;
                }
                setGrid.add(settingItem, col++, row);
                GridPane.setMargin(settingItem, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        nickname.setUnderline(true);
        nickname.setText("Егорыч");


    }

    private List<Setting> data() {
        ItemController controller = new ItemController();
        File dir = new File(sroot); //path указывает на директорию
        File[] arrFiles = dir.listFiles();
        List<File> lst = Arrays.asList(arrFiles);
        List<Setting> ls = new ArrayList<>();
        Setting setting = new Setting();
        for (File file: arrFiles) {
            if (file.isFile()){
                setting.setFileViewSrc("client/src/main/resources/img/txt.png");
                setting.setFileName( file.getName().toString());
            }
        }
        ls.add(setting);

        return ls;
    }

    public void download(ActionEvent actionEvent) {
    }

    public void send(ActionEvent actionEvent) {

    }

    public void fileChoose(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете файл для отправки");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        File file = fileChooser.showOpenDialog(secondaryStage);

        if (file != null) {
            sendFile(file);
            List<File> files = Collections.singletonList(file);
            printLog(log_area, files);
        }
    }

    public void fileChooseM(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете файл для отправки");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        File file = fileChooser.showOpenDialog(secondaryStage);

        if (file != null) {
            sendFile(file);
            List<File> files = Collections.singletonList(file);
            printLog(log_area, files);
        }
    }


    private void sendFile(File file) {

        try {
            String fileMd5 = file.getName();
            uploadFileMsg.setFilePath(sroot);
            byte[] bytes = Files.readAllBytes(Paths.get(String.valueOf(file)));

            uploadFileMsg.setEnd(bytes.length);
            uploadFileMsg.setFile_md5(fileMd5);
            uploadFileMsg.setStart(0);
            uploadFileMsg.setFile(file);
            uploadFileMsg.setBytes(bytes);

            network = new NettyNetwork(uploadFileMsg);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDAnaD(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void dropped(DragEvent dragEvent) throws FileNotFoundException {
        List<File> files = dragEvent.getDragboard().getFiles();
        File file = files.get(0);
        if (file != null) {
            sendFile(file);
            files = Collections.singletonList(file);
            printLog(log_area, files);
        }

    }

    private void printLog(TextArea textArea, List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        for (File file : files) {
            textArea.appendText(file.getAbsolutePath() + "\n");
        }
    }

    public void hover(MouseEvent mouseEvent) {
        dragAndDropPane.setStyle("-fx-background-color: #F2F4F2;");
        text.setStyle("-fx-background-color: #F2F4F2;");

    }

    public void hoverBTN(MouseEvent mouseEvent) {
        download_btn.setStyle("-fx-background-color: #6abcd0;");

    }

    public void objectFree(MouseEvent mouseEvent) {
        dragAndDropPane.setStyle("-fx-background-color: #FFFFFF;");
        text.setStyle("-fx-background-color:#FFFFFF;");

    }

    public void BTNFree(MouseEvent mouseEvent) {
        download_btn.setStyle("-fx-background-color: #4b78bb;");

    }

    public void hoverSendBTN(MouseEvent mouseEvent) {
        upload_btn.setStyle("-fx-background-color: #6abcd0;");
        //upload_btn.setStyle( "-fx-effect: dropshadow(three-pass-box, rgba(106, 188, 208, 0.8), 10, 0, 0, 0);");

    }

    public void sendBTNFree(MouseEvent mouseEvent) {
        upload_btn.setStyle("-fx-background-color: #4b78bb;");
        //upload_btn.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(75, 120, 187, 0.8), 10, 0, 0, 0);");
    }
}
