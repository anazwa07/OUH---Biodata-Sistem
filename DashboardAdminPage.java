package com.biodataouh;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.collections.ObservableList;
import javafx.util.Duration;

public class DashboardAdminPage {

    private static final String FONT_DISPLAY = "SF Pro Display";
    private static final String FONT_TEXT    = "SF Pro Text";

    // ── Warna helper sesuai tema ─────────────────────────────────────────────
    private static Color warnaTeks(boolean dark)  { return dark ? Color.WHITE : Color.web("#0F172A"); }
    private static Color warnaSub(boolean dark)   { return dark ? Color.web("#94A3B8") : Color.web("#475569"); }
    private static String styleSidebar(boolean dark) {
        return dark
            ? "-fx-background-color: rgba(15,15,20,0.80); -fx-border-color: rgba(255,255,255,0.07); -fx-border-width: 0 1 0 0;"
            : "-fx-background-color: rgba(255,255,255,0.72); -fx-border-color: rgba(234,88,12,0.15); -fx-border-width: 0 1 0 0;";
    }

    // ── State navigasi aktif ─────────────────────────────────────────────────
    // "dashboard" | "tambah-ou" | "kelola-ou" | "tambah-dev" | "kelola-dev" | "pengaturan"
    private static String activeNav = "dashboard";

    // ── Entry point ──────────────────────────────────────────────────────────
    public static VBox dapatkanTampilan(boolean isDarkMode, Runnable callbackSegarkan) {
        activeNav = "dashboard";
        return bangunLayout(isDarkMode, callbackSegarkan);
    }

    // ── Layout utama: sidebar + konten ───────────────────────────────────────
    private static VBox bangunLayout(boolean isDarkMode, Runnable callbackSegarkan) {
        VBox wrapper = new VBox();
        wrapper.setStyle("-fx-background-color: transparent;");
        wrapper.setFillWidth(true);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(28, 40, 18, 40));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: transparent;");

        Label lblJudul = new Label("🛡️ Panel Kontrol Admin");
        lblJudul.setFont(Font.font(FONT_DISPLAY, FontWeight.BLACK, 24));
        lblJudul.setTextFill(warnaTeks(isDarkMode));

        Label lblSub = new Label("  Kelola data orangutan & tim developer");
        lblSub.setFont(Font.font(FONT_TEXT, 13));
        lblSub.setTextFill(warnaSub(isDarkMode));

        topBar.getChildren().addAll(lblJudul, lblSub);

        HBox mainRow = new HBox(0);
        mainRow.setFillHeight(true);
        mainRow.setStyle("-fx-background-color: transparent;");
        HBox.setHgrow(mainRow, Priority.ALWAYS);

        VBox sidebar = buatSidebar(isDarkMode, mainRow, callbackSegarkan);
        sidebar.setPrefWidth(220);
        sidebar.setMinWidth(220);
        sidebar.setMaxWidth(220);

        StackPane kontenArea = new StackPane();
        kontenArea.setStyle("-fx-background-color: transparent;");
        HBox.setHgrow(kontenArea, Priority.ALWAYS);

        kontenArea.getChildren().setAll(buatHalamanDashboard(isDarkMode, callbackSegarkan));

        sidebar.getChildren().clear();
        isiSidebar(sidebar, isDarkMode, kontenArea, callbackSegarkan);

        mainRow.getChildren().addAll(sidebar, kontenArea);
        wrapper.getChildren().addAll(topBar, mainRow);

