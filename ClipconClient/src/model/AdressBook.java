package model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdressBook {
	private Map<String, String> adressBook;
	
	public AdressBook() {
		adressBook = new HashMap<String, String>();
	}
	
	// �ּҷ� �߰�
	public void addAdress(String eMail, String name) {
		adressBook.put(eMail, name);
	}
	
	// �ּҷ� ����
	public void deleteAdress(String eMail) {
		adressBook.remove(eMail);
	}
	
	// �ּҷ� �˻�?
	public String searchAdress(String eMail) {
		return adressBook.get(eMail);
	}
}
