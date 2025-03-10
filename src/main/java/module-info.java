module com.example.automation {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.automation to javafx.fxml;
    exports com.example.automation;
}