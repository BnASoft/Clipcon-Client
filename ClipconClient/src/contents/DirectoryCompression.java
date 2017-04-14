package contents;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DirectoryCompression {
	private static final int COMPRESSION_LEVEL = 1;
    private static final int BUFFER_SIZE = 1024 * 2;
    
    /**
     * ������ ������ Zip ���Ϸ� �����Ѵ�.
     * @param sourcePath - ���� ��� ���丮
     * @param output - ���� zip ���� �̸�
     * @throws Exception
     */
    public static void compress(File file) throws Exception {

    	String filePath = file.getPath();
    	String outputFileName = filePath + ".zip";
    	System.out.println("outputFileName : " + outputFileName);
    	
        // ���� ����� ���丮�� ������ �ƴϸ� �����Ѵ�.
        if (!file.isFile() && !file.isDirectory()) {
            throw new Exception("���� ����� ������ ã�� ���� �����ϴ�.");
        }

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ZipOutputStream zos = null;

        try {
            fos = new FileOutputStream(outputFileName); // FileOutputStream
            bos = new BufferedOutputStream(fos); // BufferedStream
            zos = new ZipOutputStream(bos); // ZipOutputStream
            zos.setLevel(COMPRESSION_LEVEL); // ���� ���� - �ִ� ������� 9, ����Ʈ 8
            zipEntry(file, filePath, zos); // Zip ���� ����
            zos.finish(); // ZipOutputStream finish
        } finally {
            if (zos != null) {
                zos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fos != null) {
                fos.close();
            }
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
        // sourceFile �� ���丮�� ��� ���� ���� ����Ʈ ������ ���ȣ��
        if (file.isDirectory()) {
            if (file.getName().equalsIgnoreCase(".metadata")) { // .metadata ���丮 return
                return;
            }
            File[] fileArray = file.listFiles(); // sourceFile �� ���� ���� ����Ʈ
            for (int i = 0; i < fileArray.length; i++) {
                zipEntry(fileArray[i], filePath, zos); // ��� ȣ��
            }
        } else { // sourcehFile �� ���丮�� �ƴ� ���
            BufferedInputStream bis = null;
            try {
                String sFilePath = file.getPath();
                String zipEntryName = sFilePath.substring(filePath.length() + 1, sFilePath.length());

                bis = new BufferedInputStream(new FileInputStream(file));
                ZipEntry zentry = new ZipEntry(zipEntryName);
                zentry.setTime(file.lastModified());
                zos.putNextEntry(zentry);

                byte[] buffer = new byte[BUFFER_SIZE];
                int cnt = 0;
                while ((cnt = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                    zos.write(buffer, 0, cnt);
                }
                zos.closeEntry();
            } finally {
                if (bis != null) {
                    bis.close();
                }
            }
        }
    }

}
