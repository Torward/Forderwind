import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CloudApp extends Application {
    private Pane leftScene;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("cSfirstScreen.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("FORDERWIND");
        primaryStage.getIcons().add(
                new Image(
                        CloudApp.class.getResourceAsStream( "img/Icon.png" )));
        primaryStage.show();
    }
}
