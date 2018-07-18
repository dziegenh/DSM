package de.uos.se.prom.dsmproject.gui.typeVisibility;

import de.uos.se.prom.dsmproject.bl.ViewEditor;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javax.inject.Inject;

/**
 * Launcher for attribute edit dialogs.
 *
 * @author dziegenh
 */
public class TypeVisibilityDialog {

    @Inject
    ViewEditor viewEditor;

    public void createEditorDialog() {
        Dialog dialog = createDialog();
        dialog.showAndWait();
    }

    private Dialog<Boolean> createDialog() {

        Dialog<Boolean> dialog = new Dialog<>();

        dialog.setTitle("Select types to hide");
        dialog.setResizable(false);

        // create panel containing attribute fields
        TypeVisibilityView view = new TypeVisibilityView();
        dialog.getDialogPane().setContent(view.getView());

        // Add button to dialog
        ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final TypeVisibilityPresenter presenter = view.getRealPresenter();
                if (presenter.hiddenTypesChanged()) {
                    List<Artifacttype> hiddenTypes = presenter.getHiddenTypes();
                    viewEditor.setHiddenTypes(hiddenTypes);
                }
            }
        });

        return dialog;
    }

}
