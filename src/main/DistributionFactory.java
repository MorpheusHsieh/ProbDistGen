package main;



public class DistributionFactory
{
  public static IDistribution createGenerator(DistributionType type)
  {
  	IDistribution dist = null;
  	
  	switch(type)
  	{
  		case Uniform:
  			dist = new UniformDist();
  			break;
  		case Exponential:
  			dist = new ExponentialDist();
  			break;
  		case Normal:
  			dist = new NormalDist();
  			break;
  		case Lognormal:
  			dist = new LognormalDist();
  				break;
  		case Geometric:
  			dist = new GeometricDist();
  			break;
  		case ExtremeValue:
  			dist = new ExtremeValueDist();
  			break;
  	}
  	
		return dist;
  }
}
