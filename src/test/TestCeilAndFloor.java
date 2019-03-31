package test;

public class TestCeilAndFloor
{
	public static void main(String[] args)
	{
		double num[] = { 10.4, 10.6, -10.4, -10.6 };
		
		String format = "\r\n%f\r\n  floor -> %f"
				          + "\r\n  Round -> %d\r\n  Ceil ->%f";
		for (int i=0; i<num.length; i++)
		{
			double v = num[i];
			System.out.println(String.format(format
					, v, Math.floor(v), Math.round(v), Math.ceil(v)));
			
		}
				
	}

}
