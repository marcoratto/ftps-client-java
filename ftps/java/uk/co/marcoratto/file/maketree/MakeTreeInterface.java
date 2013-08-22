package uk.co.marcoratto.file.maketree;

import java.io.File;

public interface MakeTreeInterface {
	
   public void onFileFound(File aFile) throws MakeTreeException;
   
   public void onDirFound(File aDirectory) throws MakeTreeException;
}