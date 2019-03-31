package main;



import java.util.List;

import main.DistributionFactory;
import main.DistributionType;
import main.IDistribution;
import main.NormalDist;

public class RunNormalDist
{
	public static void main(String[] args)
	{
		DistributionType type = DistributionType.Normal;
		IDistribution dist = DistributionFactory.createGenerator(type);

		NormalDist normal = (NormalDist) dist;

		double location = 0.0;
		double scale = 1.0;
		normal.setParameters(location, scale);
		
    System.out.println(dist.toString());
		
    // �N�K�ר�Ƹ�T��T��X�� Excel��
    int count = 1000000;
    List<Double> samples = normal.getSamples(count);
    dist.toExcel(samples);
		
		System.out.println("\r\nEnding");
	}
}
