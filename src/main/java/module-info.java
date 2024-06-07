module org.example.ds_teamwork02 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.ds_teamwork02 to javafx.fxml;
    exports org.example.ds_teamwork02;
}