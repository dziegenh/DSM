package de.uos.se.prom.dsmproject.gui.onlineDialog;

import javax.inject.Inject;

import de.uos.se.prom.dsmproject.bl.ServerController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;

public class SendProjectDialog {
	
	
	@Inject
	ServerController serverController;
	
	public void createDialog() {
		Dialog dialog = createSendProjectDialog();
	    dialog.showAndWait();
	}

	private Dialog<Boolean> createSendProjectDialog() {
		
		Dialog<Boolean> dialog = new Dialog<>();
		
		// create panel containing attribute fields
        SendProjectDialogView view = new SendProjectDialogView();
        dialog.getDialogPane().setContent(view.getView());
        
        dialog.setTitle("Online - Send Project");
        dialog.setResizable(false);
        
        // Add button to dialog
        ButtonType buttonTypeSend = new ButtonType("Send", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel,buttonTypeSend);
        
        //Handle Event Load Project pressed
        final Button btSd = (Button) dialog.getDialogPane().lookupButton(buttonTypeSend);
        btSd.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				serverController.sendProject(view.getRealPresenter().getProjectName());
				
				//prevent the dialog to close
				event.consume();
			}
        	
        });
        
        return dialog;
	}
}
