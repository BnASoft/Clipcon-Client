package model;


import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.json.JSONObject;

/**
 * ������ ���� object(Message)�� string���� encoding. */
public class MessageEncoder implements Encoder.Text<Message> {
	private JSONObject tmp;

	public void destroy() {
	}

	public void init(EndpointConfig arg0) {
		tmp = new JSONObject();
	}

	public String encode(Message message) throws EncodeException {
		System.out.println("������ ���� string: " + message.getJson());
		return message.getJson().toString();
	}
}
