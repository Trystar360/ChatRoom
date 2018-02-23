package ClientGUI;

import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientGUI extends Application {
	
	private Stage serverStage;
		
    @Override
    public void start(Stage serverStage) throws Exception {
    	
        Parent root = new Parent() {};
        Scene scene = new Scene(root);
        
	    	ScreenController screenController = new ScreenController(scene);
	    	screenController.addScreen("ServerSelect", FXMLLoader.load(getClass().getResource( "gui/ServerSelect.fxml" )));
	    	screenController.activate("ServerSelect");
        
        serverStage.setScene(scene);
        serverStage.setTitle("Chat");
        serverStage.show();
    }
    
    public void SetWindowTitle(String title) {
        serverStage.setTitle(title);
	}
    
    public static void main(String[] args) {
        launch(args);
    }
}
