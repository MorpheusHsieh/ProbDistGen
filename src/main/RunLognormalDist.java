package main;



import java.util.List;

import main.DistributionFactory;
import main.DistributionType;
import main.IDistribution;
import main.LognormalDist;

public class RunLognormalDist
{
	public static void main(String[] args)
	{
		DistributionType type = DistributionType.Lognormal;
		IDistribution dist = DistributionFactory.createGenerator(type);

    System.out.println(dist.toString());
    
    LognormalDist ln = (LognormalDist) dist;
    
    double location = 0.0;  // a
    double scale = 0.0;     // mu
    double shape = 0.5;     // sigma
    ln.setParameters(location, scale, shape);
		
    // �N�K�ר�Ƹ�T��T��X�� Excel��
    int count = 100000;
    List<Double> rands = dist.getSamples(count);
    dist.toExcel(rands);
		
		System.out.println("\r\nEnding");
	}

}
