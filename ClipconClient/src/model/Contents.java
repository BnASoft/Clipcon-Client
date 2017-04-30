package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Contents {
	public static String TYPE_STRING = "STRING";
	public static String TYPE_IMAGE = "IMAGE";
	public static String TYPE_FILE = "FILE";

	private String contentsType;
	private long contentsSize;

	// �׷쳻�� �� Data�� �����ϴ� ����Ű��
	public String contentsPKName;

	private String uploadUserName;
	private String uploadTime;
	// XXX[����]: uploadTime�� Ŭ���̾�Ʈ���� �����ϴ� ������ ���� parser ����

	// String Type: String��, File Type: FileOriginName
	private String contentsValue;

	/**
	 * @author delf �ӽ� ������
	 */
	public Contents(String contentsType, long contentsSize, String contentsPKName, String uploadUserName,
			String uploadTime) {
		this(contentsType, contentsSize, contentsPKName, uploadUserName, uploadTime, null);
	}
}
