package main;



import java.util.List;

import main.DistributionFactory;
import main.DistributionType;
import main.IDistribution;
import main.UniformDist;

public class RunUniformDist
{
	public static void main(String[] args)
	{
		DistributionType type = DistributionType.Uniform;
		IDistribution dist = DistributionFactory.createGenerator(type);
		
		UniformDist U = (UniformDist) dist;
		
		double xMin = 0;
		double xMax = 1.0;
		U.setParameters(xMin, xMax);

    System.out.println(dist.toString());
		
    int count = 100000;
    List<Double> samples = dist.getSamples(count);
    
    // �N�K�ר�Ƹ�T��T��X�� Excel��
    dist.toExcel(samples);
		
		System.out.println("\r\nEnding");
	}

}
