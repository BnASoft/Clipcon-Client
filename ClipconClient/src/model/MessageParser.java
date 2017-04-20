package model;

import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class MessageParser {

   /**
    * @param message �������� ���� Message��ü
    * @return message �κ��� ��ȯ�� User��ü */
   public static User getUserByMessage(Message message) {
      User user = new User(); // ��ȯ�� ��ü
      user.setEmail(message.get(Message.EMAIL));   // User��ü�� email ����
      user.setName(message.get(Message.NAME));   // User��ü�� name ����

      // user�� ������ AddressBook��ü ����
      AddressBook addressBook = new AddressBook();
      Map<String, String> users = addressBook.getAddressBook();

      // message���� JSONObject����
      JSONObject jsonMsg = message.getJson();
      JSONArray array = jsonMsg.getJSONArray(Message.LIST);

      Iterator<?> it = array.iterator();
      while (it.hasNext()) {
         JSONObject tmpJson = (JSONObject) it.next();
         String email = tmpJson.getString(Message.EMAIL);
         String name = tmpJson.getString(Message.NAME);
         users.put(email, name);
      }
      user.setAddressBook(addressBook);
      
      return user;
   }

   public static AddressBook getAddressBookByMessage(Message message) {
      AddressBook addressBook = new AddressBook();
      Map<String, String> users = addressBook.getAddressBook();
      JSONObject jsonMsg = message.getJson();
      JSONArray array = jsonMsg.getJSONArray(Message.LIST);

      Iterator<?> it = array.iterator();
      while (it.hasNext()) {
         JSONObject tmpJson = (JSONObject) it.next();
         String email = tmpJson.getString(Message.EMAIL);
         String name = tmpJson.getString(Message.NAME);
         // User tmpUser = new User(email, tmpJson.getString(Message.NAME));
         users.put(email, name);
      }

      for (String key : users.keySet()) {
         System.out.println(key + " " + users.get(key));
      }

      return addressBook;
   }

   public static Message getMeessageByAddressBook(AddressBook addressBook) {
      Map<String, String> users = addressBook.getAddressBook();
      Message message = new Message().setType(Message.ADDRESS_BOOK);

      JSONArray array = new JSONArray();
      for (String key : users.keySet()) {
         JSONObject tmp = new JSONObject();
         tmp.put(Message.EMAIL, users.get(key));
         tmp.put(Message.NAME, users.get(key));
         array.put(tmp);
      }
      message.getJson().put(Message.LIST, array);
      
      return message;
   }

   public static Message getMessageByUser(User user) {
      Message message = new Message().setType(Message.USER_INFO); // ��ȯ�� ��ü, Ÿ���� '��������'

      message.add(Message.EMAIL, user.getEmail());   // email ����
      message.add(Message.NAME, user.getName());      // name ����

      // Json���� ��ȯ�� �ּҷ� Map
      Map<String, String> users = user.getAddressBook().getAddressBook();
      // �ּҷ� ���� ���� JsonArray
      JSONArray array = new JSONArray();
      // array�� �ּҷ� ���� ����
      for (String key : users.keySet()) {
         JSONObject tmp = new JSONObject();
         tmp.put(Message.EMAIL, users.get(key));
         tmp.put(Message.NAME, users.get(key));
         array.put(tmp);
      }
      // array�� message�� ����
      message.getJson().put(Message.LIST, array);

      return message;
   }
   
   /**
    * @param message �������� ���� Message��ü
    * @return message �κ��� ��ȯ�� Group��ü */
   public static Group getGroupByMessage(Message message) {
		Group group = new Group();
		String key = message.get("groupkey");
		String name = message.get("groupname");
		group.setPrimaryKey(key);
		group.setName(name);

		group.setUserList((Map<String, String>)message.getObject("list"));
		return group;
	}

}