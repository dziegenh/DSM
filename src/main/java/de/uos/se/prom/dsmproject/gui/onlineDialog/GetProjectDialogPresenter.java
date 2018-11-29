package de.uos.se.prom.dsmproject.gui.onlineDialog;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GetProjectDialogPresenter implements Initializable{
	

	@FXML
    private TextField projectName;
	
	@FXML
    private TextArea projectListArea;
	
	@FXML
	private TextArea statusFieldOnline;
	
	@FXML
	private TextArea statusFieldProject;
	
	@FXML
	private CheckBox liveMode;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	/**
	 * Print Online Status to Status Text Field
	 * @param status
	 */
	public void set_statusFieldOnline(String status) {
		statusFieldOnline.setText(status);
	}
	
	/**
	 * Print the Read/Write Status to Status Text Field
	 * @param status
	 */
	public void set_statusFieldProject(String status) {
		statusFieldProject.setText(status);
	}
	
	/**
	 * Clear the Online Status Text Field
	 */
	public void clear_statusFieldOnline() {
		statusFieldOnline.clear();
	}
	
	/**
	 * Clear the Project Status Text Field
	 */
	public void clear_statusFieldProject() {
		statusFieldProject.clear();
	}
	
	/**
	 * Get the Project Name 
	 * @return projectName
	 */
    public String getProjectName() {
        return projectName.getText().trim();
    }
    
    
    /**
     * Print the Projectlist in the Projectlist Field
     * @param projects
     */
    public void setProjectList(List<String> projects) {
    	projectListArea.clear();
    	for (String entry: projects) {
			projectListArea.appendText(entry+"\n");
		}
    		
    }
    
    /**
     * Clear ProjectList Field
     */
    public void clearProjectList() {
    	projectListArea.clear();
    }
    
    /**
     * Get the Status of Live CheckBox
     * @return true if CheckBox is selected
     */
    public boolean getLiveModeStatus() {
    	if(liveMode.isSelected()) return true;
    	else return false;
    }
    
    public void appendProject(String project) {
    	
    }

}
