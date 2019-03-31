package main;



import java.util.List;

import main.DistributionFactory;
import main.DistributionType;
import main.ExtremeValueDist;
import main.IDistribution;

public class RunExtremeValueDist
{
	public static void main(String[] args)
	{
		DistributionType type = DistributionType.ExtremeValue;
		IDistribution dist = DistributionFactory.createGenerator(type);

		ExtremeValueDist ev = (ExtremeValueDist) dist;
		
		double location = 0.0;
		double scale = 0.5;
		ev.setParameters(location, scale);
		
    System.out.println(dist.toString());
		
    // 將密度函數資訊資訊輸出到 Excel檔
    int count = 100000;
    List<Double> samples = dist.getSamples(count);
    dist.toExcel(samples);
		
		System.out.println("\r\nEnding");
	}
}
