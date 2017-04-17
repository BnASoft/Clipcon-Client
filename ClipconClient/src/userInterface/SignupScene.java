package userInterface;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.websocket.EncodeException;

import controller.Endpoint;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Message;

public class SignupScene implements Initializable{
	
	@FXML private TextField email;
	@FXML private PasswordField password1;
	@FXML private PasswordField password2;
	@FXML private TextField nickname;
	@FXML private Button confirmBtn;
	
	private Endpoint endpoint = Endpoint.getIntance();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		confirmBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				if (email.getText().equals("") || password1.getText().equals("") || password2.getText().equals("")
						|| nickname.getText().equals("")) {
					System.out.println("��� ���� �Է�");
				} else {
					if (password1.getText().equals(password2.getText())) {
						Message signUpMsg = new Message(Message.REQUEST_SIGN_UP);
						signUpMsg.add(Message.EMAIL, email.getText());
						signUpMsg.add("password", password1.getText());
						signUpMsg.add("nickname", nickname.getText());
						try {
							endpoint.sendMessage(signUpMsg);
						} catch (IOException | EncodeException e) {
							e.printStackTrace();
						}
						
						closeSignUpView();

					} else {
						System.out.println("��й�ȣ Ȯ��");
					}
				}
			}

		});
	}

	public void closeSignUpView() {
		Stage stage = (Stage) confirmBtn.getScene().getWindow();
	    stage.close();
	}

}
