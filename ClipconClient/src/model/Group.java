package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Group {
	private String groupName;
	private String groupPK;
	// ������ List
	// �ʴ��� List
	private History history;
}
