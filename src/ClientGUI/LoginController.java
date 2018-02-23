package ClientGUI;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.prefs.Preferences;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class LoginController {
	
	public Preferences prefs = Preferences.userNodeForPackage(ClientGUI.class);
	
	private String username;

    @FXML
    private JFXTextField userTextField;

    @FXML
    private JFXPasswordField passField;
    
    @FXML
    private JFXCheckBox LoginRememberMe;

    @FXML
    private Text errorLoginOutput;

    @FXML
    public void initialize() {
    		userTextField.setText(prefs.get("username", ""));
		passField.setText(prefs.get("password", ""));
    }
    
    @FXML
    void LoginBtn(ActionEvent event) {
    		LoginToServer();
    }
    @FXML
    void PasswordPressedEnter(ActionEvent event) {
    		LoginToServer();
    }
    @FXML
    void UsernamePressedEnter(ActionEvent event) {
    		LoginToServer();
    }

    private void LoginToServer() {
		try{
			Class.forName("com.mysql.jdbc.Driver");

			Connection con=DriverManager.getConnection("jdbc:mysql://"+prefs.get("server_address", "")+":3306/JavaChat?autoReconnect=true&useSSL=false","chat","7xsVuPeF1rCQOeo2");
			//here sonoo is the database name, root is the username and root is the password
			Statement stmt=con.createStatement();
			
	        final String encodedPassword = Decrypt(passField.getText().trim());
			
			ResultSet result = stmt.executeQuery("select * from users where user='"+userTextField.getText().trim()+"' AND pass='"+encodedPassword+"'");
			if(result.next()){
				//System.out.println(result.getInt(1)+"  "+result.getString(2)+"  '"+result.getString(3)+"' = '"+encoded+"'");
				username = userTextField.getText().trim();
				errorLoginOutput.setText("Login Successful!");
				// put remeber stuff here this is the successful login spot
				

    				prefs.put("username", userTextField.getText().trim());
				if(LoginRememberMe.isSelected()) {
	        			prefs.put("password", passField.getText().trim());
				} else {
					prefs.put("password", "");
				}
				ScreenController.addScreen("Chat", FXMLLoader.load(getClass().getResource( "gui/Chat.fxml" )));
				ScreenController.activate("Chat");
		    } else {
				errorLoginOutput.setText("Invalid login.");
			}
			con.close();

		}catch(Exception e1){ 
			System.out.println(e1.getMessage());
			if(e1.getMessage().contains("empty result set")) {
				errorLoginOutput.setText("Invalid login.");
			} else if(e1.getMessage().contains("Could not create connection to database server")) {
				errorLoginOutput.setText("Could not connect to server.");
			} else {
				errorLoginOutput.setText("Error while processing request.");
			}
		}
	}
    
    // decrypts the password for database
    public String Decrypt(String text) throws NoSuchAlgorithmException {
    	
		String salt = "nb9af3uobu80ag87bpfu4iwef";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update((text+salt).getBytes());
        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
	    	for (int i=0;i<byteData.length;i++) {
	    		String hex=Integer.toHexString(0xff & byteData[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     	hexString.append(hex);
	    	}
	    	return hexString.toString();
    }

}
