package de.uos.se.prom.dsmproject.gui.onlineDialog;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class SendProjectDialogPresenter implements Initializable{
	
	@FXML
	private TextField projectName;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	public String getProjectName() {
		return projectName.getText().trim();
	}

}
