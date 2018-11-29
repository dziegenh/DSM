package de.uos.se.prom.dsmproject.gui.onlineDialog;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import de.uos.se.prom.dsmproject.bl.ServerController;
import de.uos.se.prom.dsmproject.bl.event.EventBus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SendProjectDialogPresenter implements Initializable{
	
	@Inject
	ServerController serverController;
	
	
	@FXML
	private TextField projectName;
	
	@FXML
	private TextArea statusField;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		 if(!serverController.getLoadedProjectFilename().isEmpty()) {
			 projectName.setText(serverController.getLoadedProjectFilename());
		 }
	}
	
	/**
	 * Get Name of projectName Field
	 * @return projectName
	 */
	public String getProjectName() {
		return projectName.getText().trim();
	}
	
	/**
	 * Print Text to Status Field 
	 * @param status 
	 */
	public void set_statusField(String status) {
		statusField.setText(status);
	}

}
