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
		
		// create panel
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
				int resp_status = serverController.sendProject(view.getRealPresenter().getProjectName());
				
				if(resp_status == 200 || resp_status == 201) {
					view.getRealPresenter().set_statusField("Sending Project was successful! " + "HTTP Code: "+ resp_status);
				}
				
				//If 0 returned probably no connection to server (Exception occured)
				else if(resp_status == 0) {
					view.getRealPresenter().set_statusField("Problem occured! Could not connect to Server!");
				}
				
				else {
					view.getRealPresenter().set_statusField("There is a problem with sending Project to Server! HTTP Code: " + resp_status);
				}
				
				//prevent the dialog to close
				event.consume();
			}
        	
        });
        
        
        return dialog;
	}
}
