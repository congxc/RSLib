package com.rs.rslib.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtil {
	private static final  String TAG = "FileUtil";

	/**
	 * @param b
	 * @param path 文件路径
	 * @param fileName 文件名称
	 * @return 全路径
	 */
	public static String saveBitmap2File(Bitmap b,String path,String fileName){
		if(b == null){
			return "";
		}
		LogUtils.info("TAGxc", "saveBitmap path: "+path);
		File parent = new File(path);
		if (!parent.exists()) {
			parent.mkdirs();
		}
		String jpegName = path + File.separator+ fileName +".jpg";
		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {
			File file = new File(jpegName);
			if(file.exists() && file.length() > 0){
				return jpegName;
			}else{
				FileOutputStream fout = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fout);
				b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jpegName;
	}


	public static String saveFile(InputStream is,String path,String fileName){
		File parent = new File(path);
		if (!parent.exists()) {
			parent.mkdirs();
		}
		String pathName = path + File.separator + fileName;
		FileOutputStream fout = null;
		BufferedOutputStream os = null;
		BufferedInputStream inputStream = null;
		try {
			File file = new File(pathName);
			if (file.exists() && file.length() > 0) {
				return pathName;
			}
			fout = new FileOutputStream(file);
			os = new BufferedOutputStream(fout);
			inputStream = new BufferedInputStream(is);
			byte[] b = new byte[1024*5];
			int len;
			while ((len = inputStream.read(b)) != -1){
				os.write(b,0,len);
				os.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (fout != null) {
					fout.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return pathName;
		}
	}
	/**
	 * 删除单个文件
	 *
	 * @param sPath 被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
	/**
	 * 删除单个文件
	 *
	 * @param file 被删除文件
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(File file) {
		boolean flag = false;
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 *
	 * @param sPath 要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public static boolean deleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 *
	 * @param sPath 被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				// 删除子文件
				if (files[i].isFile()) {
					flag = deleteFile(files[i].getAbsolutePath());
					if (!flag)
						break;
				} // 删除子目录
				else {
					flag = deleteDirectory(files[i].getAbsolutePath());
					if (!flag)
						break;
				}
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			System.out
					.println("dir " + dirFile.getAbsolutePath() + " del succ");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除目录下的文件，不删除目录
	 *
	 * @param sPath 被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectoryChilds(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				// 删除子文件
				if (files[i].isFile()) {
					flag = deleteFile(files[i].getAbsolutePath());
					if (!flag)
						break;
				} // 删除子目录
				else {
					flag = deleteDirectory(files[i].getAbsolutePath());
					if (!flag)
						break;
				}
			}
		}
		if (!flag)
			return false;
		return true;
	}
	/**
	 * 创建目录
	 *
	 * @param destDirName 目录名字
	 * @return 是否创建成功
	 */
	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			System.out.println("创建目录" + destDirName + "失败，目标目录已存在！");
			return false;
		}
		if (!destDirName.endsWith(File.separator))
			destDirName = destDirName + File.separator;
		// 创建单个目录
		if (dir.mkdirs()) {
			System.out.println("创建目录" + destDirName + "成功！");
			return true;
		} else {
			System.out.println("创建目录" + destDirName + "成功！");
			return false;
		}
	}
	private static final int BUFF_SIZE = 1024 * 1024*2; // 2M Byte

	/**
	 * 压缩strPath目录下的所有文件
	 * @param strPath
	 * @param zipFile
	 */
	public static File zipFiles(String strPath,File zipFile) throws Exception{
		LinkedList<File> list = new LinkedList<>();
		getFiles(list, strPath);
		zipFiles(list,zipFile);
		return zipFile;
	}

	/**
	 * 文件夹遍历
	 * @param strPath
	 * @return
	 */
	public static Collection<File> getFiles(LinkedList<File> list ,String strPath){
		File dir = new File(strPath);
		File file[] = dir.listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()){
				list.add(file[i]);
			}else{
				getFiles(list,file[i].getAbsolutePath());
			}
		}
		return list;
	}
	/**
	 * 批量压缩文件（夹）
	 *
	 * @param resFileList 要压缩的文件（夹）列表
	 * @param zipFile 生成的压缩文件
	 * @throws IOException 当压缩过程出错时抛出
	 */
	public static File zipFiles(Collection<File> resFileList, File zipFile) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
				zipFile), BUFF_SIZE));
		for (File resFile : resFileList) {
			zipFile(resFile, zipout, "");
		}
		zipout.close();
		return zipFile;
	}
	/**
	 * 批量压缩文件（夹）
	 *
	 * @param resFileList 要压缩的文件（夹）列表
	 * @param zipFile 生成的压缩文件
	 * @param comment 压缩文件的注释
	 * @throws IOException 当压缩过程出错时抛出
	 */
	public static void zipFiles(Collection<File> resFileList, File zipFile, String comment)
			throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
				zipFile), BUFF_SIZE));
		for (File resFile : resFileList) {
			zipFile(resFile, zipout, "");
		}
		zipout.setComment(comment);
		zipout.close();
	}
	/**
	 * 压缩文件
	 *
	 * @param resFile 需要压缩的文件（夹）
	 * @param zipout 压缩的目的文件
	 * @param rootpath 压缩的文件路径
	 * @throws FileNotFoundException 找不到文件时抛出
	 * @throws IOException 当压缩过程出错时抛出
	 */
	private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath)
			throws FileNotFoundException, IOException {
		rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator)
				+ resFile.getName();
		rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();
			for (File file : fileList) {
				zipFile(file, zipout, rootpath);
			}
		} else {
			byte buffer[] = new byte[BUFF_SIZE];
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),
					BUFF_SIZE);
			zipout.putNextEntry(new ZipEntry(rootpath));
			int realLength;
			while ((realLength = in.read(buffer)) != -1) {
				zipout.write(buffer, 0, realLength);
			}
			in.close();
			zipout.flush();
			zipout.closeEntry();
		}
	}
	/**
	 * 压缩文件
	 *
	 * @param sourceFile
	 * @param zipFile
	 * @return
	 * @throws IOException
	 */
	public static File zipFile(File sourceFile, File zipFile) throws Exception {
		BufferedInputStream origin = null;
		ZipOutputStream out = null;
		boolean flag = false;
		int BUFFER = 4096;//缓存大小
		FileOutputStream fileOutputStream = null;
		FileInputStream fis = null;
		try {
			fileOutputStream = new FileOutputStream(zipFile);
			out = new ZipOutputStream(fileOutputStream);
			fis = new FileInputStream(sourceFile);
			origin = new BufferedInputStream(fis, BUFFER);
			ZipEntry entry = new ZipEntry(sourceFile.getName());
			byte data[] = new byte[BUFFER];
			int count;
			out.putNextEntry(entry);
			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			out.closeEntry();
			if (flag) {
				flag = sourceFile.delete();
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			try {
				if (origin != null) origin.close();
			} catch (Exception ignored) {
			}
			try {
				if (fis != null) fis.close();
			} catch (Exception ignored) {
			}
			try {
				if (fileOutputStream != null) fileOutputStream.close();
			} catch (Exception ignored) {
			}
			try {
				if (out != null) out.close();
			} catch (Exception ignored) {
			}
		}
		return zipFile;
	}


	/**
	 * 解压,处理下载的zip工具包文件
	 *
	 * @param directory 要解压到的目录
	 * @param zip       工具包文件
	 * @return 返回最后一个文件对象, 主要用处针对如果只有一个文件
	 * @throws Exception 操作失败时抛出异常
	 */
	public static File unzipFile(String directory, File zip) throws IOException {
		File child = null;
		ZipInputStream zis = null;
		FileOutputStream output = null;
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(zip);
			zis = new ZipInputStream(inputStream);
			ZipEntry ze = zis.getNextEntry();
			File parent = new File(directory);
			if (!parent.exists() && !parent.mkdirs()) {
				zis.close();
				throw new IOException("创建解压目录 \"" + parent.getAbsolutePath() + "\" 失败");
			}
			while (ze != null) {
				String name = ze.getName();
				child = new File(parent, name);
				output = new FileOutputStream(child);
				byte[] buffer = new byte[10240];
				int bytesRead;
				while ((bytesRead = zis.read(buffer)) > 0) {
					output.write(buffer, 0, bytesRead);
				}
				output.flush();
				output.close();
				ze = zis.getNextEntry();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException(e);
		} finally {
			try {
				if (zis != null) zis.close();
			} catch (Exception ignored) {
			}
			try {
				if (inputStream != null) inputStream.close();
			} catch (Exception ignored) {
			}
			try {
				if (output != null) output.close();
			} catch (Exception ignored) {
			}
		}
		return child;
	}
	/**
	 * 保存文件到磁盘
	 *
	 * @param target
	 * @param in
	 */
	public static void saveFile(File target, InputStream in) throws Exception {
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(target);
			byte[] buffer = new byte[10240];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) > 0) {
				output.write(buffer, 0, bytesRead);
			}
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
