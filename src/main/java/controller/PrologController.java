package controller;

import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import model.AudioManager;

/**
 * Menampilkan prolog dengan efek mesin tik bertahap sebelum game dimulai.
 */
public class PrologController {

    private static final List<String> PROLOG_SEGMENTS = List.of(
        """
        Indonesia, negeri yang kaya akan hutan tropis, lautan biru, dan kekayaan alam yang tak ternilai. Namun seiring berjalannya waktu, keindahan itu perlahan memudar.
        """,
        """
        Sampah mulai menumpuk di sudut kota, sungai-sungai tercemar, bahkan hutan yang dulu hijau kini meranggas. Ulah manusia yang tidak bertanggung jawab membuat tanah air ini rentan terhadap banjir dan longsor.
        """,
        """
        Dalam kekacauan itu, kamu muncul membawa harapan. Perubahan besar dimulai dari satu langkah kecil: membersihkan sampah dan menanam pohon di tiap daerah yang rusak.
        """,
        """
        Setiap sampah yang kamu kumpulkan berarti selangkah menuju Indonesia yang bersih. Setiap pohon yang kamu tanam adalah harapan baru bagi masa depan.
        """,
        """
        Inilah Petualangan Hijau: perjalanan menyelamatkan negeri, satu sampah dan satu pohon pada satu waktu.
        """
    );

    private static final double TYPE_INTERVAL_MS = 60;

    @FXML
    private AnchorPane root;

    @FXML
    private Label prologLabel;

    @FXML
    private Label hintLabel;

    private Timeline typingTimeline;
    private int typedCharCount;
    private boolean segmentFinished;
    private int currentSegmentIndex;

    @FXML
    public void initialize() {
        AudioManager.playMenuMusic();
        prologLabel.setText("");
        hintLabel.setText("");
        startTypingSegment();
        attachInputHandlers();
    }

    private void startTypingSegment() {
        stopTimeline();
        typedCharCount = 0;
        segmentFinished = false;
        prologLabel.setText("");
        hintLabel.setText("");

        typingTimeline = new Timeline(new KeyFrame(Duration.millis(TYPE_INTERVAL_MS), event -> typeNextCharacter()));
        typingTimeline.setCycleCount(Timeline.INDEFINITE);
        typingTimeline.play();
    }

    private void typeNextCharacter() {
        String currentSegment = PROLOG_SEGMENTS.get(currentSegmentIndex);
        if (typedCharCount < currentSegment.length()) {
            typedCharCount++;
            prologLabel.setText(currentSegment.substring(0, typedCharCount));
        } else {
            finishSegment(currentSegment);
        }
    }

    private void finishSegment(String fullText) {
        if (segmentFinished) {
            return;
        }
        segmentFinished = true;
        stopTimeline();
        prologLabel.setText(fullText);

        if (currentSegmentIndex < PROLOG_SEGMENTS.size() - 1) {
            hintLabel.setText("Tekan SPACE atau klik untuk melanjutkan cerita");
        } else {
            hintLabel.setText("Tekan SPACE atau klik untuk memulai Petualangan Hijau");
        }
    }

    private void stopTimeline() {
        if (typingTimeline != null) {
            typingTimeline.stop();
        }
    }

    private void skipTyping() {
        String currentSegment = PROLOG_SEGMENTS.get(currentSegmentIndex);
        typedCharCount = currentSegment.length();
        finishSegment(currentSegment);
    }

    private void attachInputHandlers() {
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPress);
                newScene.setOnMouseClicked(event -> proceedIfReady());
            }
        });
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            proceedIfReady();
        } else if (event.getCode() == KeyCode.ENTER && !segmentFinished) {
            skipTyping();
        }
    }

    private void proceedIfReady() {
        if (!segmentFinished) {
            skipTyping();
            return;
        }

        if (currentSegmentIndex < PROLOG_SEGMENTS.size() - 1) {
            currentSegmentIndex++;
            startTypingSegment();
        } else {
            loadGameScene();
        }
    }

    private void loadGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/game.fxml"));
            Scene scene = root.getScene();
            scene.setRoot(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

