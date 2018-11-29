package de.uos.se.prom.dsmproject.gui.onlineDialog;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PreferencesDialogPresenter implements Initializable {
	@FXML
	private TextField host;
	
	@FXML
	private TextField port;
	
	@FXML
	private TextArea statusField;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Get the Host written in TextField
	 * @return Host
	 */
	public String getHost() {
		return host.getText().trim();
	}
	
	/**
	 * Get the Port written in TextField
	 * @return
	 */
	public String getPort() {
		return port.getText().trim();
	}
	
	/**
	 * Set Text on Status Field
	 * @param status
	 */
	public void setStatus(String status) {
		this.statusField.setText(status);
	}
	
	
	/**
	 * Clear the Status Field
	 */
	public void clearStatus() {
		this.statusField.clear();
	}
}
