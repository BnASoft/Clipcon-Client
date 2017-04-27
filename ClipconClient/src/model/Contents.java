package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
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
	
	// String Type: String��, File Type: FileOriginName
	private String contentsValue;
	
	public Contents(String type, String userEmail, String time, long size) {
		this();
		this.contentsType = type;
		this.uploadUserName = userEmail;
		this.uploadTime = time;
		this.contentsSize = size;
	}

}
