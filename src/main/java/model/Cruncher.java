package model;

public class Cruncher {
	
	private int arity;

	private String name;
	
	public Cruncher(int arity) {
		this.arity = arity;
		this.name = "Counter 0";
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public int getArity() {
		return arity;
	}

	public void setArity(int arity) {
		this.arity = arity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
