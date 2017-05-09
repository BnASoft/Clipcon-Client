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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.platform.win32.WinUser.WNDCLASSEX;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

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
				System.out.println("[ClipboardManager]Ŭ������ ���� Ÿ��: ����");
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
	
	
	
	private static final class WindowProc implements WinUser.WindowProc {

		private HWND nextViewer;
		
		private static int count = 0;

		public void setNextViewer(HWND nextViewer) {
			this.nextViewer = nextViewer;
		}
		
		public LRESULT callback(HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam) {
			//System.out.println("#callback : uMsg=" + uMsg);
			switch (uMsg) {
			case User32x.WM_CHANGECBCHAIN:
				// If the next window is closing, repair the chain.
				System.out.println("Repairing clipboard viewers chain...");
				if (nextViewer.toNative().equals(wParam.toNative())) {
					nextViewer = new HWND(Pointer.createConstant(lParam.longValue()));
				} else if (nextViewer != null) {
					User32x.INSTANCE.SendMessage(nextViewer, uMsg, wParam, lParam);
				}
				return new LRESULT(0);
			case User32x.WM_DRAWCLIPBOARD:
				count++;
				if (count > 1) {
					try {
						System.out.println("clipboard change!!!! " + count);
					} catch (IllegalStateException e) {
					}
				}
				break;
			}
			return User32.INSTANCE.DefWindowProc(hWnd, uMsg, wParam, lParam);
		}
	}

	public interface User32x extends StdCallLibrary {

		User32x INSTANCE = (User32x) Native.loadLibrary("user32", User32x.class, W32APIOptions.UNICODE_OPTIONS);

		final int WM_DESTROY = 0x0002;
		final int WM_CHANGECBCHAIN = 0x030D;
		final int WM_DRAWCLIPBOARD = 0x0308;

		HWND SetClipboardViewer(HWND viewer);

		void SendMessage(HWND nextViewer, int uMsg, WPARAM wParam, LPARAM lParam);

		void ChangeClipboardChain(HWND viewer, HWND nextViewer);

		int SetWindowLong(HWND hWnd, int nIndex, WinUser.WindowProc callback);

		int MsgWaitForMultipleObjects(int length, HANDLE[] handles, boolean b, int infinite, int qsAllinput);

	}

	public static void clipboardMonitor() {
		WString windowClass = new WString("MyWindowClass");
		HMODULE hInst = Kernel32.INSTANCE.GetModuleHandle("");
		WNDCLASSEX wClass = new WNDCLASSEX();
		wClass.hInstance = hInst;
		WindowProc wProc = new WindowProc();
		wClass.lpfnWndProc = wProc;
		wClass.lpszClassName = windowClass;

		// register window class
		User32.INSTANCE.RegisterClassEx(wClass);
		getLastError();

		// create new window
		HWND hWnd = User32.INSTANCE.CreateWindowEx(User32.WS_EX_TOPMOST, windowClass,
				"My hidden helper window, used only to catch the windows events", 0, 0, 0, 0, 0, null, null, hInst, null);
		getLastError();
		System.out.println("Window created hwnd: " + hWnd.getPointer().toString());

		// set clipboard viewer
		HWND nextViewer = User32x.INSTANCE.SetClipboardViewer(hWnd);
		wProc.setNextViewer(nextViewer);

		// pump messages
		MSG msg = new MSG();
		while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) != 0) {
			User32.INSTANCE.TranslateMessage(msg);
			User32.INSTANCE.DispatchMessage(msg);
		}
		// wait for input
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// destroy window
		User32.INSTANCE.UnregisterClass(windowClass, hInst);
		User32.INSTANCE.DestroyWindow(hWnd);
		System.exit(0);
	}

	public static int getLastError() {
		int rc = Kernel32.INSTANCE.GetLastError();
		if (rc != 0)
			System.out.println("error: " + rc);
		return rc;
	}
}

