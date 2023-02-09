module us.malfeasant.logalyzer {
    requires javafx.controls;
    requires javafx.fxml;

    opens us.malfeasant.logalyzer to javafx.fxml;
    exports us.malfeasant.logalyzer;
}
