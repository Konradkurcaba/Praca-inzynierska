package pl.kurcaba;

public enum TextColor {
	Red("#dd1111"),Green("#0ac247"),Grey("#7f7979");
	
	private String color;
	
	private TextColor(String aColor) {
		color = aColor;
	}
	
	public String getColor()
	{
		return color;
	}
}
