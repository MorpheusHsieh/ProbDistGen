package main;



import java.util.List;
import java.util.Map;

public interface IDistribution
{
	public static final double X_SCALE = 10;
	
	public static final int EXPERIMENT_COUNT = 1000000;
	
	public static final double ORIGIN = 0.0;
	public static final double INFINITY = Double.MAX_VALUE;
	public static final double MINUS_INFINITY = Double.MIN_VALUE;
	
  public DistributionType type();
  
  public double rand();
  
  public List<Double> getSamples(int num);
  public double mean(List<Double> sammples);
  public double variance(List<Double> sampless);
  
  public List<Double> getXcoordinates(List<Double> samples);
  
  public Map<Double, Double> probabilitys(List<Double> samples);
  
  public Map<Double, Double> densitys(Map<Double, Double> porbs);
  public Map<Double, Double> distributions(Map<Double, Double> porbs);
  
  public void toExcel(List<Double> samples);
}
