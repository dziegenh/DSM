package de.uos.se.prom.dsmproject.gui.onlineDialog;

import java.util.List;

import javax.inject.Inject;
import de.uos.se.prom.dsmproject.bl.ServerController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
		
		// create panel
        GetProjectDialogView view = new GetProjectDialogView();
        
        dialog.setTitle("Online - Get Project");
        dialog.setResizable(false);
        
        dialog.getDialogPane().setContent(view.getView());
		
		// Add button to dialog
        ButtonType buttonTypeCancel = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
        ButtonType buttonTypeListProjects = new ButtonType("List Projects");
        ButtonType buttonTypeStatus = new ButtonType("Status");
        ButtonType buttonTypeLoadProject = new ButtonType("Load Project");

        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel, buttonTypeListProjects, buttonTypeLoadProject, buttonTypeStatus);
    
        
        //Handle Event Get Project pressed
        final Button btLd = (Button) dialog.getDialogPane().lookupButton(buttonTypeLoadProject);
        btLd.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				//Clear the Status Field
				view.getRealPresenter().clear_statusFieldOnline();
				view.getRealPresenter().clear_statusFieldProject();
				
				String projectname = view.getRealPresenter().getProjectName();
				 
				int resp_status;
				 
				 //call get Project with LiveMode or without LiveMode
				if(view.getRealPresenter().getLiveModeStatus()) {
					resp_status = serverController.getProject(projectname,true);
				}
				 
				else resp_status = serverController.getProject(projectname,false);
				
				//if anything went wrong with receiving project
				if(resp_status != 200) {
					 
					//if 0 returned could not connect to server or projectname Field is emtpy
					if(resp_status == 0) {
						view.getRealPresenter().set_statusFieldOnline("Problem occured! Could not connect to Server or Projectname Field is empty!");
					}
					 
					//else print the HTTP Code
					else {
						view.getRealPresenter().set_statusFieldOnline("Could not get Project! HTTP Code: " + resp_status );
					}
					 
				}
				//everything was successful
				else {
					view.getRealPresenter().set_statusFieldOnline("Receiving Project was successful! HTTP Code: " + resp_status);
				}

				//prevent the dialog to close
				event.consume();
			}
        	
        });
        

        
        
        //Handle Event List Projects is pressed
        final Button btLst = (Button) dialog.getDialogPane().lookupButton(buttonTypeListProjects);
        btLst.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				//Clear the Status Fields
				view.getRealPresenter().clear_statusFieldOnline();
				view.getRealPresenter().clear_statusFieldProject();
				
				List<String> projects = serverController.getProjectList();
				
				if(projects == null) {
					view.getRealPresenter().clearProjectList();
					view.getRealPresenter().set_statusFieldOnline("Project List could not be loaded or no Projects exists!");
				}
				else {
					view.getRealPresenter().setProjectList(projects);
					
				}
				
				 //prevent the dialog to close
				event.consume();
			}	
        });
        
        
        //Handle Status pressed
        final Button btSt = (Button) dialog.getDialogPane().lookupButton(buttonTypeStatus);
        btSt.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				//Clear the Status Fields
				view.getRealPresenter().clear_statusFieldOnline();
				view.getRealPresenter().clear_statusFieldProject();
				
				//Get the Projectname and Access Status
				String projectname = view.getRealPresenter().getProjectName();
				String accStatus = serverController.getProjectStatus(projectname);
				 
				//Check Status and print result in Status Field
				if(accStatus.equals("Write")) view.getRealPresenter().set_statusFieldProject("You have full Access (Write/Read) to this Project!");
				else if (accStatus.equals("Read")) view.getRealPresenter().set_statusFieldProject("You can only Read the Project without Write Access Right!");
				else view.getRealPresenter().set_statusFieldOnline("There was a Problem with Connection to Server!");
				 
				 //prevent the dialog to close
				 event.consume();
				
			}
        	
        });

        return dialog;
	}
}
