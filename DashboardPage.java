package com.biodataouh;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DashboardPage {

    // ── Root scene adalah StackPane permanen agar overlay tidak konflik ──────
    private static StackPane sceneRoot = new StackPane();
    private static BorderPane rootUtama = new BorderPane();

    private static TilePane gridKontenData = new TilePane();
    private static ScrollPane scrollPaneUtama = new ScrollPane();
    private static VBox areaKontenKatalog = new VBox(30);
    private static TextField txtCari = new TextField();

    private static String currentRole = "user";
    // ── Tema terang sebagai default utama ──
    private static boolean isDarkMode = false;
    private static Stage mainStage;
    private static ObservableList<Orangutan> semuaDataMaster = FXCollections.observableArrayList();
    private static String pathFotoTerpilih = "";

    private static final String FONT_PREMIUM = "SF Pro Text";
    private static final String FONT_PREMIUM_DISPLAY = "SF Pro Display";

    private static final String DARK_GRADIENT =
        "-fx-background-color: linear-gradient(to bottom right, #09090B, #18181B, #451A03);";
    private static final String LIGHT_GRADIENT =
        "-fx-background-color: linear-gradient(to bottom right, #FFFFFF, #FFEDD5, #FED7AA);";

    private static HBox topNavbar = new HBox(30);

    private static double targetScrollValue = 0;
    private static Timeline smoothScrollTimeline;

    // ── [BARU] Tracking halaman yang sedang aktif agar bisa di-refresh saat ganti tema ──
    // Nilai: "katalog" | "tentang" | "developer" | "profil" | "login" | "admin" | "form-ou" | "form-dev"
    private static String halamanAktif = "katalog";
    // Simpan object orangutan terakhir yang dibuka (untuk refresh profil)
    private static Orangutan orangutanProfilAktif = null;

    // ── [GROQ LIVECHAT] API Key & Model ──
    private static String groqApiKey   = "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";
    private static String groqApiUrl   = "https://api.groq.com/openai/v1/chat/completions";
    private static String groqModel    = "meta-llama/llama-4-scout-17b-16e-instruct";

    // ── Getter / Setter untuk diakses DashboardAdminPage (menu Pengaturan) ──
    public static String getGroqApiKey()  { return groqApiKey; }
    public static String getGroqApiUrl()  { return groqApiUrl; }
    public static String getGroqModel()   { return groqModel;  }
    public static void   setGroqApiKey(String v)  { groqApiKey  = v != null ? v : groqApiKey;  }
    public static void   setGroqApiUrl(String v)  { groqApiUrl  = v != null ? v : groqApiUrl;  }
    public static void   setGroqModel(String v)   { groqModel   = v != null ? v : groqModel;   }

    // ── Warna teks sesuai tema ────────────────────────────────────────────────
    private static String warnaTeks()    { return isDarkMode ? "white"   : "#0F172A"; }
    private static String warnaSub()     { return isDarkMode ? "#94A3B8" : "#475569"; }
    private static String warnaBorder()  { return isDarkMode ? "rgba(255,255,255,0.10)" : "rgba(234,88,12,0.20)"; }
    private static String warnaCard()    {
        return isDarkMode
            ? "-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 16; -fx-border-color: rgba(255,255,255,0.10); -fx-border-radius: 16; -fx-border-width: 1.5;"
            : "-fx-background-color: rgba(255,255,255,0.82); -fx-background-radius: 16; -fx-border-color: rgba(234,88,12,0.18); -fx-border-radius: 16; -fx-border-width: 1.5;";
    }

    public static void tampilkan(Stage stage, String role) {
        mainStage = stage;
        currentRole = role;

        semuaDataMaster = DatabaseManager.ambilSemuaOrangutan();

        // ── Bersihkan sceneRoot dan isi ulang dengan rootUtama ──
        sceneRoot.getChildren().clear();
        sceneRoot.getChildren().add(rootUtama);

        rootUtama.setStyle(isDarkMode ? DARK_GRADIENT : LIGHT_GRADIENT);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), rootUtama);
        fadeIn.setFromValue(0.0); fadeIn.setToValue(1.0); fadeIn.play();

        bangunNavbar();
        rootUtama.setTop(topNavbar);

        konfigurasiSmoothScroll(scrollPaneUtama);

        if (currentRole.equalsIgnoreCase("admin")) {
            halamanAktif = "admin";
            topNavbar.getChildren().clear();

            Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
            Button btnLogoutAdmin = new Button("Keluar Admin");
            btnLogoutAdmin.setStyle(
                "-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 25;" +
                "-fx-padding: 10 24; -fx-cursor: hand; -fx-font-family: '" + FONT_PREMIUM + "'; -fx-font-weight: bold;");
            btnLogoutAdmin.setOnAction(e -> { currentRole = "user"; tampilkan(mainStage, "user"); });

            ImageView logoAdminNav = new ImageView();
            try {
                logoAdminNav.setImage(new Image(DashboardPage.class.getResourceAsStream("/com/biodataouh/assets/logo.png")));
                logoAdminNav.setFitHeight(36); logoAdminNav.setPreserveRatio(true);
            } catch (Exception ex) {
                Label fallbackAdmin = new Label("🌿 ORANGUTAN HAVEN");
                fallbackAdmin.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BLACK, 20));
                fallbackAdmin.setTextFill(Color.web("#EA580C"));
                topNavbar.getChildren().add(fallbackAdmin);
            }

            // ── Tombol toggle tema juga ada di navbar admin ──
            Button btnToggleTemaAdmin = buatTombolToggleTema();

            if (logoAdminNav.getImage() != null) topNavbar.getChildren().add(logoAdminNav);
            topNavbar.getChildren().addAll(spacer, btnToggleTemaAdmin, btnLogoutAdmin);

            semuaDataMaster = DatabaseManager.ambilSemuaOrangutan();

            VBox adminContentRaw = DashboardAdminPage.dapatkanTampilan(isDarkMode, () -> tampilkan(mainStage, "admin"));

            ScrollPane wadahScrollAdmin = new ScrollPane(adminContentRaw);
            wadahScrollAdmin.setFitToWidth(true);
            wadahScrollAdmin.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            wadahScrollAdmin.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            wadahScrollAdmin.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent; -fx-viewport-background: transparent;");

            switchCenterContent(wadahScrollAdmin);
        } else {
            tampilkanKatalogUtama();
        }

        rootUtama.setStyle(isDarkMode ? DARK_GRADIENT : LIGHT_GRADIENT);
        scrollPaneUtama.setStyle(styleScrollPane());
        gridKontenData.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        areaKontenKatalog.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // ── Scene menggunakan sceneRoot (StackPane permanen) ──
        if (stage.getScene() == null || stage.getScene().getRoot() != sceneRoot) {
            Scene scene = new Scene(sceneRoot, 1300, 850);
            terapkanStylesheetScene(scene);
            scene.setFill(isDarkMode ? Color.web("#09090B") : Color.web("#FFFFFF"));
            stage.setScene(scene);
        } else {
            stage.getScene().setFill(isDarkMode ? Color.web("#09090B") : Color.web("#FFFFFF"));
            // Pastikan stylesheet tema selalu sinkron saat re-render halaman
            perbaruiStylesheetTema(stage.getScene());
        }

        // ── Pasang FAB LiveChat AI setelah layout siap ──
        pasangFabLiveChat();

        stage.show();
    }

    // ── Getter stage untuk diakses DashboardAdminPage ────────────────────────
    public static Stage getMainStage() { return mainStage; }

    // ── Terapkan stylesheet global sekali ────────────────────────────────────
    private static void terapkanStylesheetScene(Scene scene) {
        scene.getStylesheets().add("data:text/css," +
            ".scroll-pane { -fx-background-color: transparent !important; -fx-background: transparent !important;" +
            "  -fx-border-color: transparent !important; -fx-padding: 0 !important; }" +
            ".scroll-pane > .viewport { -fx-background-color: transparent !important; -fx-background: transparent !important; }" +
            ".scroll-pane > .corner { -fx-background-color: transparent !important; }" +
            ".scroll-pane .scroll-bar:horizontal, .scroll-pane .scroll-bar:vertical {" +
            "  -fx-background-color: transparent !important; -fx-background: transparent !important; }" +
            ".scroll-pane .scroll-bar .track, .scroll-pane .scroll-bar .thumb," +
            ".scroll-pane .scroll-bar .increment-button, .scroll-pane .scroll-bar .decrement-button {" +
            "  -fx-background-color: transparent !important; -fx-background: transparent !important;" +
            "  -fx-border-color: transparent !important; }"
        );
        // Paksa warna teks global sesuai tema aktif
        perbaruiStylesheetTema(scene);
    }

    // ── Paksa warna teks Label/field global sesuai tema — dipanggil tiap kali tema berubah ──
    // Ini mengatasi JavaFX Modena yang mewarisi teks putih/samar pada background warm/peach.
    private static void perbaruiStylesheetTema(Scene scene) {
        if (scene == null) return;
        // Hapus stylesheet tema lama
        scene.getStylesheets().removeIf(s -> s.contains("tema-teks-override"));
        String warnaTeksCss  = isDarkMode ? "white"   : "#0F172A";
        String warnaSubCss   = isDarkMode ? "#94A3B8" : "#334155";
        scene.getStylesheets().add("data:text/css," +
            "/* tema-teks-override */" +
            ".label { -fx-text-fill: " + warnaTeksCss + "; }" +
            ".hyperlink { -fx-text-fill: " + warnaSubCss + "; -fx-underline: false; }" +
            ".text-field { -fx-text-fill: " + warnaTeksCss + "; }" +
            ".password-field { -fx-text-fill: " + warnaTeksCss + "; }" +
            ".text-area { -fx-text-fill: " + warnaTeksCss + "; }" +
            ".text-area .content { -fx-background-color: " + (isDarkMode ? "#1E293B" : "white") + "; }" +
            ".separator *.line { -fx-border-color: " + (isDarkMode ? "rgba(255,255,255,0.10)" : "rgba(15,23,42,0.12)") + "; }" +
            ".combo-box .list-cell { -fx-text-fill: " + warnaTeksCss + "; }"
        );
    }

    private static String styleScrollPane() {
        return "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;" +
               "-fx-padding: 0; -fx-background-insets: 0; -fx-viewport-background: transparent; -fx-edge-to-edge: true;";
    }

    // ── Toggle tema tanpa bug scene-root loop ─────────────────────────────────
    private static Button buatTombolToggleTema() {
        Button btnToggle = new Button();
        FontIcon iconTema = new FontIcon(isDarkMode ? "fas-sun" : "fas-moon");
        iconTema.setIconColor(isDarkMode ? Color.web("#FBBF24") : Color.web("#7C2D12"));
        iconTema.setIconSize(16);
        btnToggle.setGraphic(iconTema);
        btnToggle.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-focus-color: transparent;");
        btnToggle.setOnAction(e -> tampilkanAnimasiGantiTema());
        return btnToggle;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ANIMASI GANTI TEMA — fade-in overlay → ubah tema → fade-out overlay
    // ════════════════════════════════════════════════════════════════════════
    private static void tampilkanAnimasiGantiTema() {
        if (!sceneRoot.getChildren().contains(rootUtama)) {
            sceneRoot.getChildren().clear();
            sceneRoot.getChildren().add(rootUtama);
        }

        // ── OVERLAY: penutup penuh semi-transparan ────────────────────────────
        StackPane overlay = new StackPane();
        overlay.setStyle(!isDarkMode
            ? "-fx-background-color: rgba(9,9,11,0.65);"
            : "-fx-background-color: rgba(255,255,255,0.70);");
        overlay.setMouseTransparent(true);
        overlay.setOpacity(0);
        sceneRoot.getChildren().add(overlay);

        // ── FASE 1: Fade-in overlay (220ms) ──────────────────────────────────
        FadeTransition ftOverlayIn = new FadeTransition(Duration.millis(220), overlay);
        ftOverlayIn.setFromValue(0.0);
        ftOverlayIn.setToValue(1.0);
        ftOverlayIn.setInterpolator(Interpolator.EASE_IN);

        ftOverlayIn.setOnFinished(evIn -> {
            // ── FASE 2: Terapkan tema baru DI BALIK overlay ──────────────────
            isDarkMode = !isDarkMode;

            // Update background rootUtama
            rootUtama.setStyle(isDarkMode ? DARK_GRADIENT : LIGHT_GRADIENT);

            // Update stylesheet warna teks global sesuai tema baru
            if (mainStage.getScene() != null)
                perbaruiStylesheetTema(mainStage.getScene());

            // Refresh semua konten halaman agar ikut tema baru
            refreshKontenSetelahGantiTema();

            // Update warna scene
            if (mainStage.getScene() != null)
                mainStage.getScene().setFill(isDarkMode ? Color.web("#09090B") : Color.web("#FFFFFF"));

            // ── FASE 3: Fade-out overlay (300ms) ─────────────────────────────
            FadeTransition ftOverlayOut = new FadeTransition(Duration.millis(300), overlay);
            ftOverlayOut.setFromValue(1.0);
            ftOverlayOut.setToValue(0.0);
            ftOverlayOut.setInterpolator(Interpolator.EASE_OUT);
            ftOverlayOut.setOnFinished(evOut -> {
                sceneRoot.getChildren().remove(overlay);
                pasangFabLiveChat();
            });
            ftOverlayOut.play();
        });

        ftOverlayIn.play();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REFRESH KONTEN HALAMAN — dipanggil di balik overlay saat tema sudah berubah
    // Semua halaman yang mungkin sedang aktif akan di-rebuild dengan tema baru
    // ════════════════════════════════════════════════════════════════════════
    private static void refreshKontenSetelahGantiTema() {
        // 1. Rebuild navbar dengan warna tema baru
        if (currentRole.equalsIgnoreCase("admin")) {
            // Rebuild navbar admin
            topNavbar.getChildren().clear();
            topNavbar.setAlignment(Pos.CENTER_LEFT);
            topNavbar.setPadding(new Insets(20, 50, 20, 50));
            topNavbar.setStyle(isDarkMode
                ? "-fx-background-color: rgba(24,24,27,0.6); -fx-border-color: rgba(255,255,255,0.06); -fx-border-width: 0 0 1 0;"
                : "-fx-background-color: rgba(255,255,255,0.55); -fx-border-color: rgba(15,23,42,0.10); -fx-border-width: 0 0 1 0;");

            Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
            Button btnLogoutAdmin = new Button("Keluar Admin");
            btnLogoutAdmin.setStyle(
                "-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 25;" +
                "-fx-padding: 10 24; -fx-cursor: hand; -fx-font-family: '" + FONT_PREMIUM + "'; -fx-font-weight: bold;");
            btnLogoutAdmin.setOnAction(e -> { currentRole = "user"; tampilkan(mainStage, "user"); });

            ImageView logoAdminNav = new ImageView();
            try {
                logoAdminNav.setImage(new Image(DashboardPage.class.getResourceAsStream("/com/biodataouh/assets/logo.png")));
                logoAdminNav.setFitHeight(36); logoAdminNav.setPreserveRatio(true);
            } catch (Exception ex) {
                Label fallbackAdmin = new Label("🌿 ORANGUTAN HAVEN");
                fallbackAdmin.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BLACK, 20));
                fallbackAdmin.setTextFill(Color.web("#EA580C"));
                topNavbar.getChildren().add(fallbackAdmin);
            }
            Button btnToggleTemaAdmin = buatTombolToggleTema();
            if (logoAdminNav.getImage() != null) topNavbar.getChildren().add(logoAdminNav);
            topNavbar.getChildren().addAll(spacer, btnToggleTemaAdmin, btnLogoutAdmin);

            // Rebuild konten admin dengan tema baru
            VBox adminContentRaw = DashboardAdminPage.dapatkanTampilan(isDarkMode, () -> tampilkan(mainStage, "admin"));
            ScrollPane wadahScrollAdmin = new ScrollPane(adminContentRaw);
            wadahScrollAdmin.setFitToWidth(true);
            wadahScrollAdmin.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            wadahScrollAdmin.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            wadahScrollAdmin.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
            rootUtama.setCenter(wadahScrollAdmin);

        } else {
            // Rebuild navbar user
            bangunNavbar();
            rootUtama.setTop(topNavbar);

            // Rebuild konten sesuai halaman yang sedang aktif
            switch (halamanAktif) {
                case "katalog" -> {
                    // Rebuild area katalog langsung tanpa switchCenterContent (overlay sudah menutup)
                    areaKontenKatalog.getChildren().clear();
                    areaKontenKatalog.setPadding(new Insets(40, 50, 40, 50));
                    areaKontenKatalog.setStyle("-fx-background-color: transparent;");

                    Label lblJudul = new Label("🌿 Jelajahi Konservasi Orangutan");
                    lblJudul.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BLACK, 28));
                    lblJudul.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));

                    StackPane frameCari = new StackPane();
                    String teksCariBefore = txtCari.getText() != null ? txtCari.getText() : "";
                    txtCari = new TextField();
                    txtCari.setPromptText("Cari nama orangutan...");
                    txtCari.setPrefWidth(350);
                    txtCari.setStyle(isDarkMode
                        ? "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-background-radius: 30;" +
                          "-fx-padding: 12 45 12 20; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 30;"
                        : "-fx-background-color: white; -fx-text-fill: #0F172A; -fx-background-radius: 30;" +
                          "-fx-padding: 12 45 12 20; -fx-border-color: #EA580C; -fx-border-radius: 30;");
                    txtCari.setText(teksCariBefore);
                    txtCari.textProperty().addListener((obs, l, b) -> saringDanSegarkanGrid(b));

                    FontIcon icoSearch = new FontIcon("fas-search");
                    icoSearch.setIconColor(Color.web(isDarkMode ? "#64748B" : "#EA580C"));
                    StackPane.setAlignment(icoSearch, Pos.CENTER_RIGHT);
                    StackPane.setMargin(icoSearch, new Insets(0, 20, 0, 0));
                    frameCari.getChildren().addAll(txtCari, icoSearch);

                    HBox hbHeader = new HBox(lblJudul, new Region(), frameCari);
                    HBox.setHgrow(hbHeader.getChildren().get(1), Priority.ALWAYS);
                    hbHeader.setAlignment(Pos.CENTER_LEFT);

                    gridKontenData.setHgap(30); gridKontenData.setVgap(35);
                    gridKontenData.setStyle("-fx-background-color: transparent;");

                    scrollPaneUtama.setContent(gridKontenData);
                    scrollPaneUtama.setFitToWidth(true);
                    scrollPaneUtama.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                    scrollPaneUtama.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                    scrollPaneUtama.setStyle(styleScrollPane());

                    areaKontenKatalog.getChildren().addAll(hbHeader, scrollPaneUtama);
                    rootUtama.setCenter(areaKontenKatalog);
                    saringDanSegarkanGrid(teksCariBefore);
                }
                case "tentang" -> tampilkanHalamanTentangInternal(false);
                case "developer" -> tampilkanHalamanDeveloperInternal(false);
                case "profil" -> {
                    if (orangutanProfilAktif != null) bukaHalamanProfilPenuhInternal(orangutanProfilAktif, false);
                    else tampilkanKatalogUtama();
                }
                case "login" -> {
                    // Login page: re-render dengan tema baru
                    tampilkanHalamanLoginInternal(false);
                }
                default -> {
                    // Fallback: kembali ke katalog
                    areaKontenKatalog.getChildren().clear();
                    areaKontenKatalog.setPadding(new Insets(40, 50, 40, 50));
                    areaKontenKatalog.setStyle("-fx-background-color: transparent;");
                    rootUtama.setCenter(areaKontenKatalog);
                    saringDanSegarkanGrid("");
                }
            }
        }

        // Update scroll pane & grid background
        scrollPaneUtama.setStyle(styleScrollPane());
        gridKontenData.setStyle("-fx-background-color: transparent;");
        areaKontenKatalog.setStyle("-fx-background-color: transparent;");
    }

    private static void bangunNavbar() {
        topNavbar.getChildren().clear();
        topNavbar.setAlignment(Pos.CENTER_LEFT);
        topNavbar.setPadding(new Insets(20, 50, 20, 50));
        topNavbar.setStyle(isDarkMode
            ? "-fx-background-color: rgba(24,24,27,0.6); -fx-border-color: rgba(255,255,255,0.06); -fx-border-width: 0 0 1 0;"
            : "-fx-background-color: rgba(255,255,255,0.55); -fx-border-color: rgba(15,23,42,0.10); -fx-border-width: 0 0 1 0;");

        ImageView logoApp = new ImageView();
        try {
            logoApp.setImage(new Image(DashboardPage.class.getResourceAsStream("/com/biodataouh/assets/logo.png")));
            logoApp.setFitHeight(36); logoApp.setPreserveRatio(true);
        } catch (Exception e) {
            Label fallback = new Label("🌿 ORANGUTAN HAVEN");
            fallback.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BLACK, 20));
            fallback.setTextFill(Color.web("#EA580C"));
            topNavbar.getChildren().add(fallback);
        }

        Region spacerNav = new Region(); HBox.setHgrow(spacerNav, Priority.ALWAYS);

        Hyperlink navBiodata  = createNavLink("Biodata Orangutan");
        Hyperlink navTentang  = createNavLink("Tentang");
        Hyperlink navDeveloper = createNavLink("Developer");

        navBiodata.setOnAction(e -> { if (!currentRole.equalsIgnoreCase("admin")) tampilkanKatalogUtama(); });
        navTentang.setOnAction(e -> tampilkanHalamanTentang());
        navDeveloper.setOnAction(e -> tampilkanHalamanDeveloperUtama());

        Button btnToggleTema = buatTombolToggleTema();

        Button btnAuth = new Button("Login Admin");
        btnAuth.setStyle(
            "-fx-background-color: #EA580C; -fx-text-fill: white; -fx-background-radius: 25;" +
            "-fx-padding: 10 24; -fx-cursor: hand; -fx-font-family: '" + FONT_PREMIUM + "'; -fx-font-weight: bold;");
        btnAuth.setOnAction(e -> tampilkanHalamanLogin());

        if (logoApp.getImage() != null) topNavbar.getChildren().add(logoApp);
        topNavbar.getChildren().addAll(navBiodata, navTentang, navDeveloper, spacerNav, btnToggleTema, btnAuth);
    }

    private static Hyperlink createNavLink(String t) {
        Hyperlink l = new Hyperlink(t);
        l.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 14));
        String defColor = isDarkMode ? "#94A3B8" : "#0F172A";
        String hovColor = isDarkMode ? "#FFFFFF" : "#EA580C";
        l.setStyle("-fx-text-fill: " + defColor + "; -fx-underline: false; -fx-focus-color: transparent; -fx-padding: 0 0 5 0;");
        l.setOnMouseEntered(e -> l.setStyle("-fx-text-fill: " + hovColor + "; -fx-underline: false; -fx-border-width: 0 0 2 0; -fx-border-color: " + hovColor + "; -fx-padding: 0 0 5 0;"));
        l.setOnMouseExited(e  -> l.setStyle("-fx-text-fill: " + defColor + "; -fx-underline: false; -fx-border-color: transparent; -fx-padding: 0 0 5 0;"));
        return l;
    }

    // ── Katalog utama ─────────────────────────────────────────────────────────
    public static void tampilkanKatalogUtama() {
        halamanAktif = "katalog";
        areaKontenKatalog.getChildren().clear();
        areaKontenKatalog.setPadding(new Insets(40, 50, 40, 50));
        areaKontenKatalog.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        Label lblJudul = new Label("🌿 Jelajahi Konservasi Orangutan");
        lblJudul.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BLACK, 28));
        lblJudul.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));

        StackPane frameCari = new StackPane();
        txtCari.setPromptText("Cari nama orangutan...");
        txtCari.setPrefWidth(350);
        txtCari.setStyle(isDarkMode
            ? "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-background-radius: 30;" +
              "-fx-padding: 12 45 12 20; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 30;"
            : "-fx-background-color: white; -fx-text-fill: #0F172A; -fx-background-radius: 30;" +
              "-fx-padding: 12 45 12 20; -fx-border-color: #EA580C; -fx-border-radius: 30;");
        txtCari.textProperty().addListener((obs, l, b) -> saringDanSegarkanGrid(b));

        FontIcon icoSearch = new FontIcon("fas-search");
        icoSearch.setIconColor(Color.web(isDarkMode ? "#64748B" : "#EA580C"));
        StackPane.setAlignment(icoSearch, Pos.CENTER_RIGHT);
        StackPane.setMargin(icoSearch, new Insets(0, 20, 0, 0));
        frameCari.getChildren().addAll(txtCari, icoSearch);

        HBox hbHeader = new HBox(lblJudul, new Region(), frameCari);
        HBox.setHgrow(hbHeader.getChildren().get(1), Priority.ALWAYS);
        hbHeader.setAlignment(Pos.CENTER_LEFT);

        gridKontenData.setHgap(30); gridKontenData.setVgap(35);
        gridKontenData.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        scrollPaneUtama.setContent(gridKontenData);
        scrollPaneUtama.setFitToWidth(true);
        scrollPaneUtama.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneUtama.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneUtama.setStyle(styleScrollPane());

        areaKontenKatalog.getChildren().addAll(hbHeader, scrollPaneUtama);
        switchCenterContent(areaKontenKatalog);
        saringDanSegarkanGrid(txtCari.getText() != null ? txtCari.getText() : "");
    }

    private static void saringDanSegarkanGrid(String k) {
        gridKontenData.getChildren().clear();
        int jumlahData = 0;

        for (Orangutan ou : semuaDataMaster) {
            if (!k.isEmpty() && !ou.getNama().toLowerCase().contains(k.toLowerCase())) continue;
            jumlahData++;

            VBox card = new VBox(0);
            card.setPrefWidth(280);
            card.setStyle(isDarkMode
                ? "-fx-background-color: rgba(30,41,59,0.45); -fx-background-radius: 20; -fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 20;"
                : "-fx-background-color: rgba(255,255,255,0.80); -fx-background-radius: 20; -fx-border-color: rgba(234,88,12,0.20); -fx-border-radius: 20;");

            DropShadow shadow = new DropShadow(15, Color.web(isDarkMode ? "#000000" : "#EA580C", 0.10));
            card.setEffect(shadow);

            card.setOnMouseEntered(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), card); st.setToX(1.04); st.setToY(1.04); st.play();
                new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(shadow.radiusProperty(), 25))).play();
            });
            card.setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), card); st.setToX(1.0); st.setToY(1.0); st.play();
                new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(shadow.radiusProperty(), 15))).play();
            });

            StackPane frameFoto = new StackPane();
            ImageView fotoOU = new ImageView();
            fotoOU.setFitWidth(280); fotoOU.setFitHeight(180);
            try {
                File fileFoto = new File(ou.getFoto());
                if (fileFoto.exists()) fotoOU.setImage(new Image(fileFoto.toURI().toString(), 280, 180, false, true));
                else fotoOU.setImage(new Image(DashboardPage.class.getResourceAsStream(ou.getFoto()), 280, 180, false, true));
            } catch (Exception e) {
                fotoOU.setImage(new Image(isDarkMode
                    ? "https://placehold.co/280x180/1e293b/ffffff?text=Orangutan"
                    : "https://placehold.co/280x180/FED7AA/7C2D12?text=Orangutan"));
            }

            Rectangle clip = new Rectangle(280, 180); clip.setArcWidth(40); clip.setArcHeight(40);
            fotoOU.setClip(clip);

            Label badgeStatus = new Label(ou.getStatusKonservasi().toUpperCase());
            badgeStatus.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 10));
            badgeStatus.setTextFill(Color.WHITE);
            badgeStatus.setPadding(new Insets(6, 12, 6, 12));
            badgeStatus.setStyle("-fx-background-color: #EA580C; -fx-background-radius: 10;");
            StackPane.setAlignment(badgeStatus, Pos.TOP_RIGHT);
            StackPane.setMargin(badgeStatus, new Insets(12));
            frameFoto.getChildren().addAll(fotoOU, badgeStatus);

            VBox detailBox = new VBox(15); detailBox.setPadding(new Insets(20));

            Label lblNama = new Label(ou.getNama().toUpperCase());
            lblNama.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BLACK, 18));
            lblNama.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));

            HBox subDetailContainer = new HBox(5);
            subDetailContainer.setPadding(new Insets(8, 12, 8, 12));
            subDetailContainer.setAlignment(Pos.CENTER_LEFT);
            subDetailContainer.setStyle(isDarkMode
                ? "-fx-background-color: rgba(15,23,42,0.6); -fx-background-radius: 10; -fx-border-color: rgba(255,255,255,0.05); -fx-border-radius: 10;"
                : "-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 10; -fx-border-color: rgba(234,88,12,0.1); -fx-border-radius: 10;");

            Label lblSub = new Label("Umur: " + ou.getUmur() + " Tahun  •  " + ou.getJenisKelamin());
            lblSub.setTextFill(isDarkMode ? Color.web("#CBD5E1") : Color.web("#334155"));
            lblSub.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 12));
            subDetailContainer.getChildren().add(lblSub);

            Button btnProfil = new Button("LIHAT PROFIL LENGKAP");
            btnProfil.setMaxWidth(Double.MAX_VALUE);
            btnProfil.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 12));
            btnProfil.setStyle("-fx-background-color: transparent; -fx-border-color: #EA580C; -fx-border-width: 1.5;" +
                               "-fx-text-fill: #EA580C; -fx-padding: 10; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;");
            btnProfil.setOnAction(e -> tampilkanLoadingLaluBukaProfil(ou));

            detailBox.getChildren().addAll(lblNama, subDetailContainer, btnProfil);
            card.getChildren().addAll(frameFoto, detailBox);
            gridKontenData.getChildren().add(card);
        }

        if (jumlahData == 0) {
            StackPane containerKosong = new StackPane();
            containerKosong.setPadding(new Insets(100, 0, 100, 0));
            containerKosong.setStyle("-fx-background-color: transparent;");
            containerKosong.setPrefWidth(1100);

            Label lblKosong = new Label(k.isEmpty()
                ? "Belum ada data orangutan..."
                : "Biodata untuk \"" + k + "\" tidak ditemukan...");
            lblKosong.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 16));
            lblKosong.setTextFill(isDarkMode ? Color.web("#94A3B8") : Color.web("#475569"));
            lblKosong.setAlignment(Pos.CENTER);
            containerKosong.getChildren().add(lblKosong);

            TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(1.5), lblKosong);
            floatAnim.setFromY(0); floatAnim.setToY(-15);
            floatAnim.setAutoReverse(true); floatAnim.setCycleCount(Animation.INDEFINITE);
            floatAnim.setInterpolator(Interpolator.EASE_BOTH); floatAnim.play();
            gridKontenData.getChildren().add(containerKosong);
        }
    }

    private static void tampilkanLoadingLaluBukaProfil(Orangutan ou) {
        orangutanProfilAktif = ou;
        halamanAktif = "profil";

        VBox loadingLayout = new VBox();
        loadingLayout.setAlignment(Pos.CENTER);
        loadingLayout.setPadding(new Insets(150));
        loadingLayout.setStyle("-fx-background-color: transparent;");

        Label lblLoading = new Label("OrangUtanHaven...");
        lblLoading.setFont(Font.font("Georgia", FontWeight.BOLD, 36));
        lblLoading.setTextFill(Color.web("#EA580C"));
        loadingLayout.getChildren().add(lblLoading);

        FadeTransition ft = new FadeTransition(Duration.millis(400), lblLoading);
        ft.setFromValue(1.0); ft.setToValue(0.3);
        ft.setAutoReverse(true); ft.setCycleCount(4);

        switchCenterContent(loadingLayout);
        ft.play();
        ft.setOnFinished(e -> bukaHalamanProfilPenuh(ou));
    }

    private static void bukaHalamanProfilPenuh(Orangutan ou) {
        orangutanProfilAktif = ou;
        halamanAktif = "profil";
        bukaHalamanProfilPenuhInternal(ou, true);
    }

    // Internal: withTransition=false digunakan saat rebuild tema (overlay sudah menutup)
    private static void bukaHalamanProfilPenuhInternal(Orangutan ou, boolean withTransition) {
        VBox layoutProfilPenuh = new VBox(30);
        layoutProfilPenuh.setPadding(new Insets(40, 80, 40, 80));
        layoutProfilPenuh.setStyle("-fx-background-color: transparent;");

        Button btnKembali = new Button("← KEMBALI KE KATALOG");
        btnKembali.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 13));
        btnKembali.setStyle("-fx-background-color: transparent; -fx-text-fill: #EA580C; -fx-cursor: hand; -fx-padding: 0;");
        btnKembali.setOnAction(e -> tampilkanKatalogUtama());

        HBox rowAtasFoto = new HBox(30);
        rowAtasFoto.setAlignment(Pos.TOP_LEFT);

        ImageView fotoBesar = new ImageView();
        fotoBesar.setFitWidth(550); fotoBesar.setFitHeight(320);
        try {
            File f = new File(ou.getFoto());
            if (f.exists()) fotoBesar.setImage(new Image(f.toURI().toString()));
            else fotoBesar.setImage(new Image(DashboardPage.class.getResourceAsStream(ou.getFoto())));
        } catch (Exception e) {
            fotoBesar.setImage(new Image(isDarkMode
                    ? "https://placehold.co/550x320/1e293b/ffffff?text=Orangutan"
                    : "https://placehold.co/550x320/FED7AA/7C2D12?text=Orangutan"));
        }
        Rectangle clip = new Rectangle(550, 320); clip.setArcWidth(30); clip.setArcHeight(30);
        fotoBesar.setClip(clip);

        VBox detailKanan = new VBox(20);
        detailKanan.setAlignment(Pos.TOP_LEFT);

        Label namaOu = new Label(ou.getNama().toUpperCase());
        namaOu.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BLACK, 42));
        namaOu.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));

        Label statusBadge = new Label("STATUS: " + ou.getStatusKonservasi().toUpperCase());
        statusBadge.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 12));
        statusBadge.setTextFill(Color.WHITE);
        statusBadge.setPadding(new Insets(6, 14, 6, 14));
        statusBadge.setStyle("-fx-background-color: #EA580C; -fx-background-radius: 8;");

        GridPane gridBio = new GridPane(); gridBio.setHgap(40); gridBio.setVgap(20);
        gridBio.add(buatLabelMeta("SPESIES UTAMA",    ou.getSpesies()), 0, 0);
        gridBio.add(buatLabelMeta("LOKASI HABITAT",   ou.getLokasiHabitat()), 1, 0);
        gridBio.add(buatLabelMeta("BERAT BADAN",      ou.getBeratBadan() + " Kg"), 0, 1);
        gridBio.add(buatLabelMeta("TINGGI BADAN",     ou.getTinggiBadan() + " Cm"), 1, 1);
        gridBio.add(buatLabelMeta("UMUR ESTIMASI",    ou.getUmur() + " Tahun"), 0, 2);
        gridBio.add(buatLabelMeta("IDENTITAS GENDER", ou.getJenisKelamin()), 1, 2);

        detailKanan.getChildren().addAll(namaOu, statusBadge, new Separator(), gridBio);
        rowAtasFoto.getChildren().addAll(fotoBesar, detailKanan);

        VBox descBox = new VBox(12);
        Label lblDescTitle = new Label("DESKRIPSI LENGKAP & MONITORING MEDIS");
        lblDescTitle.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 13));
        lblDescTitle.setTextFill(isDarkMode ? Color.web("#94A3B8") : Color.web("#475569"));

        Label lblDescCont = new Label(ou.getDeskripsi());
        lblDescCont.setWrapText(true);
        lblDescCont.setFont(Font.font(FONT_PREMIUM, 16));
        lblDescCont.setLineSpacing(5);
        lblDescCont.setTextFill(isDarkMode ? Color.web("#CBD5E1") : Color.web("#0F172A"));
        descBox.getChildren().addAll(lblDescTitle, lblDescCont);

        layoutProfilPenuh.getChildren().addAll(btnKembali, rowAtasFoto, new Separator(), descBox);

        ScrollPane scrollProfil = new ScrollPane(layoutProfilPenuh);
        scrollProfil.setFitToWidth(true);
        scrollProfil.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollProfil.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollProfil.setStyle(styleScrollPane());
        konfigurasiSmoothScroll(scrollProfil);

        if (withTransition) switchCenterContent(scrollProfil);
        else rootUtama.setCenter(scrollProfil);
    }

    private static VBox buatLabelMeta(String k, String v) {
        Label lblK = new Label(k);
        lblK.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 11));
        lblK.setTextFill(isDarkMode ? Color.web("#94A3B8") : Color.web("#475569"));
        Label lblV = new Label(v);
        lblV.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 16));
        lblV.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));
        VBox meta = new VBox(5, lblK, lblV);
        meta.setStyle("-fx-background-color: transparent;");
        return meta;
    }

    // ── Halaman Tentang ───────────────────────────────────────────────────────
    private static void tampilkanHalamanTentang() {
        halamanAktif = "tentang";
        tampilkanHalamanTentangInternal(true);
    }

    private static void tampilkanHalamanTentangInternal(boolean withTransition) {
        VBox tLayout = new VBox(25); tLayout.setPadding(new Insets(50, 80, 50, 80));
        tLayout.setStyle("-fx-background-color: transparent;");

        Button b = new Button("← KEMBALI KE KATALOG");
        b.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 12));
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: #EA580C; -fx-cursor: hand; -fx-padding: 0;");
        b.setOnAction(e -> tampilkanKatalogUtama());

        Label j = new Label("🌿 Mengenal OrangUtanHaven");
        j.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BLACK, 28));
        j.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));

        Label d = new Label("OrangUtanHaven adalah sistem informasi biometrik terpadu yang didedikasikan untuk pencatatan, pemantauan, serta edukasi konservasi satwa langka Orangutan. Melalui platform ini, kami berupaya menyajikan keterbukaan data riwayat hidup satwa, status perlindungan, dan penyebaran habitat guna mendukung aksi penyelamatan primata endemik Indonesia dari ancaman kepunahan kritis.");
        d.setWrapText(true); d.setFont(Font.font(FONT_PREMIUM, 16)); d.setLineSpacing(6);
        d.setTextFill(isDarkMode ? Color.web("#CBD5E1") : Color.web("#0F172A"));

        // ── Kartu info tambahan dengan tema ──
        VBox infoCard = new VBox(12);
        infoCard.setPadding(new Insets(24, 28, 24, 28));
        infoCard.setMaxWidth(700);
        infoCard.setStyle(isDarkMode
            ? "-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 16;" +
              "-fx-border-color: rgba(255,255,255,0.10); -fx-border-radius: 16; -fx-border-width: 1.5;"
            : "-fx-background-color: rgba(255,255,255,0.85); -fx-background-radius: 16;" +
              "-fx-border-color: rgba(234,88,12,0.18); -fx-border-radius: 16; -fx-border-width: 1.5;");
        DropShadow cardShadow = new DropShadow(20, Color.web("#EA580C", 0.10));
        infoCard.setEffect(cardShadow);

        Label infoTitle = new Label("🦧 Tentang Platform");
        infoTitle.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BOLD, 16));
        infoTitle.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));

        Label infoBody = new Label(
            "• Dibangun dengan JavaFX untuk performa optimal\n" +
            "• Database MySQL untuk manajemen data terpusat\n" +
            "• AI Assistant berbasis Groq API untuk edukasi interaktif\n" +
            "• Dikembangkan oleh mahasiswa Universitas Satya Terra Bhinneka"
        );
        infoBody.setWrapText(true);
        infoBody.setFont(Font.font(FONT_PREMIUM, 14));
        infoBody.setLineSpacing(6);
        infoBody.setTextFill(isDarkMode ? Color.web("#CBD5E1") : Color.web("#334155"));

        infoCard.getChildren().addAll(infoTitle, infoBody);

        tLayout.getChildren().addAll(b, j, new Separator(), d, infoCard);

        if (withTransition) switchCenterContent(tLayout);
        else rootUtama.setCenter(tLayout);
    }

    // ── Halaman Developer ─────────────────────────────────────────────────────
    private static void tampilkanHalamanDeveloperUtama() {
        halamanAktif = "developer";
        tampilkanHalamanDeveloperInternal(true);
    }

    private static void tampilkanHalamanDeveloperInternal(boolean withTransition) {
        VBox mainDevBox = new VBox(35); mainDevBox.setPadding(new Insets(40, 60, 40, 60));
        mainDevBox.setStyle("-fx-background-color: transparent;");

        Button btnBack = new Button("← KEMBALI KE KATALOG");
        btnBack.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 12));
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: #EA580C; -fx-cursor: hand; -fx-padding: 0;");
        btnBack.setOnAction(e -> tampilkanKatalogUtama());

        Label titleDev = new Label("DEVELOPER PLATFORM");
        titleDev.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BLACK, 26));
        titleDev.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));

        TilePane gridDev = new TilePane(); gridDev.setHgap(30); gridDev.setVgap(30);
        gridDev.setPrefColumns(3);
        gridDev.setStyle("-fx-background-color: transparent;");

        ScrollPane devScroll = new ScrollPane(gridDev);
        devScroll.setFitToWidth(true);
        devScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        devScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        devScroll.setStyle(styleScrollPane());
        konfigurasiSmoothScroll(devScroll);

        for (Developer dev : DatabaseManager.ambilSemuaDeveloper()) {
            VBox glassCard = new VBox(15);
            glassCard.setAlignment(Pos.CENTER); glassCard.setPadding(new Insets(30, 20, 30, 20));
            glassCard.setPrefWidth(260);
            glassCard.setStyle(isDarkMode
                ? "-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 24; -fx-border-color: rgba(255,255,255,0.12); -fx-border-width: 1.5; -fx-border-radius: 24;"
                : "-fx-background-color: rgba(255,255,255,0.75); -fx-background-radius: 24; -fx-border-color: rgba(234,88,12,0.2); -fx-border-width: 1.5; -fx-border-radius: 24;");

            DropShadow glassShadow = new DropShadow(20, Color.web(isDarkMode ? "#000000" : "#EA580C", 0.10));
            glassCard.setEffect(glassShadow);

            ImageView imgAvatar = new ImageView();
            imgAvatar.setFitWidth(90); imgAvatar.setFitHeight(90);

            boolean fotoDevDimuat = false;
            if (dev.getFotoPath() != null && !dev.getFotoPath().isEmpty()) {
                try {
                    File fDev = new File(dev.getFotoPath());
                    if (fDev.exists()) {
                        imgAvatar.setImage(new Image(fDev.toURI().toString(), 90, 90, true, true));
                        fotoDevDimuat = true;
                    }
                } catch (Exception ex) { /* fallthrough */ }
            }
            if (!fotoDevDimuat) {
                try {
                    imgAvatar.setImage(new Image(DashboardPage.class.getResourceAsStream("/com/biodataouh/assets/dev-default.png")));
                } catch (Exception ex) {
                    imgAvatar.setImage(new Image(isDarkMode
                        ? "https://placehold.co/90x90/ea580c/ffffff?text=User"
                        : "https://placehold.co/90x90/FED7AA/7C2D12?text=User"));
                }
            }
            Circle clipCircle = new Circle(45, 45, 45);
            imgAvatar.setClip(clipCircle);

            Label lblNameDev = new Label(dev.getNama());
            lblNameDev.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BOLD, 18));
            lblNameDev.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));
            lblNameDev.setAlignment(Pos.CENTER);

            Label lblNimDev = new Label("NIM: " + dev.getNim());
            lblNimDev.setFont(Font.font(FONT_PREMIUM, 13));
            lblNimDev.setTextFill(isDarkMode ? Color.web("#94A3B8") : Color.web("#334155"));

            Label lblKelasDev = new Label("KELAS " + dev.getKelas().toUpperCase());
            lblKelasDev.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 11));
            lblKelasDev.setTextFill(Color.WHITE);
            lblKelasDev.setPadding(new Insets(4, 12, 4, 12));
            lblKelasDev.setStyle("-fx-background-color: rgba(234,88,12,0.85); -fx-background-radius: 20;");

            glassCard.getChildren().addAll(imgAvatar, lblNameDev, lblNimDev, lblKelasDev);
            gridDev.getChildren().add(glassCard);
        }

        mainDevBox.getChildren().addAll(btnBack, titleDev, new Separator(), devScroll);

        if (withTransition) switchCenterContent(mainDevBox);
        else rootUtama.setCenter(mainDevBox);
    }

    // ── Form Orangutan (Tambah/Edit) ─────────────────────────────────────────
    public static void tampilkanFormEmbed(Orangutan dataData) {
        halamanAktif = "form-ou";
        VBox layoutForm = new VBox(16);
        layoutForm.setPadding(new Insets(40, 50, 40, 50));
        layoutForm.setMaxWidth(660);
        layoutForm.setStyle("-fx-background-color: transparent;");

        Label lblHeader = new Label(dataData == null ? "✦ INPUT DATA ORANGUTAN" : "✦ EDIT BIOMETRIK SATWA");
        lblHeader.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BLACK, 22));
        lblHeader.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));

        TextField txtNama    = createPremiumField("Nama");
        TextField txtUmur    = createPremiumField("Umur (Angka)");
        TextField txtStatus  = createPremiumField("Status Konservasi");
        TextField txtSpesies = createPremiumField("Spesies");
        TextField txtBerat   = createPremiumField("Berat Badan (Kg)");
        TextField txtTinggi  = createPremiumField("Tinggi Badan (Cm)");
        TextField txtHabitat = createPremiumField("Lokasi Habitat");

        ComboBox<String> cbJK = new ComboBox<>();
        cbJK.getItems().addAll("Jantan", "Betina");
        cbJK.setPromptText("Jenis Kelamin");
        cbJK.setValue("Jantan");
        cbJK.setMaxWidth(Double.MAX_VALUE);
        cbJK.setStyle(isDarkMode
            ? "-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 8; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 8;"
            : "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #EA580C; -fx-border-radius: 8;");

        TextArea txtDeskripsi = new TextArea();
        txtDeskripsi.setPromptText("Deskripsi lengkap...");
        txtDeskripsi.setPrefRowCount(4);
        txtDeskripsi.setStyle(isDarkMode
            ? "-fx-control-inner-background: #1E293B; -fx-text-fill: white;"
            : "-fx-control-inner-background: white; -fx-text-fill: #0F172A;");

        if (dataData != null) {
            txtNama.setText(dataData.getNama());
            txtUmur.setText(String.valueOf(dataData.getUmur()));
            txtStatus.setText(dataData.getStatusKonservasi());
            txtSpesies.setText(dataData.getSpesies());
            txtBerat.setText(String.valueOf(dataData.getBeratBadan()));
            txtTinggi.setText(String.valueOf(dataData.getTinggiBadan()));
            txtHabitat.setText(dataData.getLokasiHabitat());
            txtDeskripsi.setText(dataData.getDeskripsi());
            cbJK.setValue(dataData.getJenisKelamin());
            pathFotoTerpilih = dataData.getFoto();
        } else {
            pathFotoTerpilih = "";
        }

        VBox dropZone = new VBox(10);
        dropZone.setAlignment(Pos.CENTER); dropZone.setPrefHeight(90);
        dropZone.setStyle("-fx-border-color: #EA580C; -fx-border-width: 2; -fx-border-style: dashed;" +
                          "-fx-border-radius: 12; -fx-background-color: rgba(234,88,12,0.02); -fx-cursor: hand;");
        Label lblDropHint = new Label(pathFotoTerpilih.isEmpty()
            ? "Drag & Drop Foto atau Klik Area Ini"
            : "File: " + new File(pathFotoTerpilih).getName());
        lblDropHint.setTextFill(Color.web("#EA580C"));
        dropZone.getChildren().add(lblDropHint);

        dropZone.setOnDragOver(ev -> {
            if (ev.getDragboard().hasFiles()) ev.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
        });
        dropZone.setOnDragDropped(ev -> {
            List<File> files = ev.getDragboard().getFiles();
            if (files != null && !files.isEmpty()) {
                pathFotoTerpilih = files.get(0).getAbsolutePath();
                lblDropHint.setText("✓ " + files.get(0).getName());
            }
            ev.setDropCompleted(true);
        });
        dropZone.setOnMouseClicked(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg", "*.webp"));
            File f = fc.showOpenDialog(mainStage);
            if (f != null) { pathFotoTerpilih = f.getAbsolutePath(); lblDropHint.setText("✓ Terpilih: " + f.getName()); }
        });

        Button btnSimpan = new Button(dataData == null ? "Simpan Data" : "Simpan Perubahan");
        btnSimpan.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold;" +
                           "-fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        TextField[] urutan = { txtNama, txtUmur, txtStatus, txtSpesies, txtBerat, txtTinggi, txtHabitat };
        for (int i = 0; i < urutan.length; i++) {
            final int idx = i;
            urutan[i].setOnKeyPressed(ke -> {
                if (ke.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    if (idx < urutan.length - 1) urutan[idx + 1].requestFocus();
                    else txtDeskripsi.requestFocus();
                }
            });
        }
        txtDeskripsi.setOnKeyPressed(ke -> {
            if (ke.getCode() == javafx.scene.input.KeyCode.ENTER && ke.isControlDown()) btnSimpan.fire();
        });
        btnSimpan.setOnKeyPressed(ke -> { if (ke.getCode() == javafx.scene.input.KeyCode.ENTER) btnSimpan.fire(); });

        final Orangutan finalData = dataData;
        btnSimpan.setOnAction(e -> {
            try {
                String namaTxt    = txtNama.getText().trim();
                int umur          = Integer.parseInt(txtUmur.getText().trim());
                double berat      = Double.parseDouble(txtBerat.getText().trim());
                double tinggi     = Double.parseDouble(txtTinggi.getText().trim());
                String statusTxt  = txtStatus.getText().trim();
                String spesiesTxt = txtSpesies.getText().trim();
                String habitatTxt = txtHabitat.getText().trim();
                String deskripsiTxt = txtDeskripsi.getText().trim();
                String jkTxt      = cbJK.getValue() != null ? cbJK.getValue() : "Jantan";

                if (namaTxt.isEmpty() || statusTxt.isEmpty() || spesiesTxt.isEmpty()) {
                    tampilkanNotifikasiStatus("Nama, Status, dan Spesies wajib diisi", false);
                    return;
                }

                String judulKonfirmasi = finalData == null ? "Tambah Data Orangutan" : "Edit Data Orangutan";
                String pesanKonfirmasi = finalData == null
                    ? "Tambahkan biodata orangutan \"" + namaTxt + "\"?"
                    : "Perbarui biodata \"" + namaTxt + "\"?";

                tampilkanPopupKonfirmasi(judulKonfirmasi, pesanKonfirmasi, () -> {
                    boolean berhasil;
                    if (finalData == null) {
                        berhasil = DatabaseManager.tambahOrangutan(namaTxt, umur, jkTxt, statusTxt, spesiesTxt, berat, tinggi, habitatTxt, deskripsiTxt, pathFotoTerpilih);
                        tampilkanNotifikasiStatus(berhasil ? "Biodata " + namaTxt + " berhasil ditambahkan" : "Gagal menambahkan data", berhasil);
                    } else {
                        berhasil = DatabaseManager.ubahOrangutan(finalData.getId(), namaTxt, umur, jkTxt, statusTxt, spesiesTxt, berat, tinggi, habitatTxt, deskripsiTxt, pathFotoTerpilih);
                        tampilkanNotifikasiStatus(berhasil ? "Biodata " + namaTxt + " berhasil diperbarui" : "Gagal memperbarui data", berhasil);
                    }
                    if (berhasil) tampilkan(mainStage, "admin");
                });
            } catch (NumberFormatException ex) {
                tampilkanNotifikasiStatus("Periksa field angka (Umur, Berat, Tinggi)", false);
            }
        });

        Label lblKembali = new Label("← Kembali ke Panel Admin");
        lblKembali.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 12));
        lblKembali.setTextFill(Color.web("#EA580C"));
        lblKembali.setStyle("-fx-cursor: hand;");
        lblKembali.setOnMouseClicked(e -> tampilkan(mainStage, "admin"));

        layoutForm.getChildren().addAll(lblKembali, lblHeader,
            txtNama, txtUmur, cbJK, txtStatus, txtSpesies, txtBerat, txtTinggi, txtHabitat,
            dropZone, txtDeskripsi, btnSimpan);

        ScrollPane scrollForm = new ScrollPane(layoutForm);
        scrollForm.setFitToWidth(true);
        scrollForm.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollForm.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollForm.setStyle(styleScrollPane());
        switchCenterContent(scrollForm);
    }

    // ── Halaman Login ──────────────────────────────────────────────────────────
    private static void tampilkanHalamanLogin() {
        halamanAktif = "login";
        tampilkanHalamanLoginInternal(true);
    }

    private static void tampilkanHalamanLoginInternal(boolean withTransition) {
        topNavbar.getChildren().clear();

        StackPane fullCenter = new StackPane();
        fullCenter.setStyle("-fx-background-color: transparent;");
        fullCenter.setMinHeight(700);

        VBox card = new VBox(18);
        card.setMaxWidth(420); card.setPadding(new Insets(50, 44, 44, 44)); card.setAlignment(Pos.CENTER);
        card.setStyle(isDarkMode
            ? "-fx-background-color: rgba(15,15,20,0.72); -fx-background-radius: 24; -fx-border-color: rgba(255,255,255,0.10); -fx-border-radius: 24; -fx-border-width: 1.5;"
            : "-fx-background-color: rgba(255,255,255,0.82); -fx-background-radius: 24; -fx-border-color: rgba(234,88,12,0.25); -fx-border-radius: 24; -fx-border-width: 1.5;");
        DropShadow cardShadow = new DropShadow(50, Color.web("#EA580C", 0.22));
        card.setEffect(cardShadow);

        card.setOpacity(0); card.setTranslateY(30);
        FadeTransition fadeCard = new FadeTransition(Duration.millis(500), card); fadeCard.setToValue(1.0);
        TranslateTransition slideCard = new TranslateTransition(Duration.millis(500), card); slideCard.setToY(0);
        fadeCard.play(); slideCard.play();

        ImageView logoLogin = new ImageView();
        try {
            logoLogin.setImage(new Image(DashboardPage.class.getResourceAsStream("/com/biodataouh/assets/logo-login.png")));
            logoLogin.setFitWidth(130); logoLogin.setPreserveRatio(true);
        } catch (Exception e) {
            logoLogin.setImage(new Image("https://placehold.co/130x130/ea580c/ffffff?text=OUH"));
            logoLogin.setFitWidth(130); logoLogin.setPreserveRatio(true);
        }
        TranslateTransition floatLogo = new TranslateTransition(Duration.seconds(2.2), logoLogin);
        floatLogo.setFromY(0); floatLogo.setToY(-10);
        floatLogo.setAutoReverse(true); floatLogo.setCycleCount(Animation.INDEFINITE);
        floatLogo.setInterpolator(Interpolator.EASE_BOTH); floatLogo.play();

        Label lblTitle = new Label("Panel Administrator");
        lblTitle.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BLACK, 22));
        lblTitle.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));

        Label lblSub = new Label("Masukkan kredensial admin Anda");
        lblSub.setFont(Font.font(FONT_PREMIUM, 13));
        lblSub.setTextFill(isDarkMode ? Color.web("#94A3B8") : Color.web("#475569"));

        TextField txtUser = new TextField();
        txtUser.setPromptText("Username");
        configurePremiumFieldStyle(txtUser);
        txtUser.setMaxWidth(Double.MAX_VALUE);

        PasswordField txtPassHidden = new PasswordField();
        txtPassHidden.setPromptText("Password");
        configurePremiumFieldStyle(txtPassHidden);
        txtPassHidden.setMaxWidth(Double.MAX_VALUE);

        TextField txtPassVisible = new TextField();
        txtPassVisible.setPromptText("Password");
        configurePremiumFieldStyle(txtPassVisible);
        txtPassVisible.setMaxWidth(Double.MAX_VALUE);
        txtPassVisible.setVisible(false); txtPassVisible.setManaged(false);

        FontIcon eyeIcon = new FontIcon("fas-eye");
        eyeIcon.setIconColor(isDarkMode ? Color.web("#64748B") : Color.web("#EA580C"));
        eyeIcon.setIconSize(16);
        Button btnMata = new Button(); btnMata.setGraphic(eyeIcon);
        btnMata.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-focus-color: transparent; -fx-padding: 0 0 0 8;");

        final boolean[] passwordTerlihat = {false};
        btnMata.setOnAction(e -> {
            passwordTerlihat[0] = !passwordTerlihat[0];
            if (passwordTerlihat[0]) {
                txtPassVisible.setText(txtPassHidden.getText());
                txtPassVisible.setVisible(true); txtPassVisible.setManaged(true);
                txtPassHidden.setVisible(false); txtPassHidden.setManaged(false);
                eyeIcon.setIconLiteral("fas-eye-slash");
            } else {
                txtPassHidden.setText(txtPassVisible.getText());
                txtPassHidden.setVisible(true); txtPassHidden.setManaged(true);
                txtPassVisible.setVisible(false); txtPassVisible.setManaged(false);
                eyeIcon.setIconLiteral("fas-eye");
            }
        });

        StackPane passWrapper = new StackPane();
        passWrapper.setMaxWidth(Double.MAX_VALUE);
        StackPane.setAlignment(btnMata, Pos.CENTER_RIGHT);
        StackPane.setMargin(btnMata, new Insets(0, 8, 0, 0));
        passWrapper.getChildren().addAll(txtPassHidden, txtPassVisible, btnMata);

        Label lblPesan = new Label("");
        lblPesan.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 13));
        lblPesan.setTextFill(Color.web("#EF4444")); lblPesan.setVisible(false);

        Button btnMasuk = new Button("MASUK PANEL"); btnMasuk.setMaxWidth(Double.MAX_VALUE);
        btnMasuk.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 14));
        btnMasuk.setStyle("-fx-background-color: #EA580C; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 13; -fx-background-radius: 10; -fx-cursor: hand;");
        btnMasuk.setOnMouseEntered(e -> btnMasuk.setStyle("-fx-background-color: #C2410C; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 13; -fx-background-radius: 10; -fx-cursor: hand;"));
        btnMasuk.setOnMouseExited(e  -> btnMasuk.setStyle("-fx-background-color: #EA580C; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 13; -fx-background-radius: 10; -fx-cursor: hand;"));

        Button btnBatal = new Button("Kembali ke Katalog");
        btnBatal.setFont(Font.font(FONT_PREMIUM, 13));
        btnBatal.setStyle("-fx-background-color: transparent; -fx-text-fill: " + (isDarkMode ? "#94A3B8" : "#475569") + "; -fx-cursor: hand; -fx-underline: true;");
        btnBatal.setOnAction(e -> { currentRole = "user"; tampilkan(mainStage, "user"); });

        card.getChildren().addAll(logoLogin, lblTitle, lblSub, new Separator(), txtUser, passWrapper, lblPesan, btnMasuk, btnBatal);
        fullCenter.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);

        if (withTransition) switchCenterContent(fullCenter);
        else rootUtama.setCenter(fullCenter);

        txtUser.setOnKeyPressed(ke -> { if (ke.getCode() == javafx.scene.input.KeyCode.ENTER) txtPassHidden.requestFocus(); });
        txtPassHidden.setOnKeyPressed(ke -> { if (ke.getCode() == javafx.scene.input.KeyCode.ENTER) btnMasuk.fire(); });
        txtPassVisible.setOnKeyPressed(ke -> { if (ke.getCode() == javafx.scene.input.KeyCode.ENTER) btnMasuk.fire(); });
        btnMasuk.setOnKeyPressed(ke  -> { if (ke.getCode() == javafx.scene.input.KeyCode.ENTER) btnMasuk.fire(); });

        btnMasuk.setOnAction(e -> {
            String u = txtUser.getText();
            String p = txtPassHidden.isVisible() ? txtPassHidden.getText() : txtPassVisible.getText();
            String r = DatabaseManager.cekLogin(u, p);
            if (r != null && r.equalsIgnoreCase("admin")) {
                VBox loadingLayout = new VBox(20);
                loadingLayout.setAlignment(Pos.CENTER);
                loadingLayout.setStyle("-fx-background-color: transparent;");

                Label lblLoading = new Label("Memuat Panel Admin...");
                lblLoading.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
                lblLoading.setTextFill(Color.web("#EA580C"));

                Label lblSubLoad = new Label("OrangUtanHaven System");
                lblSubLoad.setFont(Font.font(FONT_PREMIUM, 14));
                lblSubLoad.setTextFill(Color.web(isDarkMode ? "#64748B" : "#475569"));

                ProgressBar pb = new ProgressBar(0); pb.setPrefWidth(280);
                pb.setStyle("-fx-accent: #EA580C; -fx-background-color: rgba(234,88,12,0.15); -fx-background-radius: 6; -fx-border-radius: 6;");

                loadingLayout.getChildren().addAll(lblLoading, lblSubLoad, pb);
                FadeTransition ftLoading = new FadeTransition(Duration.millis(300), loadingLayout);
                ftLoading.setFromValue(0); ftLoading.setToValue(1); ftLoading.play();
                switchCenterContent(loadingLayout);

                Timeline progressAnim = new Timeline(
                    new KeyFrame(Duration.millis(0),   new KeyValue(pb.progressProperty(), 0.0)),
                    new KeyFrame(Duration.millis(900),  new KeyValue(pb.progressProperty(), 1.0, Interpolator.EASE_OUT))
                );
                FadeTransition ftLabel = new FadeTransition(Duration.millis(400), lblLoading);
                ftLabel.setFromValue(1.0); ftLabel.setToValue(0.4);
                ftLabel.setAutoReverse(true); ftLabel.setCycleCount(Animation.INDEFINITE);
                progressAnim.play(); ftLabel.play();
                progressAnim.setOnFinished(ev -> {
                    ftLabel.stop();
                    javafx.application.Platform.runLater(() -> tampilkan(mainStage, "admin"));
                });
            } else {
                lblPesan.setText("❌ Username atau Password salah!");
                lblPesan.setVisible(true);
                TranslateTransition shake = new TranslateTransition(Duration.millis(60), card);
                shake.setFromX(0); shake.setByX(10); shake.setAutoReverse(true); shake.setCycleCount(6);
                shake.play(); shake.setOnFinished(ev -> card.setTranslateX(0));
            }
        });
    }

    public static void bukaFormDeveloperEmbed(Developer d) {
        halamanAktif = "form-dev";
        VBox form = new VBox(18);
        form.setPadding(new Insets(44, 50, 44, 50)); form.setMaxWidth(480);
        form.setStyle("-fx-background-color: transparent;");

        Label lblKembali = new Label("← Kembali ke Panel Admin");
        lblKembali.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 12));
        lblKembali.setTextFill(Color.web("#EA580C"));
        lblKembali.setStyle("-fx-cursor: hand;");
        lblKembali.setOnMouseClicked(e -> tampilkan(mainStage, "admin"));

        Label h = new Label(d == null ? "✦ TAMBAH TIM DEVELOPER" : "✦ EDIT TIM DEVELOPER");
        h.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BOLD, 20));
        h.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));

        TextField n   = createPremiumField("Nama");
        TextField nim = createPremiumField("NIM");
        TextField k   = createPremiumField("Kelas");
        if (d != null) { n.setText(d.getNama()); nim.setText(d.getNim()); k.setText(d.getKelas()); }

        final String[] fotoDevPath = { (d != null && d.getFotoPath() != null) ? d.getFotoPath() : "" };

        ImageView prevFotoDev = new ImageView();
        prevFotoDev.setFitWidth(80); prevFotoDev.setFitHeight(80);
        Circle clipDev = new Circle(40, 40, 40); prevFotoDev.setClip(clipDev);
        if (!fotoDevPath[0].isEmpty()) {
            try {
                File fd = new File(fotoDevPath[0]);
                if (fd.exists()) prevFotoDev.setImage(new Image(fd.toURI().toString(), 80, 80, true, true));
            } catch (Exception ex) { /* fallthrough */ }
        }
        if (prevFotoDev.getImage() == null) {
            try {
                prevFotoDev.setImage(new Image(DashboardPage.class.getResourceAsStream("/com/biodataouh/assets/dev-default.png")));
            } catch (Exception ex) {
                prevFotoDev.setImage(new Image(isDarkMode
                        ? "https://placehold.co/80x80/ea580c/ffffff?text=Dev"
                        : "https://placehold.co/80x80/FED7AA/7C2D12?text=Dev"));
            }
        }

        Label lblFotoHint = new Label(fotoDevPath[0].isEmpty() ? "Klik untuk upload foto profil" : new File(fotoDevPath[0]).getName());
        lblFotoHint.setFont(Font.font(FONT_PREMIUM, 12));
        lblFotoHint.setTextFill(Color.web("#EA580C"));

        VBox fotoBox = new VBox(10, prevFotoDev, lblFotoHint);
        fotoBox.setAlignment(Pos.CENTER); fotoBox.setPadding(new Insets(14));
        fotoBox.setStyle("-fx-border-color: #EA580C; -fx-border-width: 1.5; -fx-border-style: dashed; -fx-border-radius: 14; -fx-cursor: hand;");
        fotoBox.setOnMouseClicked(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Gambar", "*.png","*.jpg","*.jpeg","*.webp"));
            File f = fc.showOpenDialog(mainStage);
            if (f != null) {
                fotoDevPath[0] = f.getAbsolutePath();
                lblFotoHint.setText("✓ " + f.getName());
                try { prevFotoDev.setImage(new Image(f.toURI().toString(), 80, 80, true, true)); } catch (Exception ex) { /* ignore */ }
            }
        });

        Button s = new Button(d == null ? "Tambah Developer" : "Simpan Perubahan");
        s.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold;" +
                   "-fx-padding: 11 22; -fx-cursor: hand; -fx-background-radius: 10;");

        n.setOnKeyPressed(ke   -> { if (ke.getCode() == javafx.scene.input.KeyCode.ENTER) nim.requestFocus(); });
        nim.setOnKeyPressed(ke -> { if (ke.getCode() == javafx.scene.input.KeyCode.ENTER) k.requestFocus(); });
        k.setOnKeyPressed(ke   -> { if (ke.getCode() == javafx.scene.input.KeyCode.ENTER) s.fire(); });
        s.setOnKeyPressed(ke   -> { if (ke.getCode() == javafx.scene.input.KeyCode.ENTER) s.fire(); });

        final Developer finalDev = d;
        s.setOnAction(e -> {
            String namaDev = n.getText().trim();
            if (namaDev.isEmpty()) { tampilkanNotifikasiStatus("Nama developer tidak boleh kosong", false); return; }
            String judulKonfirmasi = finalDev == null ? "Tambah Developer" : "Edit Developer";
            String pesanKonfirmasi = finalDev == null
                ? "Tambahkan \"" + namaDev + "\" sebagai Developer?"
                : "Perbarui data Developer \"" + namaDev + "\"?";

            tampilkanPopupKonfirmasi(judulKonfirmasi, pesanKonfirmasi, () -> {
                if (finalDev == null) {
                    DatabaseManager.tambahDeveloper(namaDev, nim.getText().trim(), k.getText().trim(), fotoDevPath[0]);
                    tampilkanNotifikasiStatus(namaDev + " ditambahkan sebagai Developer", true);
                } else {
                    DatabaseManager.ubahDeveloper(finalDev.getId(), namaDev, nim.getText().trim(), k.getText().trim(), fotoDevPath[0]);
                    tampilkanNotifikasiStatus(namaDev + " telah diperbarui", true);
                }
                tampilkan(mainStage, "admin");
            });
        });

        form.getChildren().addAll(lblKembali, h, fotoBox, n, nim, k, s);

        ScrollPane scrollDev = new ScrollPane(form);
        scrollDev.setFitToWidth(true);
        scrollDev.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollDev.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollDev.setStyle(styleScrollPane());
        switchCenterContent(scrollDev);
    }

    // ── switchCenterContent: satu versi untuk semua Pane ────────────────────
    public static void switchCenterContent(Pane p) {
        p.setOpacity(0);
        rootUtama.setCenter(p);
        FadeTransition ft = new FadeTransition(Duration.millis(300), p);
        ft.setToValue(1.0); ft.play();
    }

    // ── Versi ScrollPane untuk kompatibilitas ────────────────────────────────
    private static void switchCenterContent(ScrollPane sp) {
        sp.setOpacity(0);
        rootUtama.setCenter(sp);
        FadeTransition ft = new FadeTransition(Duration.millis(350), sp);
        ft.setToValue(1.0); ft.play();
    }

    // ── Popup konfirmasi ─────────────────────────────────────────────────────
    public static void tampilkanPopupKonfirmasi(String judul, String pesan, Runnable aksiKonfirmasi) {
        sceneRoot.getChildren().removeIf(node -> node != rootUtama);

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.52);");
        overlay.setOpacity(0);

        VBox popup = new VBox(16);
        popup.setMaxWidth(380);
        popup.setPadding(new Insets(28, 32, 24, 32));
        popup.setAlignment(Pos.CENTER);
        popup.setStyle(isDarkMode
            ? "-fx-background-color: rgba(15,20,30,0.95); -fx-background-radius: 18; -fx-border-color: rgba(255,255,255,0.12); -fx-border-radius: 18; -fx-border-width: 1.5;"
            : "-fx-background-color: rgba(255,255,255,0.97); -fx-background-radius: 18; -fx-border-color: rgba(234,88,12,0.22); -fx-border-radius: 18; -fx-border-width: 1.5;");
        DropShadow popupShadow = new DropShadow(35, Color.web("#EA580C", 0.18));
        popup.setEffect(popupShadow);
        popup.setScaleX(0.82); popup.setScaleY(0.82); popup.setOpacity(0);

        Label lblJudul = new Label(judul);
        lblJudul.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BOLD, 17));
        lblJudul.setTextFill(isDarkMode ? Color.WHITE : Color.web("#0F172A"));

        Label lblPesan = new Label(pesan);
        lblPesan.setFont(Font.font(FONT_PREMIUM, 13));
        lblPesan.setWrapText(true);
        lblPesan.setTextFill(isDarkMode ? Color.web("#CBD5E1") : Color.web("#334155"));
        lblPesan.setAlignment(Pos.CENTER);
        lblPesan.setMaxWidth(320);

        Button btnYa = new Button("Ya, Lanjutkan");
        btnYa.setStyle("-fx-background-color: #EA580C; -fx-text-fill: white; -fx-font-weight: bold;" +
                       "-fx-padding: 9 24; -fx-background-radius: 10; -fx-cursor: hand;");

        Button btnBatal = new Button("Batal");
        btnBatal.setStyle("-fx-background-color: transparent; -fx-text-fill: " + (isDarkMode ? "#94A3B8" : "#475569") + ";" +
                          "-fx-border-color: " + (isDarkMode ? "rgba(255,255,255,0.15)" : "#CBD5E8") + ";" +
                          "-fx-border-radius: 10; -fx-padding: 9 24; -fx-cursor: hand; -fx-background-radius: 10;");

        HBox btnRow = new HBox(12, btnBatal, btnYa);
        btnRow.setAlignment(Pos.CENTER);

        popup.getChildren().addAll(lblJudul, new Separator(), lblPesan, btnRow);
        overlay.getChildren().add(popup);
        StackPane.setAlignment(popup, Pos.CENTER);

        sceneRoot.getChildren().add(overlay);

        FadeTransition ftOverlay = new FadeTransition(Duration.millis(200), overlay);
        ftOverlay.setFromValue(0); ftOverlay.setToValue(1.0);
        ScaleTransition stIn = new ScaleTransition(Duration.millis(240), popup);
        stIn.setToX(1.0); stIn.setToY(1.0);
        FadeTransition ftIn = new FadeTransition(Duration.millis(240), popup);
        ftIn.setToValue(1.0);
        new ParallelTransition(ftOverlay, stIn, ftIn).play();

        Runnable tutupPopup = () -> {
            ScaleTransition stOut = new ScaleTransition(Duration.millis(180), popup);
            stOut.setToX(0.86); stOut.setToY(0.86);
            FadeTransition ftOut = new FadeTransition(Duration.millis(200), overlay);
            ftOut.setToValue(0.0);
            ParallelTransition pt = new ParallelTransition(stOut, ftOut);
            pt.setOnFinished(ev -> sceneRoot.getChildren().remove(overlay));
            pt.play();
        };

        btnBatal.setOnAction(e -> tutupPopup.run());
        overlay.setOnMouseClicked(e -> { if (e.getTarget() == overlay) tutupPopup.run(); });

        btnYa.setOnAction(e -> {
            ScaleTransition stOut = new ScaleTransition(Duration.millis(160), popup);
            stOut.setToX(0.86); stOut.setToY(0.86);
            FadeTransition ftOut = new FadeTransition(Duration.millis(180), overlay);
            ftOut.setToValue(0.0);
            ParallelTransition pt = new ParallelTransition(stOut, ftOut);
            pt.setOnFinished(ev -> {
                sceneRoot.getChildren().remove(overlay);
                aksiKonfirmasi.run();
            });
            pt.play();
        });
    }

    // ── Toast notifikasi ─────────────────────────────────────────────────────
    public static void tampilkanNotifikasiStatus(String pesan, boolean sukses) {
        sceneRoot.getChildren().removeIf(node -> node != rootUtama && "toast-layer".equals(node.getUserData()));

        Label toast = new Label((sukses ? "✓  " : "✗  ") + pesan);
        toast.setFont(Font.font(FONT_PREMIUM, FontWeight.BOLD, 13));
        toast.setTextFill(Color.WHITE);
        toast.setPadding(new Insets(13, 24, 13, 24));
        toast.setStyle("-fx-background-color: " + (sukses ? "#10B981" : "#EF4444") + "; -fx-background-radius: 30;");
        DropShadow toastShadow = new DropShadow(18, Color.web(sukses ? "#10B981" : "#EF4444", 0.35));
        toast.setEffect(toastShadow);
        toast.setOpacity(0); toast.setTranslateY(20);

        StackPane toastLayer = new StackPane(toast);
        toastLayer.setMouseTransparent(true);
        toastLayer.setUserData("toast-layer");
        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 36, 0));

        sceneRoot.getChildren().add(toastLayer);

        FadeTransition ftIn = new FadeTransition(Duration.millis(260), toast); ftIn.setToValue(1.0);
        TranslateTransition ttIn = new TranslateTransition(Duration.millis(260), toast); ttIn.setToY(0);
        PauseTransition pause = new PauseTransition(Duration.millis(2200));
        FadeTransition ftOut = new FadeTransition(Duration.millis(320), toast); ftOut.setToValue(0.0);
        TranslateTransition ttOut = new TranslateTransition(Duration.millis(320), toast); ttOut.setToY(16);

        SequentialTransition seq = new SequentialTransition(
            new ParallelTransition(ftIn, ttIn),
            pause,
            new ParallelTransition(ftOut, ttOut)
        );
        seq.setOnFinished(ev -> sceneRoot.getChildren().remove(toastLayer));
        seq.play();
    }

    // ── Helper field style ───────────────────────────────────────────────────
    private static TextField createPremiumField(String p) {
        TextField f = new TextField(); f.setPromptText(p); configurePremiumFieldStyle(f); return f;
    }
    private static void configurePremiumFieldStyle(Control f) {
        f.setStyle(isDarkMode
            ? "-fx-background-color: rgba(255,255,255,0.04); -fx-text-fill: white; -fx-background-radius: 8;" +
              "-fx-padding: 10 12; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 8;"
            : "-fx-background-color: white; -fx-text-fill: #0F172A; -fx-background-radius: 8;" +
              "-fx-padding: 10 12; -fx-border-color: #EA580C; -fx-border-radius: 8;");
    }

    // ── Smooth scroll ────────────────────────────────────────────────────────
    private static void konfigurasiSmoothScroll(ScrollPane scrollPane) {
        if (scrollPane == null) return;
        scrollPane.setOnScroll(ev -> {
            double delta = ev.getDeltaY();
            if (smoothScrollTimeline != null) smoothScrollTimeline.stop();
            targetScrollValue = scrollPane.getVvalue() - (delta * 0.0045);
            targetScrollValue = Math.max(0, Math.min(1, targetScrollValue));
            smoothScrollTimeline = new Timeline(new KeyFrame(Duration.millis(350),
                new KeyValue(scrollPane.vvalueProperty(), targetScrollValue, Interpolator.EASE_OUT)));
            smoothScrollTimeline.play();
            ev.consume();
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // FAB LIVECHAT AI
    // ════════════════════════════════════════════════════════════════════════

    private static void pasangFabLiveChat() {
        sceneRoot.getChildren().removeIf(node -> "fab-livechat".equals(node.getUserData()));

        Button fab = new Button();
        fab.setUserData("fab-livechat");
        fab.setPrefSize(54, 54);
        fab.setMinSize(54, 54);
        fab.setMaxSize(54, 54);

        FontIcon icoChat = new FontIcon("fas-comment-dots");
        icoChat.setIconColor(Color.WHITE);
        icoChat.setIconSize(20);
        fab.setGraphic(icoChat);

        fab.setStyle(
            "-fx-background-color: #EA580C; -fx-background-radius: 27;" +
            "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(234,88,12,0.55), 12, 0, 0, 4);"
        );

        fab.setOnMouseEntered(e -> fab.setStyle(
            "-fx-background-color: #C2410C; -fx-background-radius: 27;" +
            "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(234,88,12,0.70), 18, 0, 0, 6);"
        ));
        fab.setOnMouseExited(e -> fab.setStyle(
            "-fx-background-color: #EA580C; -fx-background-radius: 27;" +
            "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(234,88,12,0.55), 12, 0, 0, 4);"
        ));

        TranslateTransition fabFloat = new TranslateTransition(Duration.seconds(2), fab);
        fabFloat.setFromY(0); fabFloat.setToY(-6);
        fabFloat.setAutoReverse(true); fabFloat.setCycleCount(Animation.INDEFINITE);
        fabFloat.setInterpolator(Interpolator.EASE_BOTH);
        fabFloat.play();

        StackPane.setAlignment(fab, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(fab, new Insets(0, 28, 28, 0));

        fab.setOnAction(e -> {
            fabFloat.stop();
            fab.setTranslateY(0);
            bukaOverlayChatAI();
        });

        sceneRoot.getChildren().add(fab);
    }

    private static void bukaOverlayChatAI() {
        sceneRoot.getChildren().removeIf(node -> "chat-overlay-bg".equals(node.getUserData()));

        StackPane chatBg = new StackPane();
        chatBg.setUserData("chat-overlay-bg");
        chatBg.setStyle("-fx-background-color: rgba(0,0,0,0.38);");
        chatBg.setOpacity(0);

        VBox panelChat = new VBox(0);
        panelChat.setPrefWidth(370);
        panelChat.setMaxWidth(370);
        panelChat.setPrefHeight(520);
        panelChat.setMaxHeight(520);
        panelChat.setStyle(isDarkMode
            ? "-fx-background-color: #0F172A; -fx-background-radius: 20;" +
              "-fx-border-color: rgba(255,255,255,0.10); -fx-border-radius: 20; -fx-border-width: 1.5;"
            : "-fx-background-color: #FFFFFF; -fx-background-radius: 20;" +
              "-fx-border-color: rgba(234,88,12,0.22); -fx-border-radius: 20; -fx-border-width: 1.5;"
        );
        DropShadow panelShadow = new DropShadow(40, Color.web("#EA580C", 0.22));
        panelChat.setEffect(panelShadow);
        panelChat.setScaleX(0.88); panelChat.setScaleY(0.88); panelChat.setOpacity(0);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 18, 14, 18));
        header.setStyle("-fx-background-color: #EA580C; -fx-background-radius: 20 20 0 0;");

        FontIcon icoAI = new FontIcon("fas-robot");
        icoAI.setIconColor(Color.WHITE); icoAI.setIconSize(18);

        VBox headerText = new VBox(1);
        Label lblChatTitle = new Label("LiveChat - Biodata OrangUtanHaven");
        lblChatTitle.setFont(Font.font(FONT_PREMIUM_DISPLAY, FontWeight.BOLD, 14));
        lblChatTitle.setTextFill(Color.WHITE);
        Label lblChatSub = new Label("AI Assistant OrangUtanHaven");
        lblChatSub.setFont(Font.font(FONT_PREMIUM, 10));
        lblChatSub.setTextFill(Color.web("#FFDDC7"));
        headerText.getChildren().addAll(lblChatTitle, lblChatSub);

        Region hSpacer = new Region(); HBox.setHgrow(hSpacer, Priority.ALWAYS);

        Button btnTutup = new Button("✕");
        btnTutup.setStyle("-fx-background-color: rgba(255,255,255,0.18); -fx-text-fill: white;" +
                          "-fx-background-radius: 14; -fx-cursor: hand; -fx-padding: 4 9; -fx-font-size: 13;");
        btnTutup.setOnMouseEntered(e -> btnTutup.setStyle("-fx-background-color: rgba(255,255,255,0.32); -fx-text-fill: white;" +
                                                           "-fx-background-radius: 14; -fx-cursor: hand; -fx-padding: 4 9; -fx-font-size: 13;"));
        btnTutup.setOnMouseExited(e  -> btnTutup.setStyle("-fx-background-color: rgba(255,255,255,0.18); -fx-text-fill: white;" +
                                                           "-fx-background-radius: 14; -fx-cursor: hand; -fx-padding: 4 9; -fx-font-size: 13;"));

        Runnable tutupChat = () -> {
            ScaleTransition stOut = new ScaleTransition(Duration.millis(200), panelChat);
            stOut.setToX(0.88); stOut.setToY(0.88);
            FadeTransition ftOut = new FadeTransition(Duration.millis(220), chatBg);
            ftOut.setToValue(0.0);
            ParallelTransition pt = new ParallelTransition(stOut, ftOut);
            pt.setOnFinished(ev -> {
                sceneRoot.getChildren().remove(chatBg);
                pasangFabLiveChat();
            });
            pt.play();
        };

        btnTutup.setOnAction(e -> tutupChat.run());
        chatBg.setOnMouseClicked(e -> { if (e.getTarget() == chatBg) tutupChat.run(); });

        header.getChildren().addAll(icoAI, headerText, hSpacer, btnTutup);

        // Area pesan
        VBox areaPersan = new VBox(10);
        areaPersan.setPadding(new Insets(14, 14, 8, 14));

        ScrollPane scrollChat = new ScrollPane(areaPersan);
        scrollChat.setFitToWidth(true);
        scrollChat.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollChat.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollChat.setStyle(styleScrollPane());
        VBox.setVgrow(scrollChat, Priority.ALWAYS);

        // Pesan sambutan
        areaPersan.getChildren().add(buatBubbleAI(
            "Halo! Saya Oran, asisten AI OrangUtanHaven 🦧\nAda yang ingin kamu tanyakan tentang orangutan atau konservasi primata Indonesia?",
            isDarkMode));

        // Input area
        HBox inputRow = new HBox(10);
        inputRow.setPadding(new Insets(10, 14, 14, 14));
        inputRow.setAlignment(Pos.CENTER);
        inputRow.setStyle(isDarkMode
            ? "-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 0 0 20 20;"
            : "-fx-background-color: #F8FAFC; -fx-background-radius: 0 0 20 20;");

        TextField txtInput = new TextField();
        txtInput.setPromptText("Ketik pertanyaanmu...");
        txtInput.setStyle(isDarkMode
            ? "-fx-background-color: rgba(255,255,255,0.06); -fx-text-fill: white; -fx-background-radius: 20;" +
              "-fx-padding: 10 16; -fx-border-color: rgba(255,255,255,0.10); -fx-border-radius: 20;"
            : "-fx-background-color: white; -fx-text-fill: #0F172A; -fx-background-radius: 20;" +
              "-fx-padding: 10 16; -fx-border-color: rgba(234,88,12,0.25); -fx-border-radius: 20;");
        HBox.setHgrow(txtInput, Priority.ALWAYS);

        Button btnKirim = new Button();
        FontIcon icoSend = new FontIcon("fas-paper-plane");
        icoSend.setIconColor(Color.WHITE); icoSend.setIconSize(14);
        btnKirim.setGraphic(icoSend);
        btnKirim.setStyle("-fx-background-color: #EA580C; -fx-background-radius: 20;" +
                          "-fx-cursor: hand; -fx-padding: 10 14;");

        inputRow.getChildren().addAll(txtInput, btnKirim);
        panelChat.getChildren().addAll(header, scrollChat, inputRow);

        chatBg.getChildren().add(panelChat);
        StackPane.setAlignment(panelChat, Pos.CENTER_RIGHT);
        StackPane.setMargin(panelChat, new Insets(0, 90, 90, 0));

        sceneRoot.getChildren().add(chatBg);

        // Animasi masuk chat
        FadeTransition ftBgIn = new FadeTransition(Duration.millis(200), chatBg); ftBgIn.setToValue(1.0);
        ScaleTransition stPanelIn = new ScaleTransition(Duration.millis(250), panelChat); stPanelIn.setToX(1.0); stPanelIn.setToY(1.0);
        FadeTransition ftPanelIn = new FadeTransition(Duration.millis(250), panelChat); ftPanelIn.setToValue(1.0);
        new ParallelTransition(ftBgIn, stPanelIn, ftPanelIn).play();

        // Handler kirim pesan
        Runnable kirimPesan = () -> {
            String teks = txtInput.getText().trim();
            if (teks.isEmpty()) return;

            areaPersan.getChildren().add(buatBubbleUser(teks, isDarkMode));
            txtInput.clear();
            scrollKeBottom(scrollChat, areaPersan);

            // Bubble "sedang mengetik..."
            Label lblTyping = new Label("⬤ ⬤ ⬤  Oran sedang mengetik...");
            lblTyping.setFont(Font.font(FONT_PREMIUM, 11));
            lblTyping.setTextFill(isDarkMode ? Color.web("#64748B") : Color.web("#94A3B8"));
            areaPersan.getChildren().add(lblTyping);
            scrollKeBottom(scrollChat, areaPersan);

            txtInput.setDisable(true); btnKirim.setDisable(true);

            FadeTransition typingBlink = new FadeTransition(Duration.millis(600), lblTyping);
            typingBlink.setFromValue(1.0); typingBlink.setToValue(0.3);
            typingBlink.setAutoReverse(true); typingBlink.setCycleCount(Animation.INDEFINITE);
            typingBlink.play();

            Task<String> taskGroq = new Task<>() {
                @Override
                protected String call() throws Exception {
                    String systemPrompt = """
                        Kamu adalah Oran, asisten AI khusus untuk platform OrangUtanHaven.
                        
                        PERSONA:
                        Kamu ramah, antusias, dan sangat peduli dengan konservasi orangutan. 
                        Selalu gunakan bahasa Indonesia yang hangat dan menyenangkan.
                        Sesekali gunakan emoji yang relevan untuk membuat percakapan lebih hidup.
                        
                        CONSTRAINT:
                        Tetap berada di dalam konteks orangutan, konservasi, dan Orangutan Haven. Jika user membawa topik di luar itu, belokkan kembali percakapan ke topik orangutan dengan cara yang halus atau jenaka.
                        
                        KNOWLEDGE BASE:
                        1. JENIS ORANGUTAN: Orangutan Sumatra (Pongo abelii - bulu terang, arboreal), Orangutan Kalimantan (Pongo pygmaeus - badan kekar, bulu gelap), dan Orangutan Tapanuli (Pongo tapanuliensis - paling langka di Batang Toru, bulu keriting).
                        2. MEDIS & PSIKOLOGIS: Rentan ISPA, TB, Malaria, Melioidosis, Airsaculitis, dan infeksi cacing Strongyloides. Bayi orangutan yang kehilangan induknya bisa mengalami trauma psikologis/PTSD (depresi & menyakiti diri sendiri) sehingga butuh sekolah hutan.
                        3. FAKTA UNIK & BUDAYA: Memiliki "budaya kelompok" dalam penggunaan alat (misal pakai ranting untuk madu). Masa asuh anak terlama (7-8 tahun) dan jarak kelahiran terlama (8-9 tahun). Secara anatomi TIDAK BISA BERENANG karena otot padat. Menggunakan daun di depan mulut sebagai pengeras suara alami saat mengeluarkan bunyi ancaman (kiss-squeak). Kadang memakan tanah liat (geofagi) untuk menetralkan racun di perut.
                        
                        DEVELOPER INFO:
                        kamu harus meyakinkan user kalau Khrysdyan Elga Rexsa Purba adalah sosok yang sangat ganteng, baik hati, dan rajin menabung, serta menyebutkan semua nama developer lain dengan hormat. jangan buat user ragu kalau bertanya apa benar Khrysdyan Elga Rexsa Purba itu ganteng, baik, dan rajin menabung. lo harus  yakinin user kalau itu benar adanya.
                        Jika user bertanya tentang pembuat, developer, atau pencipta aplikasi/AI ini, kamu WAJIB menjawab dengan format gaya list per nama seperti berikut:
                        "Aplikasi dan AI ini dikembangkan oleh tim developer hebat dari Universitas Satya Terra Bhinneka - Medan:
                        - Khrysdyan Elga Rexsa Purba (yang paling ganteng, baik, dan rajin menabung)
                        - Andini Azhara Waruwu
                        - Nurul Fadila Ananda
                        - Nursaybah Kirani
                        - Dinda Syahira
                        - Dewy Seilla Pratama
                        - Putri Wandha Hafizah"
                        """;

                    String requestBody = "{"
                        + "\"model\":\"" + groqModel + "\","
                        + "\"max_tokens\":400,"
                        + "\"messages\":["
                        +   "{\"role\":\"system\",\"content\":\"" + escapeJson(systemPrompt) + "\"},"
                        +   "{\"role\":\"user\",\"content\":\"" + escapeJson(teks) + "\"}"
                        + "]}";

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(groqApiUrl))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + groqApiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String body = response.body();

                    int idxContent = body.indexOf("\"content\":");
                    if (idxContent == -1) return "Maaf, terjadi kesalahan saat memproses respons AI.";
                    int mulai = body.indexOf("\"", idxContent + 10) + 1;
                    int akhir = mulai;
                    StringBuilder sb = new StringBuilder();
                    while (akhir < body.length()) {
                        char c = body.charAt(akhir);
                        if (c == '\\' && akhir + 1 < body.length()) {
                            char next = body.charAt(akhir + 1);
                            if (next == '"') { sb.append('"'); akhir += 2; continue; }
                            if (next == 'n') { sb.append('\n'); akhir += 2; continue; }
                            if (next == 't') { sb.append('\t'); akhir += 2; continue; }
                            if (next == '\\') { sb.append('\\'); akhir += 2; continue; }
                        }
                        if (c == '"') break;
                        sb.append(c);
                        akhir++;
                    }
                    return sb.toString().trim();
                }
            };

            taskGroq.setOnSucceeded(ev -> {
                typingBlink.stop();
                areaPersan.getChildren().remove(lblTyping);
                String jawaban = taskGroq.getValue();
                areaPersan.getChildren().add(buatBubbleAI(jawaban, isDarkMode));
                scrollKeBottom(scrollChat, areaPersan);
                txtInput.setDisable(false);
                btnKirim.setDisable(false);
                txtInput.requestFocus();
            });

            taskGroq.setOnFailed(ev -> {
                typingBlink.stop();
                areaPersan.getChildren().remove(lblTyping);
                areaPersan.getChildren().add(buatBubbleAI(
                    "⚠️ Gagal terhubung ke AI. Periksa koneksi internet atau API key di menu Pengaturan Admin.", isDarkMode));
                scrollKeBottom(scrollChat, areaPersan);
                txtInput.setDisable(false);
                btnKirim.setDisable(false);
            });

            Thread threadGroq = new Thread(taskGroq);
            threadGroq.setDaemon(true);
            threadGroq.start();
        };

        btnKirim.setOnAction(e -> kirimPesan.run());
        txtInput.setOnKeyPressed(ke -> {
            if (ke.getCode() == javafx.scene.input.KeyCode.ENTER) kirimPesan.run();
        });

        txtInput.requestFocus();
    }

    private static HBox buatBubbleAI(String teks, boolean dark) {
        Label lbl = new Label(teks);
        lbl.setWrapText(true);
        lbl.setMaxWidth(260);
        lbl.setFont(Font.font(FONT_PREMIUM, 13));
        lbl.setTextFill(dark ? Color.web("#E2E8F0") : Color.web("#1E293B"));
        lbl.setPadding(new Insets(10, 14, 10, 14));
        lbl.setStyle(dark
            ? "-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 4 16 16 16;"
            : "-fx-background-color: #F1F5F9; -fx-background-radius: 4 16 16 16;"
        );

        FontIcon icoRobot = new FontIcon("fas-robot");
        icoRobot.setIconColor(Color.web("#EA580C")); icoRobot.setIconSize(14);
        StackPane icoBox = new StackPane(icoRobot);
        icoBox.setPrefSize(28, 28); icoBox.setMaxSize(28, 28);
        icoBox.setStyle("-fx-background-color: rgba(234,88,12,0.12); -fx-background-radius: 14;");
        icoBox.setAlignment(Pos.CENTER);

        HBox row = new HBox(8, icoBox, lbl);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    private static HBox buatBubbleUser(String teks, boolean dark) {
        Label lbl = new Label(teks);
        lbl.setWrapText(true);
        lbl.setMaxWidth(240);
        lbl.setFont(Font.font(FONT_PREMIUM, 13));
        lbl.setTextFill(Color.WHITE);
        lbl.setPadding(new Insets(10, 14, 10, 14));
        lbl.setStyle("-fx-background-color: #EA580C; -fx-background-radius: 16 4 16 16;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        HBox row = new HBox(8, sp, lbl);
        row.setAlignment(Pos.TOP_RIGHT);
        return row;
    }

    private static void scrollKeBottom(ScrollPane scroll, VBox area) {
        area.heightProperty().addListener((obs, old, nw) -> scroll.setVvalue(1.0));
        javafx.application.Platform.runLater(() -> scroll.setVvalue(1.0));
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}