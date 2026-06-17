package com.biodataouh;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("OrangUtan Haven - Database Konservasi");
        // ── PERUBAHAN: Tampilkan splash screen dengan app-logo.png saat aplikasi pertama dibuka ──
        tampilkanSplashScreen(primaryStage);
    }

    // ── BARU: Splash screen premium dengan animasi interaktif ───────────────
    private void tampilkanSplashScreen(Stage mainStage) {
        Stage splashStage = new Stage();
        splashStage.initStyle(StageStyle.TRANSPARENT);
        splashStage.setAlwaysOnTop(true);

        // Root splash
        StackPane splashRoot = new StackPane();
        splashRoot.setPrefSize(520, 340);
        splashRoot.setStyle("-fx-background-color: transparent;");

        // Backdrop glass card
        VBox card = new VBox(22);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(520, 340);
        card.setPadding(new javafx.geometry.Insets(40, 50, 36, 50));
        card.setStyle(
            "-fx-background-color: rgba(9,9,11,0.92); " +
            "-fx-background-radius: 28; " +
            "-fx-border-color: rgba(255,255,255,0.10); " +
            "-fx-border-radius: 28; " +
            "-fx-border-width: 1.5;"
        );
        DropShadow cardGlow = new DropShadow(60, Color.web("#EA580C", 0.30));
        card.setEffect(cardGlow);

        // Partikel latar (lingkaran dekoratif)
        StackPane particleLayer = new StackPane();
        particleLayer.setPrefSize(520, 340);
        particleLayer.setMouseTransparent(true);
        for (int i = 0; i < 7; i++) {
            Circle c = new Circle(3 + (i % 3) * 2);
            c.setFill(Color.web("#EA580C", 0.18 + i * 0.04));
            double[] tx = {80, 420, 60, 460, 200, 350, 260};
            double[] ty = {60, 80, 260, 240, 30, 300, 170};
            c.setTranslateX(tx[i] - 260);
            c.setTranslateY(ty[i] - 170);
            // Animasi float tiap partikel
            TranslateTransition float_p = new TranslateTransition(Duration.seconds(2.5 + i * 0.4), c);
            float_p.setFromY(c.getTranslateY());
            float_p.setToY(c.getTranslateY() - 12 - i * 2);
            float_p.setAutoReverse(true);
            float_p.setCycleCount(Animation.INDEFINITE);
            float_p.setInterpolator(Interpolator.EASE_BOTH);
            float_p.play();
            particleLayer.getChildren().add(c);
        }

        // Logo utama
        ImageView logoSplash = new ImageView();
        try {
            logoSplash.setImage(new Image(Main.class.getResourceAsStream("/com/biodataouh/assets/app-logo.png")));
            logoSplash.setFitWidth(110); logoSplash.setPreserveRatio(true);
        } catch (Exception e) {
            // Fallback: teks logo jika file belum ada
            logoSplash.setImage(new Image("https://placehold.co/110x110/ea580c/ffffff?text=OUH"));
            logoSplash.setFitWidth(110); logoSplash.setPreserveRatio(true);
        }
        // Clip bulat pada logo
        Circle logoClip = new Circle(55, 55, 55);
        logoSplash.setClip(logoClip);

        // Glow animasi pada logo
        DropShadow logoGlow = new DropShadow(0, Color.web("#EA580C", 0.6));
        logoSplash.setEffect(logoGlow);
        Timeline glowPulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(logoGlow.radiusProperty(), 0)),
            new KeyFrame(Duration.millis(900), new KeyValue(logoGlow.radiusProperty(), 28)),
            new KeyFrame(Duration.millis(1800), new KeyValue(logoGlow.radiusProperty(), 0))
        );
        glowPulse.setCycleCount(Animation.INDEFINITE);
        glowPulse.play();

        // Rotasi halus logo
        RotateTransition logoSpin = new RotateTransition(Duration.seconds(18), logoSplash);
        logoSpin.setFromAngle(-4); logoSpin.setToAngle(4);
        logoSpin.setAutoReverse(true); logoSpin.setCycleCount(Animation.INDEFINITE);
        logoSpin.setInterpolator(Interpolator.EASE_BOTH);
        logoSpin.play();

        Label lblApp = new Label("OrangUtan Haven");
        lblApp.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        lblApp.setTextFill(Color.web("#EA580C"));

        Label lblTagline = new Label("Database Konservasi Satwa Primata Indonesia");
        lblTagline.setFont(Font.font("SF Pro Text", 13));
        lblTagline.setTextFill(Color.web("#64748B"));

        // Progress bar splash
        ProgressBar pbSplash = new ProgressBar(0);
        pbSplash.setPrefWidth(300);
        pbSplash.setPrefHeight(5);
        pbSplash.setStyle("-fx-accent: #EA580C; -fx-background-color: rgba(234,88,12,0.15); -fx-background-radius: 4; -fx-border-radius: 4;");

        // ── Label loading dengan efek mengetik + mengambang ──
        Label lblLoading = new Label("");
        lblLoading.setFont(Font.font("SF Pro Text", FontWeight.BOLD, 12));
        lblLoading.setTextFill(Color.web("#EA580C"));
        lblLoading.setMinHeight(18);

        // Animasi mengambang pada label loading
        TranslateTransition floatLoading = new TranslateTransition(Duration.millis(1200), lblLoading);
        floatLoading.setFromY(0); floatLoading.setToY(-6);
        floatLoading.setAutoReverse(true); floatLoading.setCycleCount(Animation.INDEFINITE);
        floatLoading.setInterpolator(Interpolator.EASE_BOTH);
        floatLoading.play();

        card.getChildren().addAll(logoSplash, lblApp, lblTagline, pbSplash, lblLoading);
        splashRoot.getChildren().addAll(particleLayer, card);

        Scene splashScene = new Scene(splashRoot, 520, 340);
        splashScene.setFill(Color.TRANSPARENT);
        splashStage.setScene(splashScene);

        // Posisi tengah layar
        splashStage.centerOnScreen();

        // Animasi masuk splash
        card.setOpacity(0);
        card.setScaleX(0.88); card.setScaleY(0.88);
        splashStage.show();

        ScaleTransition stIn = new ScaleTransition(Duration.millis(480), card);
        stIn.setToX(1.0); stIn.setToY(1.0); stIn.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ftIn = new FadeTransition(Duration.millis(480), card);
        ftIn.setToValue(1.0);
        new ParallelTransition(stIn, ftIn).play();

        // ── Animasi mengetik: setiap karakter muncul satu per satu, lalu ganti ke teks berikutnya ──
        String[] pesanLoading = {
            "Memuat sistem...",
            "Menghubungkan database...",
            "Menyiapkan antarmuka...",
            "Hampir selesai..."
        };

        // Total durasi tiap fase mengetik: ~500ms untuk ketik + 200ms jeda = 700ms per pesan
        // Total 4 pesan × 700ms ≈ 2800ms, diselaraskan dengan progress bar
        Timeline ketikAnim = new Timeline();
        int waktuSaatIni = 0;
        for (String pesan : pesanLoading) {
            // Kosongkan dulu sebelum mulai mengetik pesan baru
            final int mulai = waktuSaatIni;
            ketikAnim.getKeyFrames().add(new KeyFrame(Duration.millis(mulai), e -> lblLoading.setText("")));
            // Tambahkan karakter satu per satu
            int ketikInterval = 38; // ms per karakter
            for (int ci = 1; ci <= pesan.length(); ci++) {
                final String potongan = pesan.substring(0, ci);
                int waktuKarakter = mulai + ci * ketikInterval;
                ketikAnim.getKeyFrames().add(new KeyFrame(Duration.millis(waktuKarakter), e -> lblLoading.setText(potongan)));
            }
            waktuSaatIni += pesan.length() * ketikInterval + 250; // jeda antar pesan
        }
        ketikAnim.play();

        // Animasi progress bar selama 2 detik (sedikit diperpanjang mengikuti animasi ketik)
        Timeline progressAnim = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(pbSplash.progressProperty(), 0.0)),
            new KeyFrame(Duration.millis(2000), new KeyValue(pbSplash.progressProperty(), 1.0, Interpolator.EASE_OUT))
        );
        progressAnim.play();

        progressAnim.setOnFinished(ev -> {
            // Animasi keluar splash
            ScaleTransition stOut = new ScaleTransition(Duration.millis(360), card);
            stOut.setToX(1.06); stOut.setToY(1.06);
            FadeTransition ftOut = new FadeTransition(Duration.millis(360), card);
            ftOut.setToValue(0.0);
            ParallelTransition ptOut = new ParallelTransition(stOut, ftOut);
            ptOut.setOnFinished(e2 -> {
                glowPulse.stop();
                splashStage.close();
                // ── Buka dashboard utama setelah splash selesai ──
                javafx.application.Platform.runLater(() -> DashboardPage.tampilkan(mainStage, "user"));
            });
            ptOut.play();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}