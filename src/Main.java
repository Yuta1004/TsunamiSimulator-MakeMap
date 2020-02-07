import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import controller.MainUIController;

public class Main extends Application {

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Window Size
        Rectangle2D d = Screen.getPrimary().getVisualBounds();
        int width = (int)Math.min(1280, d.getWidth());
        int height = (int)Math.min(720, d.getHeight());

        // Scene
        Scene scene = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainUI.fxml"));
            loader.setController(new MainUIController());
            scene = new Scene(loader.load(), width, height);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Stage
        stage.setTitle("Tsunami Simulator - Make Map");
        stage.setScene(scene);
        stage.show();
    }

}
