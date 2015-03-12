package br.ufrj.cos.labia.aips.dal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipError;
import java.util.zip.ZipInputStream;

public class ZipReader {

	private byte[] buffer;
	private File file;

	public ZipReader(File file) {
		this.buffer = new byte[1024 * 512];
		this.file = file;
	}
	
	public ZipReader get(String name, OutputStream s) throws IOException {
		FileInputStream stream = new FileInputStream(this.file);
		ZipInputStream zis = new ZipInputStream(stream);
		
		for (ZipEntry ze = zis.getNextEntry(); ze != null; 
				ze = zis.getNextEntry()) {
			
			if (ze.getName().equals(name)) {
				
	            int len;
	            while ((len = zis.read(buffer)) > 0)
	            	s.write(buffer, 0, len);
	            s.close();
	            
	            zis.close();
	            return this;
			}
		}
		
		zis.close();
		throw new ZipError("Could not find entry with name " + name);
	}
	
}
