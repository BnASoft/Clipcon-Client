package userInterface.scene;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.websocket.EncodeException;

import application.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.message.Message;
import server.Endpoint;
import userInterface.UserInterface;
import userInterface.dialog.Dialog;
import userInterface.dialog.PlainDialog;

public class GroupJoinScene implements Initializable{

	private UserInterface ui = UserInterface.getInstance();

	@FXML private TextField groupKeyTF;
	@FXML private Button confirmBtn, XBtn;
	
	private Dialog dialog;
	
	private Endpoint endpoint = Endpoint.getInstance();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ui.setGroupJoinScene(this);
		
		// group key text field event handling
		groupKeyTF.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.ENTER)) {
					if(groupKeyTF.getText().length() == 0) {
						notInputGroupKey();
					} else {
						sendGroupJoinMessage();
					}
				}
			}
		});
		
		// confirm button event handling
		confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(groupKeyTF.getText().length() == 0) {
					notInputGroupKey();
				} else {
					sendGroupJoinMessage();
				}
			}
		});
		
		// X button event handling
		XBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					Parent goBack = FXMLLoader.load(getClass().getResource("/view/StartingView.fxml"));
					Scene scene = new Scene(goBack);
					Stage backStage = Main.getPrimaryStage();

					backStage.setScene(scene);
					backStage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/** Show dialog when not input the group key */
	public void notInputGroupKey() {
		dialog = new PlainDialog("Group key 를 입력하세요.", false);
		dialog.showAndWait();
	}
	
	/** Send join group messgae and group key to server */
	public void sendGroupJoinMessage() {
		if (groupKeyTF.getText().length() != 0) {
			
			Message signUpMsg = new Message().setType(Message.REQUEST_JOIN_GROUP);
			signUpMsg.add(Message.GROUP_PK, groupKeyTF.getText());
			try {
				endpoint.sendMessage(signUpMsg);
			} catch (IOException | EncodeException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Show dialog when input invalid group key */
	public void failGroupJoin() {
		Platform.runLater(() -> {
			dialog = new PlainDialog("유효하지 않는 Group Key 입니다. 다시 입력하세요.", false);
			dialog.showAndWait();
			groupKeyTF.setText("");
		});
	}
}
