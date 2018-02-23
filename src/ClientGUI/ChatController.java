package ClientGUI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;

import org.apache.commons.io.FileUtils;
import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class ChatController {
	
	private String server, username;
	private int port;
	private boolean connected;
	
	private Client client;
	
	//  app prefs - https://stackoverflow.com/questions/4017137/how-do-i-save-preference-user-settings-in-java
	public Preferences prefs = Preferences.userNodeForPackage(ClientGUI.class);
	
	// detech OS
	private static String OS = System.getProperty("os.name").toLowerCase();

    @FXML
    private JFXTextArea MsgArea;

    @FXML
    private JFXTextField msgFld;

    @FXML
    private JFXButton sendBtn;

    @FXML
    private JFXListView<String> UserList = new JFXListView<String>();


    @FXML
    private JFXButton fileBtn;

    @FXML
    private JFXButton exportBtn;

    @FXML
    private JFXButton disconnectBtn;

    @FXML
    private JFXButton settingsBtn;

    @FXML
    private Text connectedLabel;

	@FXML
	public void initialize() {
		server = prefs.get("server_address", "");
		port = Integer.parseInt(prefs.get("server_port", ""));
		username = prefs.get("username", "");
		
		try {
			client = new Client(server, port, username, this);
			if(client.start()) {
				connected = true;
				connectedLabel.setText("Connected");
			}
			
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
		}catch (Exception e) {
			append("Error connecting");
			connectedLabel.setText("Disconnected");
		}
	}

    @FXML
    void DisconnectPress(ActionEvent event) {
		if(connected) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			disconnectBtn.setText("Connect");
			connectedLabel.setText("Disconnected");
			UserList.getItems().clear();
			connected = false;
		} else if (!connected) {
			reconnectToServer();
			disconnectBtn.setText("Disconnect");
			connected = true;
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
		}
    }

    @FXML
    void FilePress(ActionEvent event) {
	    	FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select File");
	    	File selectedFile = fileChooser.showOpenDialog(null);
	    	
	    	if (selectedFile != null) {
			    ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
		        Notifications.create().title("Chat: "+server+":"+port).text("File selected: " + selectedFile.getName()).graphic(icon).show();
		        try {
					SendFile(selectedFile);
				} catch (IOException e1) {
					Notifications.create().title("Chat: "+server+":"+port).text("Error Sending File: " + selectedFile.getName()).showError();
				}
	    	}
    }

    @FXML
    void SendMsgPress(ActionEvent event) {
		if(!msgFld.getText().equals("")) {
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msgFld.getText()));
			msgFld.setText("");
		}
    }

    @FXML
    void SendMsgPressedEnter(ActionEvent event) {
    		if(!msgFld.getText().equals("")) {
    			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msgFld.getText()));
    			msgFld.setText("");
    		}
    }
	
    @FXML
    void exportBtnPress(ActionEvent event) throws IOException {
    		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		if(isWindows()) {
			File userDirectory = new File(System.getProperty("user.home")+"\\Downloads");
			if(!userDirectory.canRead())
			    userDirectory = new File("c:/");
			fileChooser.setInitialDirectory(userDirectory);
		}
		
		fileChooser.setInitialFileName("");
		File savedFile = fileChooser.showSaveDialog(null);

		if (savedFile != null) {
			
	    		BufferedWriter out = new BufferedWriter(new FileWriter(savedFile));
		    	try {
		    	    out.write(MsgArea.getText());
		    	}
		    	catch (IOException e)
		    	{
		    	    System.out.println("Exception ");
		        Notifications.create().title("Chat: "+server+":"+port).text("An ERROR occurred while saving the file").showError();

		    	}
		    	finally
		    	{
		    	    out.close();
			    ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
		        Notifications.create().title("Chat: "+server+":"+port).text("File saved: " + savedFile.toString()).graphic(icon).show();
		    	}
		}

    }
    
    private void reconnectToServer() {
    		try {
    			client = new Client(server, port, username, this);
    			if(client.start()) {
    				connected = true;
    				connectedLabel.setText("Connected");
    			}
    			
    			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
		} catch (Exception e) {
			connectedLabel.setText("Disconnected");
			connected = false;
		}
	}

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}
    
    public void append(String msg) {
		try {
			if(msg.contains("ServerResetUserList:")) {
				Platform.runLater(new Runnable() {
    			    @Override
    			    public void run() {
    			    		UserList.getItems().clear();
    			    }
    			});
			} else {
				if(msg.contains("ServerAddToUserList:")) {
					Platform.runLater(new Runnable() {
	    			    @Override
	    			    public void run() {
	    					UserList.getItems().add(msg.replace("ServerAddToUserList:", ""));
	    			    }
	    			});
				} else if(msg.contains("|ClintSendFile|")) {
					//System.out.println(msg);
					
					String s = new String(msg);
					
					String[] separatedMsg = s.split("\\|");

					String userAndTime = separatedMsg[0];
					String fileName = separatedMsg[2];
					String fileEncoded = msg.replace(userAndTime + "|ClintSendFile|" + fileName + "|", "");
					 
					System.out.println("Saving: "+ fileName);
					
					byte[] encoded = fileEncoded.getBytes("ISO-8859-1");

					if(!msg.contains(username)) {
						//  save file as
						Platform.runLater(new Runnable() {
			    			    @Override
			    			    public void run() {
			    					FileChooser fileChooser = new FileChooser();
			    					fileChooser.setTitle("Save File");
			    					if(isWindows()) {
				    					File userDirectory = new File(System.getProperty("user.home")+"\\Downloads");
				    					if(!userDirectory.canRead())
				    					    userDirectory = new File("c:/");
				    					fileChooser.setInitialDirectory(userDirectory);
			    					}
			    					
			    					fileChooser.setInitialFileName(separatedMsg[2]);
			    					File savedFile = fileChooser.showSaveDialog(null);
		
			    					if (savedFile != null) {
		
			    					    try {
			    					    	FileUtils.writeByteArrayToFile(savedFile, encoded);
			    					    }
			    					    catch(IOException e) {
			    						
			    					        e.printStackTrace();
			    					        Notifications.create().title("Chat: "+server+":"+port).text("An ERROR occurred while saving the file").showError();
			    					        return;
			    					    }
			    					    ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
			    				        Notifications.create().title("Chat: "+server+":"+port).text("File saved: " + savedFile.toString()).graphic(icon).show();
			    					}
		    					}
			    			});
					}
					
					userAndTime=null;
					fileName=null;
					fileEncoded=null;
				} else {
					MsgArea.appendText(msg);
					if(!msg.contains(username)) {
	        			Platform.runLater(new Runnable() {
	        			    @Override
	        			    public void run() {
	        			    	ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
	        			    
	        			    	Notifications.create().title("Chat: "+server+":"+port).text(msg).graphic(icon).show();
	        			    }
	        			});
	        		}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
    
    private void SendFile(File file) throws IOException {
    	// convert file to byte array then to string to send to server
		byte[] str = FileUtils.readFileToByteArray(file);
		String decoded = new String(str, "ISO-8859-1");
		client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, "|ClintSendFile|" + file.getName() + "|" + decoded));
	}

	public void connectionFailed() {
		MsgArea.appendText("Connection to chat server failed");
	}
}
