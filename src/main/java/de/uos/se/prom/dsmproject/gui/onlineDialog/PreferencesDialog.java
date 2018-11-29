package de.uos.se.prom.dsmproject.gui.onlineDialog;

import javax.inject.Inject;

import de.uos.se.prom.dsmproject.bl.ServerController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;

public class PreferencesDialog {
	
	
	@Inject
	ServerController serverController;
	
	public void createDialog() {
		 Dialog dialog = createPreferencesDialog();
	     dialog.showAndWait();
	}
	
	private Dialog<Boolean> createPreferencesDialog(){
		Dialog<Boolean> dialog = new Dialog<>();
		
		// create panel
        PreferencesDialogView view = new PreferencesDialogView();
        
        dialog.setTitle("Online - Preferences");
        dialog.setResizable(false);
        
        dialog.getDialogPane().setContent(view.getView());
        
        //Add buttons to dialog
        ButtonType buttonTypeCancel = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
        ButtonType buttonTypeSetPreferences = new ButtonType("Set Preferences", ButtonData.OK_DONE);
        
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeSetPreferences, buttonTypeCancel);
        
        //Handle Event Set Preferences pressed 
        final Button btSt = (Button) dialog.getDialogPane().lookupButton(buttonTypeSetPreferences);
        btSt.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String host = view.getRealPresenter().getHost();
				String port = view.getRealPresenter().getPort();
				
				if(!host.isEmpty()) ServerController.setHost(host);
				if(!port.isEmpty()) ServerController.setPort(port);
				
				//prevent the dialog to close
				event.consume();
				
				//Set Info to Status Field whether Server could be connected
				if(serverController.testConnection()) {
					view.getRealPresenter().clearStatus();
					view.getRealPresenter().setStatus("Preferences successfully set!");
				}
				else {
					view.getRealPresenter().clearStatus();
					view.getRealPresenter().setStatus("Unable to set Preferences! Server not available!");
				}
				
				
			}
        	
        });
        
        return dialog;
	}

}
