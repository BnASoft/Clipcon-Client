package controller;

import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import model.Contents;

public class ClipboardController {
	private static Clipboard systemClipboard; // �ڽ��� �ý��� Ŭ������
	//private static Contents contents;
	private static String type; // ������ Ÿ��
	
	/**
	 * �ý��� Ŭ�����忡�� Transferable ��ü�� �о�� ������ Ÿ���� �˾Ƴ��� Contents ��ü�� ��ȯ
	 * 
	 * @return settingObject ������ ������ Contents ��ü
	 */
	public static Object readClipboard() {
		Transferable t = getSystmeClipboardContets();
		setDataFlavor(t);
		Object clipboardData = extractDataFromTransferable(t);

		return clipboardData;
	}
	
	/**
	 * �ý��� Ŭ�������� Transferable ��ü ����
	 * 
	 * @return �ý��� Ŭ�����忡 �����ϴ� Transferable ��ü
	 */
	public static Transferable getSystmeClipboardContets() {
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		return systemClipboard.getContents(null);
	}
	
	/**
	 * Ŭ�������� Transferable ��ü�� � Ÿ������ set�ϰ� ����
	 * 
	 * @param t
	 *            Ŭ�������� Transferable ��ü
	 * @return t �� DataFlavor�� ����
	 */
	public static DataFlavor setDataFlavor(Transferable t) {
		DataFlavor[] flavors = t.getTransferDataFlavors();

		for (int i = 0; i < flavors.length; i++) {

			if (flavors[i].equals(DataFlavor.stringFlavor)) {
				type = Contents.TYPE_STRING;
				return DataFlavor.stringFlavor;
			} else if (flavors[i].equals(DataFlavor.imageFlavor)) {
				type = Contents.TYPE_IMAGE;
				return DataFlavor.imageFlavor;
			} else if (flavors[i].equals(DataFlavor.javaFileListFlavor)) {
				type = Contents.TYPE_FILE;
				return DataFlavor.javaFileListFlavor;
			} else {
			}
		}
		return null;
	}
	
	/**
	 * Ŭ�������� Transferable ��ü�� ���۽�Ʈ������ �ٲ�
	 * 
	 * @param contents
	 *            Ŭ�������� Transferable ��ü
	 * @return sendObject ���� ���� ��ü(Contents Ÿ��) ����
	 */
	private static Object extractDataFromTransferable(Transferable t) {
		try {
			Object extractData = null;

			// Ŭ�������� ������ ����
			if (type.equals(Contents.TYPE_STRING)) {
				System.out.println("[ClipboardManager]Ŭ������ ���� Ÿ��: ���ڿ�");
				//contents = new StringContents((String) t.getTransferData(DataFlavor.stringFlavor));
				//extractString = contents.toString();
				extractData = (String) t.getTransferData(DataFlavor.stringFlavor);
				
			} else if (type.equals(Contents.TYPE_IMAGE)) {
				System.out.println("[ClipboardManager]Ŭ������ ���� Ÿ��: �̹���");
				//contents = new ImageContents((Image) t.getTransferData(DataFlavor.imageFlavor));
				//extractString = contents.toString();
				extractData = (Image) t.getTransferData(DataFlavor.imageFlavor);
				
			} else if (type.equals(Contents.TYPE_FILE)) {
				String [] filePath = getFilePathInSystemClipboard().split(", ");
				
				ArrayList<String> filePathList = new ArrayList<String>();
				
				for(int i=0; i<filePath.length; i++) {
					filePathList.add(filePath[i]);
				}
				
				extractData = filePathList;

//				if (filePathList.size() == 1) {
//					contents = new FileContents(filePath[0]);
//					extractString = contents.toString();
//				} else {
//					System.out.println("[ClipboardManager]Ŭ������ ���� Ÿ��: �������� ���� �Ǵ� ���͸�");
//					extractString = "";
//
//					Contents [] contentsList = new FileContents [filePath.length];
//					
//					for (int i = 0; i < filePath.length; i++) {
//						contentsList[i] = new FileContents(filePath[i]);
//						extractString += contentsList[i].toString() + "\n";
//					}
//				}
			} else {
				System.out.println("[ClipboardManager]Ŭ������ ���� Ÿ��: ���ڿ�, �̹���, ����, ���͸��� �ƴ�");
			}

			return extractData;

		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** @return Ŭ�����忡 �ִ� ������ ��θ� */
	private static String getFilePathInSystemClipboard() {
		try {
			Transferable contents = getSystmeClipboardContets(); // �ý��� Ŭ�����忡�� ������ ����
			String fileTotalPath = contents.getTransferData(DataFlavor.javaFileListFlavor).toString();
			return fileTotalPath.substring(1, fileTotalPath.length() - 1); // ��θ� ������ ���� �� ���� []�� ����
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ���۹��� Contents ��ü�� Transferable�ؼ� Ŭ�����忡 ����(���ڿ�, �̹����� ���)
	 *
	 * @param data
	 *            ���۹��� ������
	 * @param dataType
	 *            ���۹��� ������ Ÿ��
	 */
	
	public static void writeClipboard(Transferable transferable) {
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		systemClipboard.setContents(transferable, null); // �ý��� Ŭ�����忡 ����
	}
}

