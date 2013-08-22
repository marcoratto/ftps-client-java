package uk.co.marcoratto.file.maketree;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;

public class MakeTree {

	private static Logger logger = Logger.getLogger(MakeTree.class);
	
	private int level = 0;
	
	public MakeTree(MakeTreeInterface obj) {
		this.target = obj;
	}

	public void searchDirectoryFile(File f) throws MakeTreeException {
		this.searchFile(f, "*.*");
	}

	public void searchFile(File f) throws MakeTreeException {
		this.searchFile(f, "*.*");
	}

	public void searchFile(File f, String pattern) throws MakeTreeException {
		try {
			if (f.isDirectory()) {
				String[] list = f.list();
				for (int i = 0; i < list.length; i++) {
					this.searchFile(new File(f, list[i]), pattern);
				}
			} else {
				this.target.onFileFound(f);
			}
		} catch (Exception e) {
			throw new MakeTreeException(e);
		}
	}

	public void searchDirectoryFile(File fromDirectory, String pattern, boolean recursive) throws MakeTreeException {
		try {
			if (fromDirectory.isDirectory()) {
				this.target.onDirFound(fromDirectory);
				FileFilter fileFilter = new WildcardFileFilter(pattern);
				File[] files = fromDirectory.listFiles(fileFilter);
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {						
						this.target.onFileFound(files[i]);	
					}
				}
				if (recursive) {
					String[] list = fromDirectory.list();
					for (int i = 0; i < list.length; i++) {
						this.searchDirectoryFile(new File(fromDirectory, list[i]), pattern, recursive);						
					}				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MakeTreeException(e);
		}
	}

	public void searchDirectory(File f) throws MakeTreeException {
		try {
			if (f.isDirectory()) {
				this.target.onDirFound(f);
				String[] list = f.list();
				for (int i = 0; i < list.length; i++) {
					this.searchDirectory(new File(f, list[i]));
				}
			}
		} catch (Throwable t) {
			throw new MakeTreeException(t);
		}
	}

	private MakeTreeInterface target = null;
}
