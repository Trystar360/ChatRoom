package ClientGUI;


import java.util.HashMap;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class ScreenController {
	
	public String server, username;
	public int port;
	
    private static HashMap<String, Pane> screenMap = new HashMap<>();
    private static Scene main;

    public ScreenController(Scene main) {
        this.main = main;
    }

    public static void addScreen(String name, Pane pane){
         screenMap.put(name, pane);
    }

    public void removeScreen(String name){
        screenMap.remove(name);
    }

    public static void activate(String name){
        main.setRoot( screenMap.get(name) );
    }
}