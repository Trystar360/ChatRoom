package ClientGUI;

import java.io.IOException;
import java.util.prefs.Preferences;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.text.Text;

public class ServerSelectController {
	
	public String server;
	public int port;
	
	public Preferences prefs = Preferences.userNodeForPackage(ClientGUI.class);

    @FXML
    private JFXTextField serverAddress;

    @FXML
    private JFXTextField serverPort;

    @FXML
    private Text errorServerSelectOutput;
    
    @FXML
    public void initialize() {
    		serverAddress.setText(prefs.get("server_address", ""));
    		serverPort.setText(prefs.get("server_port", ""));
    }

    @FXML
    void ConnectBtn(ActionEvent event) throws IOException {
    		ConnectToServer();
    }
    @FXML
    void AddressPressedEnter(ActionEvent event) throws IOException {
    		ConnectToServer();
    }
    @FXML
    void PortPressedEnter(ActionEvent event) throws IOException {
    		ConnectToServer();
    }
    
    public void ConnectToServer() throws IOException {
    		
    		server = serverAddress.getText().trim();
    		port = Integer.parseInt(serverPort.getText().trim());

		prefs.put("server_address", server);
		prefs.put("server_port", Integer.toString(port));
		
    		System.out.println(server+":"+port);
    		
		ScreenController.addScreen("Login", FXMLLoader.load(getClass().getResource( "gui/Login.fxml" )));
		ScreenController.activate("Login");	
	}
    
}
