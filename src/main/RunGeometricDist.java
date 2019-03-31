package main;



import java.util.List;

import main.DistributionFactory;
import main.DistributionType;
import main.GeometricDist;
import main.IDistribution;

public class RunGeometricDist
{
	public static void main(String[] args)
	{
		DistributionType type = DistributionType.Geometric;
		IDistribution dist = DistributionFactory.createGenerator(type);

    GeometricDist geom = (GeometricDist) dist;
    
    double prob = 0.5;
    geom.setParameters(prob);

    System.out.println(dist.toString());
    
    // 將密度函數資訊資訊輸出到 Excel檔
    int count = 10000;
    List<Double> rands = dist.getSamples(count);
    dist.toExcel(rands);
		
		System.out.println("\r\nEnding");
	}

}
