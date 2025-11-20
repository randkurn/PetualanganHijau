import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/game.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setTitle("Petualangan Hijau");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Gagal memuat game.fxml!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
