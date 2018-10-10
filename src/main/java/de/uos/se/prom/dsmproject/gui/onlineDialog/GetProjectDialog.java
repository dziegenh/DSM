package de.uos.se.prom.dsmproject.gui.onlineDialog;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import de.uos.se.prom.dsmproject.bl.ArtifactEditor;
import de.uos.se.prom.dsmproject.bl.ServerController;
import de.uos.se.prom.dsmproject.gui.artifactProperties.ArtifactPropertiesView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;

public class GetProjectDialog {
	 
	
	@Inject
	ServerController serverController;
	 
	public void createDialog() {
		 Dialog dialog = createGetProjectDialog();
	     dialog.showAndWait();
	}

	
	private Dialog<Boolean> createGetProjectDialog() {
		Dialog<Boolean> dialog = new Dialog<>();
		
		// create panel containing attribute fields
        GetProjectDialogView view = new GetProjectDialogView();
        
        dialog.setTitle("Online - Get Project");
        dialog.setResizable(false);
        
        //view.getRealPresenter().setArtifact(artifact);
        dialog.getDialogPane().setContent(view.getView());
		
		// Add button to dialog
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        ButtonType buttonTypeListProjects = new ButtonType("List Projects");
        ButtonType buttonTypeGetProject = new ButtonType("Get Project");
        ButtonType buttonTypeLoadProject = new ButtonType("Load Project");
        
        CheckBox live = new CheckBox("live");
        
     
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel, buttonTypeListProjects, buttonTypeGetProject, buttonTypeLoadProject);
    
        
        //Handle Event Get Project pressed
        final Button btGt = (Button) dialog.getDialogPane().lookupButton(buttonTypeGetProject);
        btGt.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				 String projectname = view.getRealPresenter().getProjectName();
				 int resp_status = serverController.getProject(projectname);
				
				 //if anything went wrong with receiving project
				 if(resp_status != 200) {
					 
					 //if 0 returned could not connect to server or projectname Field is emtpy
					 if(resp_status == 0) {
						 view.getRealPresenter().set_statusField("Problem occured! Could not connect to Server or Projectname Field is empty!");
					 }
					 
					 //else print the HTTP Code
					 else {
						 view.getRealPresenter().set_statusField("Could not get Project! HTTP Code: " + resp_status );
					 }
					 
				 }
				 //everything was successful
				 else {
					 view.getRealPresenter().set_statusField("Receiving Project was successful! HTTP Code: " + resp_status);
				 }

				 //prevent the dialog to close
				 event.consume();
			}
        	
        });
        
        //Handle Event Load Project pressed
        final Button btLd = (Button) dialog.getDialogPane().lookupButton(buttonTypeLoadProject);
        btLd.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				if(view.getRealPresenter().getLiveModeStatus()) {
					
					//Live Mode
					
					serverController.loadProjectLive();
				}
				else {
					
					//Normal Mode
					
					if(!serverController.loadProject()) {
						view.getRealPresenter().set_statusField("Project could not be loaded! You have to Get a Project first!");
						//prevent the dialog to close
						 event.consume();
					}
				}
			}	
        });
        
        
        //Handle Event List Projects is pressed
        final Button btLst = (Button) dialog.getDialogPane().lookupButton(buttonTypeListProjects);
        btLst.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				List<String> projects = serverController.getProjectList();
				
				if(projects == null) {
					view.getRealPresenter().set_statusField("Project List could not be loaded!");
				}
				else {
					view.getRealPresenter().setProjectList(projects);
					
				}
				
				 //prevent the dialog to close
				event.consume();
			}	
        });
        

        
        
        return dialog;
	}
}
