package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Group {
	private String primaryKey;
	private String name;
	// ������ List
	// �ʴ��� List
	private History history;
}
