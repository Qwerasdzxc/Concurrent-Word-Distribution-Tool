package model;

public class FileInput {

	private Disk disk;

	private String name;
	
	public FileInput(Disk disk) {
		this.name = "0";
		this.disk = disk;
	}
	
	public Disk getDisk() {
		return disk;
	}
	public String getName() {
		return name;
	}

	public void setDisk(Disk disk) {
		this.disk = disk;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
