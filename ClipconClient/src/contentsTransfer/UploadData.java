package contentsTransfer;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class UploadData {
	// public final static String SERVER_URL = "http://182.172.16.118:8080/websocketServerModule";
	// public final static String SERVER_URL = "http://223.194.157.244:8080/websocketServerModule";
	public final static String SERVER_URL = "http://223.194.152.19:8080/websocketServerModule";
	public final static String SERVER_SERVLET = "/UploadServlet";
	private String charset = "UTF-8";

	private String userName = null;
	private String groupPK = null;
	private int startIndex = 0;

	/** ������ userName�� groupPK�� �����Ѵ�. */
	public UploadData(String userName, String groupPK) {
		this.userName = userName;
		this.groupPK = groupPK;
	}

	/** String Data�� ���ε� */
	public void uploadStringData(String stringData) {
		try {
			MultipartUtility multipart = new MultipartUtility(SERVER_URL + SERVER_SERVLET, charset);
			setCommonParameter(multipart);

			multipart.addFormField("createFolder", "FALSE");
			multipart.addFormField("stringData", stringData);
			System.out.println("stringData: " + stringData);

			List<String> response = multipart.finish();

			for (String line : response) {
				System.out.println(line);
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	/** Clipboard�� �ִ� Captured Image Data�� ���ε� */
	public void uploadCapturedImageData(Image capturedImageData) {
		try {
			MultipartUtility multipart = new MultipartUtility(SERVER_URL + SERVER_SERVLET, charset);
			setCommonParameter(multipart);

			multipart.addFormField("createFolder", "FALSE");
			multipart.addImagePart("imageData", capturedImageData);
			System.out.println("imageData: " + capturedImageData.toString());

			List<String> response = multipart.finish();

			for (String line : response) {
				System.out.println(line);
			}

		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	/** ���� File Data�� ���ε�
	 * 
	 * @param dir ���ε��� ������ ��ġ
	 * @param dir ���ε��� ���ϸ�
	 */
	public void uploadMultipartData(ArrayList<String> fileFullPathList) {
		try {
			MultipartUtility multipart = new MultipartUtility(SERVER_URL + SERVER_SERVLET, charset);
			setCommonParameter(multipart);
			
			// ���ε��� ���� ����
			File firstUploadFile = new File(fileFullPathList.get(0));
			
			/* case: ������ ������ 1���� ���(������ �ƴ� ���) createFolder = FALSE */
			if (fileFullPathList.size() == 1 && firstUploadFile.isFile()) {
				System.out.println("\n������ ������ �ϳ���~~\n");
				multipart.addFormField("createFolder", "FALSE");
				multipart.addFilePart("multipartFileData", firstUploadFile, "/");
			}
			/* case: ������ ������ 2�� �̻�, ������ �ϳ� �̻��� ��� createFolder = TRUE */
			else{
				System.out.println("\n������ ������ ��������~~\n");
				multipart.addFormField("createFolder", "TRUE");
				// Iterator ���� ��ü ��ȸ
				Iterator iterator = fileFullPathList.iterator();

				// ���� ������ ������� ó��
				while (iterator.hasNext()) {
					String fileFullPath = (String) iterator.next();
					
					// ���ε��� ���� ����
					File uploadFile = new File(fileFullPath);

					System.out.println("<<fileFullPathList>>: "+ fileFullPath);

					/* case: File */
					if(uploadFile.isFile()){
						System.out.println("������ ������ File�̿�~~");
						multipart.addFilePart("multipartFileData", uploadFile, "/");
					}
					/* case: Directory */
					else if(uploadFile.isDirectory()){
						System.out.println("������ ������ Directory��~~");
						
						// ����θ��� ���� �ʱⰪ(ó�� root dir�� ���� ��ġ ����)
						startIndex = uploadFile.getPath().lastIndexOf(uploadFile.getName());
						
						multipart.addFormField("directoryData", uploadFile.getPath().substring(startIndex));
						System.out.println("���丮 �̸� = " + uploadFile.getName() + ", ��� ���: " + uploadFile.getPath().substring(startIndex));
						
						subDirList(uploadFile, multipart);
					}
					System.out.println();
				}
			}

			List<String> response = multipart.finish();

			for (String line : response) {
				System.out.println(line);
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	/** File Data�� ������ ���� <����θ�, ���ϸ�> ���� 
	 * directory�̸� addFormField�� ����� ���� ������ 
	 * file�̸� addFilePart�� ���ϰ� ����� ���� ������*/
	public void subDirList(File uploadFile, MultipartUtility multipart) {
		File[] fileList = uploadFile.listFiles(); //directory ���� file data list

		for (int i = 0; i < fileList.length; i++) {
			File file = fileList[i];
			try {
				/* case: ���ε��� ���� ���ο� �� �ٸ� ������ �ִ� ��� */
				if (file.isFile()) {
					multipart.addFilePart("multipartFileData", file, getFileRelativePath(file));
					System.out.println("���� �̸� = " + file.getName() + ", ��� ���: " + getFileRelativePath(file));
				} 
				/* case: ���ε��� ���� ���ο� ������丮�� �����ϴ� ��� �ٽ� Ž�� */
				else if (file.isDirectory()) {
					multipart.addFormField("directoryData", file.getPath().substring(startIndex));
					// subDirList(file.getCanonicalPath().toString());
					subDirList(file, multipart);
					System.out.println("���丮 �̸� = " + file.getName() + ", ��� ���: " + file.getPath().substring(startIndex));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** relative path ������ */
	public String getFileRelativePath(File file){
		String filePath = file.getPath();
		String fileName = file.getName();
		int endIndex = filePath.lastIndexOf(fileName);
		
		// ���ϸ��� ������ ����� ����
		return filePath.substring(startIndex, endIndex-1); 
	}
	
	/** ��� Data���� �������� �����ؾ��ϴ� Parameter
	 * userName, groupPK, uploadTime */
	public void setCommonParameter(MultipartUtility multipart) {
		multipart.addHeaderField("User-Agent", "Heeee");
		multipart.addFormField("userName", userName);
		multipart.addFormField("groupPK", groupPK);
		multipart.addFormField("uploadTime", uploadTime());
	}

	/** @return YYYY-MM-DD HH:MM:SS ������ ���� �ð� */
	public String uploadTime() {
		Calendar cal = Calendar.getInstance();
		String year = Integer.toString(cal.get(Calendar.YEAR));
		String month = Integer.toString(cal.get(Calendar.MONTH) + 1);

		String date = Integer.toString(cal.get(Calendar.DATE));
		String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
		if (Integer.parseInt(hour) < 10) {
			hour = "0" + hour;
		}
		if (Integer.parseInt(hour) > 12) {
			hour = "PM " + Integer.toString(Integer.parseInt(hour) - 12);
		} else {
			hour = "AM " + hour;
		}

		String minute = Integer.toString(cal.get(Calendar.MINUTE));
		if (Integer.parseInt(minute) < 10) {
			minute = "0" + minute;
		}
		String sec = Integer.toString(cal.get(Calendar.SECOND));
		if (Integer.parseInt(sec) < 10) {
			sec = "0" + sec;
		}

		return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + sec;
	}
}