package contentsTransfer;

import java.awt.Image;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import controller.ClipboardController;
import controller.Endpoint;
import model.Contents;
import model.FileTransferable;
import model.History;
import model.ImageTransferable;

public class DownloadData {
	// �ٿ�ε� ������ �ӽ÷� ������ ��ġ
	private final String DOWNLOAD_LOCATION = "C:\\Program Files\\Clipcon";

	public final static String SERVER_URL = "http://223.194.152.19:8080/websocketServerModule";
//	public final static String SERVER_URL = "http://59.9.213.133:8080/websocketServerModule"; // delf's
	public final static String SERVER_SERVLET = "/DownloadServlet";

	private final String charset = "UTF-8";
	private HttpURLConnection httpConn;

	private String userName = null;
	private String groupPK = null;

	private Contents requestContents; // Contents Info to download
	// private String downloadDataPK; // Contents' Primary Key to download
	// private History myhistory; // The Group History to which I belong
	private Map<String, String[]> requestAgainOfFileData = new HashMap<String, String[]>();

	/** ������ userName�� groupPK�� �����Ѵ�. */
	public DownloadData(String userName, String groupPK) {
		this.userName = userName;
		this.groupPK = groupPK;
	}

	/**
	 * �ٿ�ε��ϱ� ���ϴ� Data�� request ���� ������ File Data�� ��츸 ����(���� ����)
	 * 
	 * @param downloadDataPK
	 *            �ٿ�ε��� Data�� ����Ű
	 * @param myhistory
	 *            ���� ���� �׷��� History ����
	 */
	public void requestDataDownload(String downloadDataPK) throws MalformedURLException {
		
		//���� �����丮 ��������. �ٸ� ��� ����s.
		History myhistory = Endpoint.user.getGroup().getHistory();
		
		// Create a temporary folder to save the imageFile, file
		createFolder(DOWNLOAD_LOCATION);
		// Retrieving Contents from My History
		requestContents = myhistory.getContentsByPK(downloadDataPK);
		// Type of data to download
		String contentsType = requestContents.getContentsType();
		
		// Parameter to be sent by the GET method
		String parameters = "userName=" + userName + "&" + "groupPK=" + groupPK + "&" + "downloadDataPK="
				+ downloadDataPK;

		try {
			URL url = new URL(SERVER_URL + SERVER_SERVLET + "?" + parameters);

			httpConn = (HttpURLConnection) url.openConnection();

			httpConn.setRequestMethod("GET");
			httpConn.setUseCaches(false);
			httpConn.setDoOutput(false); // indicates GET method
			httpConn.setDoInput(true);

			// checks server's status code first
			int status = httpConn.getResponseCode();
			List<String> response = new ArrayList<String>(); // Server�� ���䳻��

			if (status == HttpURLConnection.HTTP_OK) {
				switch (contentsType) {
				case Contents.TYPE_STRING:
					// response body�� ���� String ��ü�� �޾ƿ´�.
					String stringData = downloadStringData(httpConn.getInputStream());
					System.out.println("stringData ���: " + stringData);
					
					StringSelection stringTransferable = new StringSelection(stringData);
					ClipboardController.writeClipboard(stringTransferable);
					
				case Contents.TYPE_IMAGE:
					// response body�� ���� Image ��ü�� �޾ƿ´�.
					Image imageData = downloadCapturedImageData(httpConn.getInputStream());
					System.out.println("ImageData ���: " + imageData.toString());
					
					ImageTransferable imageTransferable = new ImageTransferable(imageData);
					ClipboardController.writeClipboard(imageTransferable);

					break;
					
				case Contents.TYPE_FILE:
					String fileOriginName = requestContents.getContentsValue();
					/* Clipcon ������ ���� File(���ϸ�: ���� ���ϸ�) ���� �� File ��ü�� �޾ƿ´�. */
					File fileData = downloadMultipartData(httpConn.getInputStream(), fileOriginName);
					System.out.println("fileOriginName ���: " + fileData.getName());
					
					ArrayList<File> fileList = new ArrayList<File>();
					fileList.add(fileData);
					FileTransferable fileTransferable = new FileTransferable(fileList);
					ClipboardController.writeClipboard(fileTransferable);

					break;
					
				case Contents.TYPE_MULTIPLE_FILE:
					// 1. server���� Json���·� multipleFileInfo�� ���� String�� �޾ƿ´�.
					// 2. Json���¸� �޾� ������ �°� dir���� �����Ѵ�.
					// 3. Json���� file�� �ش��ϴ� ���� GET request�� �ٽ� ��û�Ѵ�.
					// (dir�� ������ ���� file�� �޾ƿ��� ������ ó���Ѵ�.)
					// response body�� ���� String ��ü�� �޾ƿ´�.
					
					String multipleFileInfo = downloadStringData(httpConn.getInputStream());
					System.out.println("multipleFileInfo ���: " + multipleFileInfo);
					
					requestAgainOfFileData = analyzeMultipartDataInfo(multipleFileInfo);

					break;

				default:
					System.out.println("� ���Ŀ��� ������ ����.");
				}
				System.out.println();

			} else {
				throw new IOException("Server returned non-OK status: " + status);
			}
			httpConn.disconnect();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** String Data�� �ٿ�ε� */
	private String downloadStringData(InputStream inputStream) {
		BufferedReader bufferedReader;
		StringBuilder stringBuilder = null;

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset));

			stringBuilder = new StringBuilder();
			String line = null;

			try {
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line + "\n");
				}
				inputStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return stringBuilder.toString();
	}

	/**
	 * Captured Image Data�� �ٿ�ε� file ������ Image Data�� ���۹޾� Image ��ü�� ����
	 */
	private Image downloadCapturedImageData(InputStream inputStream) {
		byte[] imageInByte = null;
		BufferedImage bImageFromConvert = null;

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
			byte[] buffer = new byte[0xFFFF]; // 65536

			for (int len; (len = inputStream.read(buffer)) != -1;)
				byteArrayOutputStream.write(buffer, 0, len);

			byteArrayOutputStream.flush();
			imageInByte = byteArrayOutputStream.toByteArray();

			inputStream.close();

			// convert byte array back to BufferedImage
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageInByte);
			bImageFromConvert = ImageIO.read(byteArrayInputStream);

		} catch (IOException e) {
			e.printStackTrace();
		}
		Image ImageData = (Image) bImageFromConvert;

		return ImageData;
	}

	/** Multiple File Data�� �ӽ������� �ٿ�ε� �� File ��ü ���� */
	private File downloadMultipartData(InputStream inputStream, String fileName) throws FileNotFoundException {
		// opens input stream from the HTTP connection
		// InputStream inputStream = httpConn.getInputStream();
		String saveFileFullPath = DOWNLOAD_LOCATION + File.separator + fileName;
		File fileData;

		try {
			// opens an output stream to save into file
			FileOutputStream fileOutputStream = new FileOutputStream(saveFileFullPath);

			int bytesRead = -1;
			byte[] buffer = new byte[0xFFFF];

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, bytesRead);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		fileData = new File(saveFileFullPath);
		return fileData;
	}
	
	/** Multiple File Data�� ������ �м��Ͽ� Dir ���� ���� �� �ٽ� server�� ��û�� ������ return */
	private Map<String, String[]> analyzeMultipartDataInfo(String jsonString){
		Map<String, String[]> multipleFileInfo = new HashMap<String, String[]>(); 
		Map<String, String[]> requestAgainOfFileData = new HashMap<String, String[]>();

        JSONObject jsonObject = new JSONObject(jsonString); // HashMap
        Iterator<?> keyset = jsonObject.keys(); // HM
        String[] value = new String[2];

		/* [����] Json ���� Ȯ�� �� ���� �ʿ� */
		while (keyset.hasNext()) {
			String key = (String) keyset.next();
			System.out.print("\n Key: " + key);

			JSONArray jsonArray = jsonObject.getJSONArray(key);
			System.out.println(", Value: " + jsonArray.toString());

			for (int i = 0; i < jsonArray.length(); i++) {
				value[i] = (String) jsonArray.get(i);
			}
			System.out.println("value[0]: " + value[0] + ", value[1]: " + value[1]);

			multipleFileInfo.put(key, value);

			// case: directory
			if (value[1].equals(Contents.TYPE_DIRECTORY)) {
				// �����ϰ� directory�� ����
				makeDirBasedJsonStruct(value[0]);
			}
			// case: file
			else {
				// �ٽ� server�� ��û�� ������ ����
				System.out.println("�ٽ� server�� ��û�� File ���� key num: " + key);
				requestAgainOfFileData.put(key, value);
			}
		}
		return requestAgainOfFileData;
	}
	
	/** ������ �°� Directory ���� */
	private void makeDirBasedJsonStruct(String dirName){
		String dirFullName = DOWNLOAD_LOCATION + File.separator + dirName.replaceAll("\"", File.separator);
		createFolder(dirFullName);
	}
	
	/* ���α׷� ������ ���� �Űܾ� ��. */
	/**
	 * Folder ���� �޼���(download�� ������ ������ �ӽ� ����)
	 * 
	 * @param saveFilePath
	 *            �� �̸����� ���� ����
	 */
	private void createFolder(String folderName) {
		File directory = new File(folderName);

		// ������ �׷� ������ �������� ������
		if (!directory.exists()) {
			directory.mkdir(); // ���� ����
			System.out.println("------------------------------------" + folderName + " ���� ����");
		}
	}
}
