package model;

import java.io.File;

public class Disk {
	
	private File directory;
	
	public Disk(File directory) {
		this.directory = directory;
	}
	
	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	@Override
	public String toString() {
		return directory.toPath().toString();
	}
}

