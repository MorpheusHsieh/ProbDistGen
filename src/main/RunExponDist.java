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
		
    // �N�K�ר�Ƹ�T��T��X�� Excel��
    int count = 100000;
    List<Double> samples = dist.getSamples(count);
    dist.toExcel(samples);
		
		System.out.println("\r\nEnding");
	}
}
