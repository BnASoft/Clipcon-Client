package controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import model.Contents;
import model.Group;
import model.Message;
import model.MessageDecoder;
import model.MessageEncoder;
import model.MessageParser;
import model.User;
import userInterface.UserInterface;

@ClientEndpoint(decoders = { MessageDecoder.class }, encoders = { MessageEncoder.class })
public class Endpoint {
	// private String uri = "ws://182.172.16.118:8080/websocketServerModule/ServerEndpoint";
	// private String uri = "ws://223.194.157.244:8080/websocketServerModule/ServerEndpoint";
	private String uri = "ws://223.194.152.19:8080/websocketServerModule/ServerEndpoint";
	private Session session = null;
	private static Endpoint uniqueEndpoint;
	private static UserInterface ui;

	public static User user;

	public static Endpoint getIntance() {
		try {
			if (uniqueEndpoint == null) {
				uniqueEndpoint = new Endpoint();
			}
		} catch (DeploymentException | IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		return uniqueEndpoint;
	}

	public Endpoint() throws DeploymentException, IOException, URISyntaxException {
		URI uRI = new URI(uri);
		ContainerProvider.getWebSocketContainer().connectToServer(this, uRI);
		ui = UserInterface.getIntance();
	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
	}

	@OnMessage
	public void onMessage(Message message) {
		System.out.println("message type: " + message.getType());
		switch (message.get(Message.TYPE)) {

		case Message.RESPONSE_CREATE_GROUP:

			switch (message.get(Message.RESULT)) {
			case Message.CONFIRM:
				System.out.println("create group confirm");

				ui.getStartingScene().setCreateGroupSuccessFlag(true); // MainView ������
				user = MessageParser.getUserAndGroupByMessage(message); // ��������  primaryKey, name �޾�  Group ��ü ���� �� user�� set

				while (true) {
					if (ui.getMainScene() != null) {
						break;
					}
				}

				System.out.println("�׷�Ű : " + user.getGroup().getPrimaryKey());
				ui.getMainScene().setInitGroupParticipantFlag(true); // UI list �ʱ�ȭ

				break;
			case Message.REJECT:
				System.out.println("create group reject");
				break;
			}

			break;

		case Message.RESPONSE_JOIN_GROUP:

			switch (message.get(Message.RESULT)) {
			case Message.CONFIRM:
				System.out.println("join group confirm");

				ui.getGroupJoinScene().setJoinGroupSuccessFlag(true); // Group join close �ϰ� MainView ������
				user = MessageParser.getUserAndGroupByMessage(message); // �������� primaryKey, name �޾� Group ��ü ���� �� user�� set

				while (true) {
					if (ui.getMainScene() != null) {
						break;
					}
				}

				System.out.println("�׷�Ű : " + user.getGroup().getPrimaryKey());
				ui.getMainScene().setInitGroupParticipantFlag(true); // UI list �ʱ�ȭ

				break;
			case Message.REJECT:
				System.out.println("join group reject");
				break;
			}

			break;

		case Message.NOTI_ADD_PARTICIPANT: // �׷� �� �ٸ� User ���� �� ���� Message �ް� UI ����

			System.out.println("add participant confirm");

			user.getGroup().getUserList().add(new User(message.get(Message.PARTICIPANT_NAME)));
			ui.getMainScene().setAddGroupParticipantFlag(true); // UI list �߰�

			break;

		case Message.NOTI_EXIT_PARTICIPANT:
			// TODO[����]: Ŭ���̾�Ʈ �׷� Ż�� �޽��� ó��
			break;

		case Message.NOTI_UPLOAD_DATA:
			Contents contents = MessageParser.getContentsbyMessage(message);
			user.getGroup().addContents(contents);
			// TODO[����]: �����丮 ������Ʈ UIó��
			System.out.println("-----<Endpoint> contentsValue ����-----");
			System.out.println(contents.getContentsValue());
			
			break;

		default:
			System.out.println("default");
			break;
		}

	}

	public void sendMessage(Message message) throws IOException, EncodeException {
		if (session == null) {
			System.out.println("debuger_delf: session is null");
		}
		session.getBasicRemote().sendObject(message);
	}

	@OnClose
	public void onClose() {
		// ������ ������ �� ��� ���� ó��
	}
}