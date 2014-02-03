package factorybeantest;

public class Bean1 {

	private final int param1;
	private final String factoryName;
	
	public Bean1(final int param1, final String factoryName){
		this.param1 = param1;
		this.factoryName = factoryName;
	}

	public String getFactoryName() {
		return factoryName;
	}

	public int getParam1() {
		return param1;
	}

}
