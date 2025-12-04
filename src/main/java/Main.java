import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import model.SettingsManager;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            SettingsManager.load();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_menu.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setTitle("Petualangan Hijau");
            stage.setScene(scene);
            stage.setFullScreenExitHint("Tekan SHIFT+ESC untuk keluar dari mode layar penuh");
            stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("SHIFT+ESC"));
            stage.setFullScreen(true);
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
