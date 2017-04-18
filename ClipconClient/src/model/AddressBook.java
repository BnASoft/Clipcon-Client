package model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressBook {
	private Map<String, String> addressBook;
	
	public AddressBook() {
		addressBook = new HashMap<String, String>();
	}
	
	// �ּҷ� �߰�
	public void addAdress(String eMail, String name) {
		addressBook.put(eMail, name);
	}
	
	// �ּҷ� ����
	public void deleteAdress(String eMail) {
		addressBook.remove(eMail);
	}
	
	// �ּҷ� �˻�?
	public String searchAdress(String eMail) {
		return addressBook.get(eMail);
	}
}
