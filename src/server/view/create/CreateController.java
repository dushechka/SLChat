package server.view.create;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import server.model.Server;

public class CreateController {
    @FXML
    Button openButton;

    @FXML
    protected void handleOpenButtonAction(ActionEvent event) {
        new Server("SRV","TalkDirtyToMe");
    }
}
