package contentsTransfer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MultipleFileCompress {
	private static final int COMPRESSION_LEVEL = 1;
	private static final int BUFFER_SIZE = 4096;
	private static final String ZIP_FILE_PATH = "C:\\Program Files\\TempCilpcon\\";
	private static int lastIndex = 0;

	/**
	 * ������ ������ Zip ���Ϸ� �����Ѵ�.
	 * @param sourcePath - ���� ��� ���丮
	 * @param output - ���� zip ���� �̸�
	 * @throws Exception
	 * @return outputFileName - ���� ������ �ִ� ��ü ���
	 */
	/* ���� ��ο� �ִ� �������� ������ ���� */
	@SuppressWarnings("finally")
	public static String compress(ArrayList<String> fileFullPathList) throws Exception {
		File[] files = new File[fileFullPathList.size()];
		String outputFileFullPath = ZIP_FILE_PATH + "Default.zip";

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipOutputStream zos = null;

		fos = new FileOutputStream(outputFileFullPath); // FileOutputStream
		bos = new BufferedOutputStream(fos); // BufferedStream
		zos = new ZipOutputStream(bos); // ZipOutputStream
		zos.setLevel(COMPRESSION_LEVEL); // ���� ���� - �ִ� ������� 9, ����Ʈ 8

		System.out.println(fileFullPathList.size());

		try {
			for (int i = 0; i < fileFullPathList.size(); i++) {
				files[i] = new File(fileFullPathList.get(i));
				System.out.println("----------------------������ file�� path: " + files[i].getPath());

				lastIndex = files[i].getPath().lastIndexOf(File.separator);

				// ���� ����� ���丮�� ������ �ƴϸ� �����Ѵ�.
				if (!files[i].isFile() && !files[i].isDirectory()) {
					throw new Exception("���� ����� ������ ã�� ���� �����ϴ�.");
				}
				zipEntry(files[i], files[i].getPath(), zos); // Zip ���� ����
			}
		} finally {
			if (zos != null) {
				zos.closeEntry();
				zos.finish(); // ZipOutputStream finish
				zos.close();
			}
			if (bos != null) {
				bos.close();
			}
			if (fos != null) {
				fos.close();
			}
			return outputFileFullPath;
		}
	}

	/**
	 * ����
	 * @param sourceFile
	 * @param sourcePath
	 * @param zos
	 * @throws Exception
	 */
	private static void zipEntry(File file, String filePath, ZipOutputStream zos) throws Exception {
		// sourceFile�� ���丮�� ��� ���� ���� ����Ʈ ������ ���ȣ��
		if (file.isDirectory()) {
			// .metadata ���丮
			if (file.getName().equalsIgnoreCase(".metadata")) {
				return;
			}
			File[] fileArray = file.listFiles(); // sourceFile �� ���� ���� ����

			for (int i = 0; i < fileArray.length; i++) {
				zipEntry(fileArray[i], fileArray[i].getPath(), zos); // ��� ȣ��
			}
		}

		// sourceFile�� ���丮�� �ƴ� ���
		else {
			BufferedInputStream bis = null;

			try {
				String sFilePath = file.getPath();
				String zipEntryName = sFilePath.substring(lastIndex + 1, sFilePath.length());
				System.out.println("zipEntry <<sFilePath>>: " + sFilePath);
				System.out.println("zipEntry <<zipEntryName>>: " + zipEntryName);
				// String zipEntryName = sFilePath.substring(filePath.length() + 1, sFilePath.length());

				bis = new BufferedInputStream(new FileInputStream(file));
				ZipEntry zentry = new ZipEntry(zipEntryName);
				zentry.setTime(file.lastModified());
				zos.putNextEntry(zentry);

				byte[] buffer = new byte[BUFFER_SIZE];
				int cnt = 0;

				while ((cnt = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
					zos.write(buffer, 0, cnt);
				}
			} finally {
				if (bis != null) {
					bis.close();
				}
			}
		}
	}
}
