module com.example.cmsc23_mp4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cmsc23_mp4 to javafx.fxml;
    exports com.example.cmsc23_mp4;
}