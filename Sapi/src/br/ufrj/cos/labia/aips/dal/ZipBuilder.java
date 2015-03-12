package br.ufrj.cos.labia.aips.dal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipBuilder {

	private byte[] buffer;
	private ZipOutputStream zos;

	public ZipBuilder(File file) throws FileNotFoundException {
		zos = new ZipOutputStream(new FileOutputStream(file));
		buffer = new byte[1024 * 512];
	}
	
	public ZipBuilder put(String filename, InputStream s) throws IOException {
		zos.putNextEntry(new ZipEntry(filename));
		
		int len;
		while ((len = s.read(buffer)) > 0)
			zos.write(buffer, 0, len);
		
		zos.closeEntry();
		return this;
	}

	public ZipBuilder close() throws IOException {
		zos.close();
		return this;
	}

}