        wrapper.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), wrapper);
        ft.setToValue(1.0); ft.play();

        return wrapper;
    }

    private static VBox buatSidebar(boolean isDarkMode, HBox mainRow, Runnable callbackSegarkan) {
        VBox sb = new VBox(6);
        sb.setPadding(new Insets(20, 12, 20, 12));
        sb.setStyle(styleSidebar(isDarkMode));
        return sb;
    }

    // ════════════════════════════════════════════════════════════════════════
    // [FITUR 3] SIDEBAR DENGAN GRUP PENGATURAN BARU
    // ════════════════════════════════════════════════════════════════════════
    private static void isiSidebar(VBox sidebar, boolean isDarkMode, StackPane kontenArea, Runnable callbackSegarkan) {
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setStyle(styleSidebar(isDarkMode));
        sidebar.setSpacing(4);

        // Grup Orangutan
        Label lblGrpOU = buatLabelGrup("ORANGUTAN", isDarkMode);
        Button btnDashboard   = buatNavBtn("fas-tachometer-alt", "Statistik",       "dashboard",   isDarkMode);
        Button btnTambahOU    = buatNavBtn("fas-plus-circle",    "Tambah Satwa",    "tambah-ou",   isDarkMode);
        Button btnKelolaOU    = buatNavBtn("fas-paw",            "Kelola Satwa",    "kelola-ou",   isDarkMode);

        // Grup Developer
        Label lblGrpDev = buatLabelGrup("DEVELOPER", isDarkMode);
        Button btnTambahDev   = buatNavBtn("fas-user-plus",      "Tambah Dev",      "tambah-dev",  isDarkMode);
        Button btnKelolaDev   = buatNavBtn("fas-users-cog",      "Kelola Dev",      "kelola-dev",  isDarkMode);

        // ── [FITUR 3] Grup Pengaturan AI ─────────────────────────────────────
        Label lblGrpPengaturan = buatLabelGrup("SISTEM", isDarkMode);
        Button btnPengaturan   = buatNavBtn("fas-sliders-h",     "Pengaturan AI",   "pengaturan",  isDarkMode);

        // Tandai aktif
        setNavAktif(btnDashboard, isDarkMode);

        // Handler navigasi
        btnDashboard.setOnAction(e -> {
            animasiKlik(btnDashboard);
            setSemuaTidakAktif(sidebar, isDarkMode);
            setNavAktif(btnDashboard, isDarkMode);
            gatiKonten(kontenArea, buatHalamanDashboard(isDarkMode, callbackSegarkan));
        });
        btnTambahOU.setOnAction(e -> {
            animasiKlik(btnTambahOU);
            setSemuaTidakAktif(sidebar, isDarkMode);
            setNavAktif(btnTambahOU, isDarkMode);
            DashboardPage.tampilkanFormEmbed(null);
        });
        btnKelolaOU.setOnAction(e -> {
            animasiKlik(btnKelolaOU);
            setSemuaTidakAktif(sidebar, isDarkMode);
            setNavAktif(btnKelolaOU, isDarkMode);
            gatiKonten(kontenArea, buatHalamanKelolaOU(isDarkMode, callbackSegarkan));
        });
        btnTambahDev.setOnAction(e -> {
            animasiKlik(btnTambahDev);
            setSemuaTidakAktif(sidebar, isDarkMode);
            setNavAktif(btnTambahDev, isDarkMode);
            DashboardPage.bukaFormDeveloperEmbed(null);
        });
        btnKelolaDev.setOnAction(e -> {
            animasiKlik(btnKelolaDev);
            setSemuaTidakAktif(sidebar, isDarkMode);
            setNavAktif(btnKelolaDev, isDarkMode);
            gatiKonten(kontenArea, buatHalamanKelolaDev(isDarkMode, callbackSegarkan));
        });
        // ── [FITUR 3] Handler Pengaturan AI ──
        btnPengaturan.setOnAction(e -> {
            animasiKlik(btnPengaturan);
            setSemuaTidakAktif(sidebar, isDarkMode);
            setNavAktif(btnPengaturan, isDarkMode);
            gatiKonten(kontenArea, buatHalamanPengaturanAI(isDarkMode));
        });

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
            lblGrpOU, btnDashboard, btnTambahOU, btnKelolaOU,
            new Separator(),
            lblGrpDev, btnTambahDev, btnKelolaDev,
            new Separator(),
            lblGrpPengaturan, btnPengaturan,
            spacer
        );
    }

    // ── Halaman statistik dashboard ──────────────────────────────────────────
    private static VBox buatHalamanDashboard(boolean isDarkMode, Runnable callbackSegarkan) {
        VBox page = new VBox(28);
        page.setPadding(new Insets(32, 40, 40, 36));
        page.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Statistik & Ringkasan");
        title.setFont(Font.font(FONT_DISPLAY, FontWeight.BOLD, 20));
        title.setTextFill(warnaTeks(isDarkMode));

        ObservableList<Orangutan> listOU = DatabaseManager.ambilSemuaOrangutan();
        ObservableList<Developer> listDev = DatabaseManager.ambilSemuaDeveloper();
        int totalOU  = listOU  != null ? listOU.size()  : 0;
        int totalDev = listDev != null ? listDev.size() : 0;

        HBox rowStat = new HBox(20);
        VBox cardOU  = buatCardStat("🦧 Data Satwa",    totalOU,  "#EA580C", isDarkMode);
        VBox cardDev = buatCardStat("👨‍💻 Tim Developer", totalDev, "#38BDF8", isDarkMode);
        rowStat.getChildren().addAll(cardOU, cardDev);

        animasiCounter(cardOU,  totalOU,  isDarkMode);
        animasiCounter(cardDev, totalDev, isDarkMode);

        Label lblAksi = new Label("Aksi Cepat");
        lblAksi.setFont(Font.font(FONT_DISPLAY, FontWeight.BOLD, 16));
        lblAksi.setTextFill(warnaTeks(isDarkMode));

        HBox rowAksi = new HBox(16);
        Button btnCepatTambahOU  = buatTombolAksiCepat("+ Tambah Orangutan", "#EA580C", isDarkMode);
        Button btnCepatKelolaOU  = buatTombolAksiCepat("☰ Kelola Orangutan", "#F59E0B", isDarkMode);
        Button btnCepatTambahDev = buatTombolAksiCepat("+ Tambah Developer",  "#38BDF8", isDarkMode);
        Button btnCepatKelolaDev = buatTombolAksiCepat("☰ Kelola Developer",  "#10B981", isDarkMode);

        btnCepatTambahOU.setOnAction(e  -> DashboardPage.tampilkanFormEmbed(null));
        btnCepatKelolaOU.setOnAction(e  -> DashboardPage.tampilkan(DashboardPage.getMainStage(), "admin"));
        btnCepatTambahDev.setOnAction(e -> DashboardPage.bukaFormDeveloperEmbed(null));
        btnCepatKelolaDev.setOnAction(e -> DashboardPage.tampilkan(DashboardPage.getMainStage(), "admin"));

        rowAksi.getChildren().addAll(btnCepatTambahOU, btnCepatKelolaOU, btnCepatTambahDev, btnCepatKelolaDev);

        Label lblPreview = new Label("Data Terbaru");
        lblPreview.setFont(Font.font(FONT_DISPLAY, FontWeight.BOLD, 16));
        lblPreview.setTextFill(warnaTeks(isDarkMode));

        VBox previewBox = new VBox(8);
        if (listOU != null) {
            int limit = Math.min(listOU.size(), 4);
            for (int i = 0; i < limit; i++) {
                Orangutan ou = listOU.get(i);
                HBox rowPrev = buatRowPreview(ou.getNama(), ou.getSpesies(), ou.getStatusKonservasi(), "#EA580C", isDarkMode);
                previewBox.getChildren().add(rowPrev);
            }
        }
        if (previewBox.getChildren().isEmpty()) {
            Label kosong = new Label("Belum ada data orangutan.");
            kosong.setTextFill(warnaSub(isDarkMode));
            kosong.setFont(Font.font(FONT_TEXT, 13));
            previewBox.getChildren().add(kosong);
        }

        Separator sep1 = buatSep(isDarkMode);
        Separator sep2 = buatSep(isDarkMode);

        page.getChildren().addAll(title, rowStat, sep1, lblAksi, rowAksi, sep2, lblPreview, previewBox);

        page.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(350), page);
        fade.setToValue(1.0);
        fade.play();
        return page;
    }

    // ── Halaman kelola orangutan ─────────────────────────────────────────────
    private static VBox buatHalamanKelolaOU(boolean isDarkMode, Runnable callbackSegarkan) {
        VBox page = new VBox(16);
        page.setPadding(new Insets(32, 40, 40, 36));
        page.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Kelola Data Orangutan");
        title.setFont(Font.font(FONT_DISPLAY, FontWeight.BOLD, 20));
        title.setTextFill(warnaTeks(isDarkMode));

        Button btnTambah = new Button("+ Tambah Orangutan Baru");
        btnTambah.setStyle(styleTombolPrimary("#EA580C"));
        btnTambah.setOnAction(e -> { animasiKlik(btnTambah); DashboardPage.tampilkanFormEmbed(null); });

        HBox header = new HBox(16, title);
        header.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        header.getChildren().addAll(sp, btnTambah);

        ObservableList<Orangutan> listOU = DatabaseManager.ambilSemuaOrangutan();
        VBox listBox = new VBox(10);

        if (listOU != null && !listOU.isEmpty()) {
            for (int i = 0; i < listOU.size(); i++) {
                Orangutan ou = listOU.get(i);
                HBox row = buatRowOrangutan(ou, isDarkMode, callbackSegarkan, listBox);
                animasiMasukBaris(row, i);
                listBox.getChildren().add(row);
            }
        } else {
            Label kosong = new Label("Belum ada data orangutan.");
            kosong.setTextFill(warnaSub(isDarkMode));
            kosong.setFont(Font.font(FONT_TEXT, 14));
            listBox.getChildren().add(kosong);
        }

        page.getChildren().addAll(header, buatSep(isDarkMode), listBox);
        page.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(350), page);
        fade.setToValue(1.0);
        fade.play();

        return page;
    }

    // ── Halaman kelola developer ─────────────────────────────────────────────
    private static VBox buatHalamanKelolaDev(boolean isDarkMode, Runnable callbackSegarkan) {
        VBox page = new VBox(16);
        page.setPadding(new Insets(32, 40, 40, 36));
        page.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Kelola Tim Developer");
        title.setFont(Font.font(FONT_DISPLAY, FontWeight.BOLD, 20));
        title.setTextFill(warnaTeks(isDarkMode));

        Button btnTambah = new Button("+ Tambah Developer Baru");
        btnTambah.setStyle(styleTombolPrimary("#38BDF8"));
        btnTambah.setOnAction(e -> { animasiKlik(btnTambah); DashboardPage.bukaFormDeveloperEmbed(null); });

        HBox header = new HBox(16, title);
        header.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        header.getChildren().addAll(sp, btnTambah);

        ObservableList<Developer> listDev = DatabaseManager.ambilSemuaDeveloper();
        VBox listBox = new VBox(10);

        if (listDev != null && !listDev.isEmpty()) {
            for (int i = 0; i < listDev.size(); i++) {
                Developer dev = listDev.get(i);
                HBox row = buatRowDeveloper(dev, isDarkMode, callbackSegarkan, listBox);
                animasiMasukBaris(row, i);
                listBox.getChildren().add(row);
            }
        } else {
            Label kosong = new Label("Belum ada data developer.");
            kosong.setTextFill(warnaSub(isDarkMode));
            kosong.setFont(Font.font(FONT_TEXT, 14));
            listBox.getChildren().add(kosong);
        }

        page.getChildren().addAll(header, buatSep(isDarkMode), listBox);
        page.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(350), page);
        fade.setToValue(1.0);
        fade.play();

        return page;
    }

    // ════════════════════════════════════════════════════════════════════════
    // [FITUR 3] HALAMAN PENGATURAN AI — Update API Key & Model
    // ════════════════════════════════════════════════════════════════════════
    private static VBox buatHalamanPengaturanAI(boolean isDarkMode) {
        VBox page = new VBox(24);
        page.setPadding(new Insets(32, 40, 40, 36));
        page.setStyle("-fx-background-color: transparent;");

        // ── Header ──
        Label title = new Label("⚙️ Pengaturan AI Asisten");
        title.setFont(Font.font(FONT_DISPLAY, FontWeight.BLACK, 22));
        title.setTextFill(warnaTeks(isDarkMode));

        Label subTitle = new Label("Konfigurasi API Key dan model AI untuk fitur LiveChat.");
        subTitle.setFont(Font.font(FONT_TEXT, 13));
        subTitle.setTextFill(warnaSub(isDarkMode));
        subTitle.setWrapText(true);

        Separator sep = buatSep(isDarkMode);

        // ── Card pengaturan ──
        VBox card = new VBox(20);
        card.setPadding(new Insets(28, 32, 28, 32));
        card.setMaxWidth(620);
        card.setStyle(isDarkMode
            ? "-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 18;" +
              "-fx-border-color: rgba(255,255,255,0.10); -fx-border-radius: 18; -fx-border-width: 1.5;"
            : "-fx-background-color: rgba(255,255,255,0.88); -fx-background-radius: 18;" +
              "-fx-border-color: rgba(234,88,12,0.18); -fx-border-radius: 18; -fx-border-width: 1.5;");
        DropShadow cardGlow = new DropShadow(20, Color.web("#EA580C", 0.10));
        card.setEffect(cardGlow);

        // ── Seksi: Groq API Key ──
        Label lblSeksiKey = buatLabelSeksi("🔑  API Key", isDarkMode);

        Label lblKeyHint = new Label("API Key yang digunakan untuk autentikasi ke layanan Groq.");
        lblKeyHint.setFont(Font.font(FONT_TEXT, 12));
        lblKeyHint.setTextFill(warnaSub(isDarkMode));
        lblKeyHint.setWrapText(true);

        // PasswordField untuk key (tersembunyi default) + toggle show/hide
        PasswordField pfApiKey = new PasswordField();
        pfApiKey.setText(DashboardPage.getGroqApiKey());
        pfApiKey.setMaxWidth(Double.MAX_VALUE);
        pfApiKey.setStyle(styleField(isDarkMode));

        TextField tfApiKeyVisible = new TextField();
        tfApiKeyVisible.setText(DashboardPage.getGroqApiKey());
        tfApiKeyVisible.setMaxWidth(Double.MAX_VALUE);
        tfApiKeyVisible.setStyle(styleField(isDarkMode));
        tfApiKeyVisible.setVisible(false); tfApiKeyVisible.setManaged(false);

        // Sinkronisasi dua arah
        pfApiKey.textProperty().addListener((obs, o, n) -> { if (!tfApiKeyVisible.getText().equals(n)) tfApiKeyVisible.setText(n); });
        tfApiKeyVisible.textProperty().addListener((obs, o, n) -> { if (!pfApiKey.getText().equals(n)) pfApiKey.setText(n); });

        FontIcon eyeIco = new FontIcon("fas-eye");
        eyeIco.setIconColor(Color.web(isDarkMode ? "#64748B" : "#EA580C")); eyeIco.setIconSize(14);
        Button btnMataKey = new Button(); btnMataKey.setGraphic(eyeIco);
        btnMataKey.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0 0 0 8;");

        final boolean[] keyTerlihat = {false};
        btnMataKey.setOnAction(e -> {
            keyTerlihat[0] = !keyTerlihat[0];
            if (keyTerlihat[0]) {
                tfApiKeyVisible.setVisible(true); tfApiKeyVisible.setManaged(true);
                pfApiKey.setVisible(false); pfApiKey.setManaged(false);
                eyeIco.setIconLiteral("fas-eye-slash");
            } else {
                pfApiKey.setVisible(true); pfApiKey.setManaged(true);
                tfApiKeyVisible.setVisible(false); tfApiKeyVisible.setManaged(false);
                eyeIco.setIconLiteral("fas-eye");
            }
        });

        StackPane wrapperKey = new StackPane();
        wrapperKey.setMaxWidth(Double.MAX_VALUE);
        StackPane.setAlignment(btnMataKey, Pos.CENTER_RIGHT);
        StackPane.setMargin(btnMataKey, new Insets(0, 10, 0, 0));
        wrapperKey.getChildren().addAll(pfApiKey, tfApiKeyVisible, btnMataKey);

        // ── Seksi: Endpoint URL ──
        Label lblSeksiUrl = buatLabelSeksi("🌐  Endpoint URL API", isDarkMode);

        Label lblUrlHint = new Label("URL endpoint API. Default: Groq (https://api.groq.com/openai/v1/chat/completions). Bisa diganti ke endpoint OpenAI-compatible lainnya.");
        lblUrlHint.setFont(Font.font(FONT_TEXT, 12));
        lblUrlHint.setTextFill(warnaSub(isDarkMode));
        lblUrlHint.setWrapText(true);

        TextField tfUrl = new TextField();
        tfUrl.setText(DashboardPage.getGroqApiUrl());
        tfUrl.setMaxWidth(Double.MAX_VALUE);
        tfUrl.setStyle(styleField(isDarkMode));

        // ── Seksi: Model AI ──
        Label lblSeksiModel = buatLabelSeksi("🤖  Model AI", isDarkMode);

        Label lblModelHint = new Label("ID model yang akan digunakan. Contoh: meta-llama/llama-4-scout-17b-16e-instruct, llama3-8b-8192, mixtral-8x7b-32768.");
        lblModelHint.setFont(Font.font(FONT_TEXT, 12));
        lblModelHint.setTextFill(warnaSub(isDarkMode));
        lblModelHint.setWrapText(true);

        ComboBox<String> cbModel = new ComboBox<>();
        cbModel.setEditable(true);
        cbModel.getItems().addAll(
            "meta-llama/llama-4-scout-17b-16e-instruct",
            "meta-llama/llama-4-maverick-17b-128e-instruct",
            "llama3-8b-8192",
            "llama3-70b-8192",
            "mixtral-8x7b-32768",
            "gemma2-9b-it",
            "deepseek-r1-distill-llama-70b"
        );
        cbModel.setValue(DashboardPage.getGroqModel());
        cbModel.setMaxWidth(Double.MAX_VALUE);
        cbModel.setStyle(isDarkMode
            ? "-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 8; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 8;"
            : "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #EA580C; -fx-border-radius: 8;");

        // ── Info card status saat ini ──
        HBox infoBox = new HBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPadding(new Insets(12, 16, 12, 16));
        infoBox.setStyle(isDarkMode
            ? "-fx-background-color: rgba(56,189,248,0.08); -fx-background-radius: 10; -fx-border-color: rgba(56,189,248,0.20); -fx-border-radius: 10; -fx-border-width: 1;"
            : "-fx-background-color: rgba(56,189,248,0.07); -fx-background-radius: 10; -fx-border-color: rgba(56,189,248,0.25); -fx-border-radius: 10; -fx-border-width: 1;");

        FontIcon icoInfo = new FontIcon("fas-info-circle");
        icoInfo.setIconColor(Color.web("#38BDF8")); icoInfo.setIconSize(14);

        Label lblInfo = new Label("Perubahan langsung aktif saat disimpan — tidak perlu restart aplikasi.");
        lblInfo.setFont(Font.font(FONT_TEXT, 12));
        lblInfo.setTextFill(isDarkMode ? Color.web("#7DD3FC") : Color.web("#0284C7"));
        lblInfo.setWrapText(true);
        HBox.setHgrow(lblInfo, Priority.ALWAYS);
        infoBox.getChildren().addAll(icoInfo, lblInfo);

        // ── Tombol Simpan ──
        Button btnSimpan = new Button("💾  Simpan Pengaturan");
        btnSimpan.setStyle(
            "-fx-background-color: #EA580C; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-padding: 12 30; -fx-background-radius: 10; -fx-cursor: hand;" +
            "-fx-font-family: '" + FONT_TEXT + "'; -fx-font-size: 13;");
        btnSimpan.setOnMouseEntered(e -> btnSimpan.setStyle(
            "-fx-background-color: #C2410C; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-padding: 12 30; -fx-background-radius: 10; -fx-cursor: hand;" +
            "-fx-font-family: '" + FONT_TEXT + "'; -fx-font-size: 13;"));
        btnSimpan.setOnMouseExited(e -> btnSimpan.setStyle(
            "-fx-background-color: #EA580C; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-padding: 12 30; -fx-background-radius: 10; -fx-cursor: hand;" +
            "-fx-font-family: '" + FONT_TEXT + "'; -fx-font-size: 13;"));

        // Tombol Reset ke default
        Button btnReset = new Button("↺  Reset Default");
        btnReset.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: " + (isDarkMode ? "#94A3B8" : "#475569") + ";" +
            "-fx-border-color: " + (isDarkMode ? "rgba(255,255,255,0.15)" : "rgba(15,23,42,0.20)") + ";" +
            "-fx-border-radius: 10; -fx-padding: 12 22; -fx-cursor: hand; -fx-background-radius: 10;" +
            "-fx-font-family: '" + FONT_TEXT + "'; -fx-font-size: 13;");

        btnReset.setOnAction(e -> {
            DashboardPage.tampilkanPopupKonfirmasi(
                "Reset Pengaturan AI",
                "Reset semua pengaturan AI ke nilai default bawaan?",
                () -> {
                    // Reset ke nilai default
                    DashboardPage.setGroqApiKey("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");
                    DashboardPage.setGroqApiUrl("https://api.groq.com/openai/v1/chat/completions");
                    DashboardPage.setGroqModel("meta-llama/llama-4-scout-17b-16e-instruct");

                    // Update field UI
                    pfApiKey.setText(DashboardPage.getGroqApiKey());
                    tfApiKeyVisible.setText(DashboardPage.getGroqApiKey());
                    tfUrl.setText(DashboardPage.getGroqApiUrl());
                    cbModel.setValue(DashboardPage.getGroqModel());

                    DashboardPage.tampilkanNotifikasiStatus("Pengaturan AI direset ke default", true);
                }
            );
        });

        btnSimpan.setOnAction(e -> {
            String apiKey = keyTerlihat[0] ? tfApiKeyVisible.getText().trim() : pfApiKey.getText().trim();
            String url    = tfUrl.getText().trim();
            String model  = cbModel.getValue() != null ? cbModel.getValue().trim() : "";

            // Validasi minimal
            if (apiKey.isEmpty()) {
                DashboardPage.tampilkanNotifikasiStatus("API Key tidak boleh kosong", false);
                return;
            }
            if (url.isEmpty() || (!url.startsWith("http://") && !url.startsWith("https://"))) {
                DashboardPage.tampilkanNotifikasiStatus("URL Endpoint harus dimulai dengan http:// atau https://", false);
                return;
            }
            if (model.isEmpty()) {
                DashboardPage.tampilkanNotifikasiStatus("Nama model tidak boleh kosong", false);
                return;
            }

            // Animasi tombol "menyimpan..."
            btnSimpan.setText("Menyimpan...");
            btnSimpan.setDisable(true);

            // Terapkan ke DashboardPage (berlaku langsung)
            DashboardPage.setGroqApiKey(apiKey);
            DashboardPage.setGroqApiUrl(url);
            DashboardPage.setGroqModel(model);

            // Simulasi delay singkat agar terasa "proses"
            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(500));
            delay.setOnFinished(ev -> {
                btnSimpan.setText("💾  Simpan Pengaturan");
                btnSimpan.setDisable(false);
                DashboardPage.tampilkanNotifikasiStatus("Pengaturan AI berhasil disimpan ✓", true);
            });
            delay.play();
        });

        HBox rowBtn = new HBox(14, btnSimpan, btnReset);
        rowBtn.setAlignment(Pos.CENTER_LEFT);

        // ── Bagian model populer — chip pills ──
        Label lblModelPopuler = new Label("Model Populer:");
        lblModelPopuler.setFont(Font.font(FONT_TEXT, FontWeight.BOLD, 12));
        lblModelPopuler.setTextFill(warnaSub(isDarkMode));

        HBox chipRow = new HBox(8);
        chipRow.setAlignment(Pos.CENTER_LEFT);
        String[] modelList = {
            "llama3-8b-8192", "llama3-70b-8192", "mixtral-8x7b-32768", "gemma2-9b-it"
        };
        for (String m : modelList) {
            Button chip = new Button(m);
            chip.setStyle(
                "-fx-background-color: " + (isDarkMode ? "rgba(255,255,255,0.06)" : "rgba(234,88,12,0.07)") + ";" +
                "-fx-text-fill: " + (isDarkMode ? "#94A3B8" : "#7C2D12") + ";" +
                "-fx-background-radius: 20; -fx-padding: 5 12; -fx-cursor: hand;" +
                "-fx-font-family: '" + FONT_TEXT + "'; -fx-font-size: 11;");
            chip.setOnAction(e -> cbModel.setValue(m));
            chip.setOnMouseEntered(ev -> chip.setStyle(
                "-fx-background-color: rgba(234,88,12,0.18); -fx-text-fill: #EA580C;" +
                "-fx-background-radius: 20; -fx-padding: 5 12; -fx-cursor: hand;" +
                "-fx-font-family: '" + FONT_TEXT + "'; -fx-font-size: 11;"));
            chip.setOnMouseExited(ev -> chip.setStyle(
                "-fx-background-color: " + (isDarkMode ? "rgba(255,255,255,0.06)" : "rgba(234,88,12,0.07)") + ";" +
                "-fx-text-fill: " + (isDarkMode ? "#94A3B8" : "#7C2D12") + ";" +
                "-fx-background-radius: 20; -fx-padding: 5 12; -fx-cursor: hand;" +
                "-fx-font-family: '" + FONT_TEXT + "'; -fx-font-size: 11;"));
            chipRow.getChildren().add(chip);
        }

        // ── Susun isi card ──
        card.getChildren().addAll(
            lblSeksiKey, lblKeyHint, wrapperKey,
            buatSep(isDarkMode),
            lblSeksiUrl, lblUrlHint, tfUrl,
            buatSep(isDarkMode),
            lblSeksiModel, lblModelHint, cbModel, lblModelPopuler, chipRow,
            buatSep(isDarkMode),
            infoBox, rowBtn
        );

        page.getChildren().addAll(title, subTitle, sep, card);

        page.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(350), page);
        fade.setToValue(1.0); fade.play();

        return page;
    }

    // ── Helper label seksi untuk halaman Pengaturan ──────────────────────────
    private static Label buatLabelSeksi(String teks, boolean dark) {
        Label lbl = new Label(teks);
        lbl.setFont(Font.font(FONT_DISPLAY, FontWeight.BOLD, 14));
        lbl.setTextFill(warnaTeks(dark));
        return lbl;
    }

    // ── Style field khusus Pengaturan ─────────────────────────────────────────
    private static String styleField(boolean dark) {
        return dark
            ? "-fx-background-color: rgba(255,255,255,0.04); -fx-text-fill: white; -fx-background-radius: 8;" +
              "-fx-padding: 10 12; -fx-border-color: rgba(255,255,255,0.10); -fx-border-radius: 8;"
            : "-fx-background-color: white; -fx-text-fill: #0F172A; -fx-background-radius: 8;" +
              "-fx-padding: 10 12; -fx-border-color: rgba(234,88,12,0.35); -fx-border-radius: 8;";
    }

    // ── Row orangutan ────────────────────────────────────────────────────────
    private static HBox buatRowOrangutan(Orangutan ou, boolean isDarkMode, Runnable callbackSegarkan, VBox listBox) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(13, 20, 13, 20));
        row.setStyle(styleRow(isDarkMode));

        row.setOnMouseEntered(e -> row.setStyle(styleRowHover(isDarkMode, "#EA580C")));
        row.setOnMouseExited(e  -> row.setStyle(styleRow(isDarkMode)));

        FontIcon ico = new FontIcon("fas-paw");
        ico.setIconColor(Color.web("#EA580C")); ico.setIconSize(15);

        Label lblNama = new Label(ou.getNama().toUpperCase() + "  •  " + ou.getSpesies());
        lblNama.setFont(Font.font(FONT_TEXT, FontWeight.BOLD, 13));
        lblNama.setTextFill(warnaTeks(isDarkMode));

        Label badge = new Label(ou.getStatusKonservasi());
        badge.setFont(Font.font(FONT_TEXT, FontWeight.BOLD, 10));
        badge.setTextFill(Color.WHITE);
        badge.setPadding(new Insets(3, 10, 3, 10));
        badge.setStyle("-fx-background-color: #EA580C; -fx-background-radius: 10;");

        Label lblInfo = new Label(ou.getJenisKelamin() + "  •  " + ou.getUmur() + " thn");
        lblInfo.setFont(Font.font(FONT_TEXT, 11));
        lblInfo.setTextFill(warnaSub(isDarkMode));

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEdit = buatTombolAksi("fas-edit", "#F59E0B", isDarkMode);
        btnEdit.setOnAction(e -> { animasiKlik(btnEdit); DashboardPage.tampilkanFormEmbed(ou); });

        Button btnHapus = buatTombolAksi("fas-trash-alt", "#EF4444", isDarkMode);
        btnHapus.setOnAction(e -> {
            animasiKlik(btnHapus);
            DashboardPage.tampilkanPopupKonfirmasi(
                "Hapus Data Orangutan",
                "Yakin hapus \"" + ou.getNama() + "\"? Tidak bisa dibatalkan.",
                () -> animasiHapusBaris(row, listBox, () -> {
                    DatabaseManager.hapusOrangutan(ou.getId());
                    DashboardPage.tampilkanNotifikasiStatus("Biodata " + ou.getNama() + " dihapus", true);
                    callbackSegarkan.run();
                })
            );
        });

        row.getChildren().addAll(ico, lblNama, badge, lblInfo, spacer, btnEdit, btnHapus);
        return row;
    }

    // ── Row developer ────────────────────────────────────────────────────────
    private static HBox buatRowDeveloper(Developer dev, boolean isDarkMode, Runnable callbackSegarkan, VBox listBox) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(13, 20, 13, 20));
        row.setStyle(styleRow(isDarkMode));

        row.setOnMouseEntered(e -> row.setStyle(styleRowHover(isDarkMode, "#38BDF8")));
        row.setOnMouseExited(e  -> row.setStyle(styleRow(isDarkMode)));

        FontIcon ico = new FontIcon("fas-user-cog");
        ico.setIconColor(Color.web("#38BDF8")); ico.setIconSize(15);

        Label lblNama = new Label(dev.getNama() + "  •  " + dev.getNim());
        lblNama.setFont(Font.font(FONT_TEXT, FontWeight.BOLD, 13));
        lblNama.setTextFill(warnaTeks(isDarkMode));

        Label badge = new Label("KELAS " + dev.getKelas().toUpperCase());
        badge.setFont(Font.font(FONT_TEXT, FontWeight.BOLD, 10));
        badge.setTextFill(Color.WHITE);
        badge.setPadding(new Insets(3, 10, 3, 10));
        badge.setStyle("-fx-background-color: #38BDF8; -fx-background-radius: 10;");

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEdit = buatTombolAksi("fas-edit", "#F59E0B", isDarkMode);
        btnEdit.setOnAction(e -> { animasiKlik(btnEdit); DashboardPage.bukaFormDeveloperEmbed(dev); });

        Button btnHapus = buatTombolAksi("fas-trash-alt", "#EF4444", isDarkMode);
        btnHapus.setOnAction(e -> {
            animasiKlik(btnHapus);
            DashboardPage.tampilkanPopupKonfirmasi(
                "Hapus Developer",
                "Yakin hapus \"" + dev.getNama() + "\" dari Tim Developer?",
                () -> animasiHapusBaris(row, listBox, () -> {
                    DatabaseManager.hapusDeveloper(dev.getId());
                    DashboardPage.tampilkanNotifikasiStatus(dev.getNama() + " dihapus", true);
                    callbackSegarkan.run();
                })
            );
        });

        row.getChildren().addAll(ico, lblNama, badge, spacer, btnEdit, btnHapus);
        return row;
    }

    // ── Card statistik ────────────────────────────────────────────────────────
    private static VBox buatCardStat(String label, int value, String color, boolean dark) {
        VBox c = new VBox(10);
        c.setPrefWidth(220); c.setPadding(new Insets(22));
        c.setStyle(dark
            ? "-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 16;" +
              "-fx-border-color: rgba(255,255,255,0.10); -fx-border-radius: 16; -fx-border-width: 1.5;"
            : "-fx-background-color: rgba(255,255,255,0.82); -fx-background-radius: 16;" +
              "-fx-border-color: rgba(234,88,12,0.15); -fx-border-radius: 16; -fx-border-width: 1.5;");

        DropShadow glow = new DropShadow(16, Color.web(color, 0.14));
        c.setEffect(glow);

        Label lblTitle = new Label(label);
        lblTitle.setFont(Font.font(FONT_TEXT, 13));
        lblTitle.setTextFill(dark ? Color.web("#94A3B8") : Color.web("#475569"));

        Label lblVal = new Label("0");
        lblVal.setFont(Font.font(FONT_DISPLAY, FontWeight.BLACK, 36));
        lblVal.setTextFill(dark ? Color.WHITE : Color.web("#0F172A"));
        lblVal.setUserData("value-label");

        c.getChildren().addAll(lblTitle, lblVal);
        return c;
    }

    private static void animasiCounter(VBox card, int target, boolean dark) {
        for (javafx.scene.Node node : card.getChildren()) {
            if (node instanceof Label && "value-label".equals(node.getUserData())) {
                Label lbl = (Label) node;
                int steps = Math.max(target, 1);
                Timeline tl = new Timeline();
                for (int i = 0; i <= steps; i++) {
                    final int val = i;
                    tl.getKeyFrames().add(new KeyFrame(
                        Duration.millis((double) i / steps * 800),
                        e -> lbl.setText(String.valueOf(val))
                    ));
                }
                tl.play(); break;
            }
        }
    }

    // ── Preview row ringkasan ─────────────────────────────────────────────────
    private static HBox buatRowPreview(String nama, String sub, String badge, String badgeColor, boolean dark) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setStyle(styleRow(dark));

        FontIcon ico = new FontIcon("fas-paw");
        ico.setIconColor(Color.web(badgeColor)); ico.setIconSize(13);

        Label lblNama = new Label(nama);
        lblNama.setFont(Font.font(FONT_TEXT, FontWeight.BOLD, 13));
        lblNama.setTextFill(dark ? Color.WHITE : Color.web("#0F172A"));

        Label lblSub = new Label(sub);
        lblSub.setFont(Font.font(FONT_TEXT, 11));
        lblSub.setTextFill(dark ? Color.web("#94A3B8") : Color.web("#475569"));

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label lblBadge = new Label(badge);
        lblBadge.setFont(Font.font(FONT_TEXT, FontWeight.BOLD, 10));
        lblBadge.setTextFill(Color.WHITE);
        lblBadge.setPadding(new Insets(3, 9, 3, 9));
        lblBadge.setStyle("-fx-background-color: " + badgeColor + "; -fx-background-radius: 10;");

        row.getChildren().addAll(ico, lblNama, lblSub, sp, lblBadge);
        return row;
    }

    // ── Tombol navigasi sidebar ───────────────────────────────────────────────
    private static Button buatNavBtn(String icon, String teks, String navId, boolean dark) {
        Button btn = new Button(teks);
        FontIcon fi = new FontIcon(icon);
        fi.setIconColor(dark ? Color.web("#94A3B8") : Color.web("#475569")); fi.setIconSize(14);
        btn.setGraphic(fi);
        btn.setUserData(navId);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(styleNavBtn(false, dark));
        btn.setOnMouseEntered(e -> { if (!"aktif".equals(btn.getProperties().get("state"))) btn.setStyle(styleNavBtnHover(dark)); });
        btn.setOnMouseExited(e  -> { if (!"aktif".equals(btn.getProperties().get("state"))) btn.setStyle(styleNavBtn(false, dark)); });
        return btn;
    }

    private static void setNavAktif(Button btn, boolean dark) {
        btn.setStyle(styleNavBtn(true, dark));
        btn.getProperties().put("state", "aktif");
        if (btn.getGraphic() instanceof FontIcon) {
            ((FontIcon) btn.getGraphic()).setIconColor(Color.web("#EA580C"));
        }
    }

    private static void setSemuaTidakAktif(VBox sidebar, boolean dark) {
        sidebar.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button b = (Button) node;
                b.setStyle(styleNavBtn(false, dark));
                b.getProperties().put("state", "");
                if (b.getGraphic() instanceof FontIcon) {
                    ((FontIcon) b.getGraphic()).setIconColor(dark ? Color.web("#94A3B8") : Color.web("#475569"));
                }
            }
        });
    }

    // ── Ganti konten area dengan animasi ─────────────────────────────────────
    private static void gatiKonten(StackPane area, javafx.scene.Node kontenBaru) {
        if (!area.getChildren().isEmpty()) {
            javafx.scene.Node lama = area.getChildren().get(0);
            FadeTransition ftOut = new FadeTransition(Duration.millis(160), lama);
            ftOut.setToValue(0.0);
            ftOut.setOnFinished(ev -> {
                area.getChildren().setAll(kontenBaru);
                kontenBaru.setOpacity(0);
                FadeTransition fade = new FadeTransition(Duration.millis(250), kontenBaru);
                fade.setToValue(1.0);
                fade.play();
            });
            ftOut.play();
        } else {
            area.getChildren().setAll(kontenBaru);
            kontenBaru.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(250), kontenBaru);
            fade.setToValue(1.0);
            fade.play();
        }
    }

    // ── Style helpers ─────────────────────────────────────────────────────────
    private static String styleNavBtn(boolean aktif, boolean dark) {
        if (aktif) {
            return dark
                ? "-fx-background-color: rgba(234,88,12,0.18); -fx-text-fill: #EA580C; -fx-font-weight: bold;" +
                  "-fx-background-radius: 10; -fx-padding: 10 14; -fx-cursor: hand; -fx-font-family: '" + FONT_TEXT + "';" +
                  "-fx-border-color: rgba(234,88,12,0.30); -fx-border-radius: 10; -fx-border-width: 1;"
                : "-fx-background-color: rgba(234,88,12,0.10); -fx-text-fill: #EA580C; -fx-font-weight: bold;" +
                  "-fx-background-radius: 10; -fx-padding: 10 14; -fx-cursor: hand; -fx-font-family: '" + FONT_TEXT + "';" +
                  "-fx-border-color: rgba(234,88,12,0.25); -fx-border-radius: 10; -fx-border-width: 1;";
        }
        return dark
            ? "-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-background-radius: 10;" +
              "-fx-padding: 10 14; -fx-cursor: hand; -fx-font-family: '" + FONT_TEXT + "';"
            : "-fx-background-color: transparent; -fx-text-fill: #475569; -fx-background-radius: 10;" +
              "-fx-padding: 10 14; -fx-cursor: hand; -fx-font-family: '" + FONT_TEXT + "';";
    }

    private static String styleNavBtnHover(boolean dark) {
        return dark
            ? "-fx-background-color: rgba(255,255,255,0.06); -fx-text-fill: white; -fx-background-radius: 10;" +
              "-fx-padding: 10 14; -fx-cursor: hand; -fx-font-family: '" + FONT_TEXT + "';"
            : "-fx-background-color: rgba(234,88,12,0.08); -fx-text-fill: #EA580C; -fx-background-radius: 10;" +
              "-fx-padding: 10 14; -fx-cursor: hand; -fx-font-family: '" + FONT_TEXT + "';";
    }

    private static String styleTombolPrimary(String color) {
        return "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;" +
               "-fx-padding: 9 18; -fx-cursor: hand; -fx-background-radius: 10;";
    }

    private static String styleRow(boolean dark) {
        return dark
            ? "-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 12;" +
              "-fx-border-color: rgba(255,255,255,0.07); -fx-border-radius: 12; -fx-border-width: 1;"
            : "-fx-background-color: rgba(255,255,255,0.82); -fx-background-radius: 12;" +
              "-fx-border-color: rgba(15,23,42,0.08); -fx-border-radius: 12; -fx-border-width: 1;";
    }

    private static String styleRowHover(boolean dark, String accent) {
        return dark
            ? "-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 12;" +
              "-fx-border-color: " + accent + "; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-cursor: hand;"
            : "-fx-background-color: rgba(255,255,255,0.97); -fx-background-radius: 12;" +
              "-fx-border-color: " + accent + "; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-cursor: hand;";
    }

    private static Button buatTombolAksi(String icon, String color, boolean dark) {
        Button btn = new Button();
        FontIcon fi = new FontIcon(icon); fi.setIconColor(Color.web(color)); fi.setIconSize(14);
        btn.setGraphic(fi);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 6 10; -fx-background-radius: 8;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(0,0,0,0.08); -fx-cursor: hand; -fx-padding: 6 10; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e  -> btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 6 10; -fx-background-radius: 8;"));
        return btn;
    }

    private static Button buatTombolAksiCepat(String teks, String color, boolean dark) {
        Button btn = new Button(teks);
        btn.setStyle(
            "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-padding: 9 16; -fx-cursor: hand; -fx-background-radius: 10; -fx-font-size: 12;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e  -> btn.setOpacity(1.0));
        return btn;
    }

    private static Label buatLabelGrup(String teks, boolean dark) {
        Label lbl = new Label(teks);
        lbl.setFont(Font.font(FONT_TEXT, FontWeight.BOLD, 10));
        lbl.setTextFill(Color.web("#64748B"));
        lbl.setPadding(new Insets(10, 14, 4, 14));
        return lbl;
    }

    private static Separator buatSep(boolean dark) {
        Separator sep = new Separator();
        sep.setStyle(dark ? "-fx-background-color: rgba(255,255,255,0.07);" : "-fx-background-color: rgba(15,23,42,0.08);");
        return sep;
    }

    // ── Animasi ───────────────────────────────────────────────────────────────
    private static void animasiKlik(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(110), btn);
        st.setToX(0.91); st.setToY(0.91); st.setAutoReverse(true); st.setCycleCount(2); st.play();
    }

    private static void animasiMasukBaris(HBox row, int index) {
        row.setOpacity(0); row.setTranslateX(-18);
        int delay = index * 55;
        FadeTransition ft = new FadeTransition(Duration.millis(280), row);
        ft.setToValue(1.0); ft.setDelay(Duration.millis(delay)); ft.play();
        TranslateTransition tt = new TranslateTransition(Duration.millis(280), row);
        tt.setToX(0); tt.setDelay(Duration.millis(delay)); tt.play();
    }

    private static void animasiHapusBaris(HBox row, VBox container, Runnable aksi) {
        FadeTransition ft = new FadeTransition(Duration.millis(220), row); ft.setToValue(0.0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(220), row); tt.setToX(50);
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.setOnFinished(e -> {
            container.getChildren().remove(row);
            aksi.run();
        });
        pt.play();
    }
}