module org.example.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    // Open packages to FXML
    opens org.example.javafx to javafx.fxml;
    opens org.example.javafx.Controller to javafx.fxml;
    opens org.example.javafx.Models to javafx.fxml; // <-- add this if models are bound or referenced in FXML

    // Export to allow other modules (or JavaFX internals) to see your classes
    exports org.example.javafx.UI;
    exports org.example.javafx.Controller;
    exports org.example.javafx.Models; // <-- if models are used anywhere externally
}