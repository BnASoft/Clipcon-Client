package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
	private String eMail;
	private String nickName;
	// �ּҷ�
	private Group group;
}
