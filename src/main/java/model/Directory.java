package model;

import java.io.File;

public class Directory {

	public File directory;

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	public Directory(File directory) {
		this.directory = directory;
	}
	
	@Override
	public String toString() {
		return directory.getPath();
	}
}
