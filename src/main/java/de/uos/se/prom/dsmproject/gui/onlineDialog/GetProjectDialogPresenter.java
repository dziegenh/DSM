package de.uos.se.prom.dsmproject.gui.onlineDialog;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GetProjectDialogPresenter implements Initializable{
	

	@FXML
    private TextField projectName;
	
	@FXML
    private TextArea projectListArea;
	
	@FXML
	private TextArea statusField;
	
	@FXML
	private CheckBox liveMode;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	/*
	 * Set the Status Field
	 */
	public void set_statusField(String status) {
		statusField.setText(status);
	}
	
	/*
	 * Get the Name in the Project Name Field
	 */
    String getProjectName() {
        return projectName.getText().trim();
    }
    
    
    /*
     * Print the Projectlist in the Field
     */
    public void setProjectList(List<String> projects) {
    	projectListArea.clear();
    	for (String entry: projects) {
			projectListArea.appendText(entry+"\n");
		}
    		
    }
    
    /*
     * Get Status of Live is checked or not
     */
    public boolean getLiveModeStatus() {
    	if(liveMode.isSelected()) return true;
    	else return false;
    }

}
