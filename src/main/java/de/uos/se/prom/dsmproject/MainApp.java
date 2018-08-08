package de.uos.se.prom.dsmproject;

import com.airhacks.afterburner.injection.Injector;
import de.uos.se.prom.dsmproject.bl.AppLogic;
import de.uos.se.prom.dsmproject.gui.app.AppView;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public final static String DEFAULT_WINDOW_TITLE = "DSM Tool";

    @Override
    public void start(Stage stage) throws Exception {
        AppView view = new AppView();
        Scene scene = new Scene(view.getView());

        stage.setTitle(DEFAULT_WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();

        AppLogic appLogic = Injector.instantiateModelOrService(AppLogic.class);
        appLogic.postGui();

        stage.setOnCloseRequest((event) -> {
            appLogic.preClose();
        });
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Necessary for improved graphstream visualisation
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        launch(args);
    }

}
