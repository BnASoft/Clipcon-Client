package model;

import java.util.HashMap;
import java.util.Map;

public class History {
	
	private Map<String, Contents> contentsMap = new HashMap<String, Contents>();
	
	/** ���ο� �����Ͱ� ���ε�Ǹ� �����丮�� add */
	public void addContents(Contents contents) {
		contentsMap.put(contents.getContentsPKName(), contents);
	}

	/** Data�� �����ϴ� ����Ű���� ��ġ�ϴ� Contents�� return */
	public Contents getContentsByPK(String contentsPKName) {
		return contentsMap.get(contentsPKName);
	}

}
