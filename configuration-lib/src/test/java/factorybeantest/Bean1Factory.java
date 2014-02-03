package factorybeantest;

public class Bean1Factory {
	public Bean1Factory(String factoryName) {
		super();
		this.factoryName = factoryName;
	}

	private String factoryName;	
	
	public Bean1 getInstance(final int value){
		return new Bean1(value, factoryName);
	}

}
