package main;



import java.util.List;

import main.DistributionFactory;
import main.DistributionType;
import main.ExponentialDist;
import main.IDistribution;

public class RunExponDist
{
	public static void main(String[] args)
	{
		DistributionType type = DistributionType.Exponential;
		IDistribution dist = DistributionFactory.createGenerator(type);

		ExponentialDist expon = (ExponentialDist) dist;
		
		double location = 0;
		double sacle = 0.5;
		expon.setParameters(location, sacle);
		
    System.out.println(dist.toString());
		
    // 將密度函數資訊資訊輸出到 Excel檔
    int count = 100000;
    List<Double> samples = dist.getSamples(count);
    dist.toExcel(samples);
		
		System.out.println("\r\nEnding");
	}
}
