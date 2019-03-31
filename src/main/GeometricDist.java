package main;



import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GeometricDist extends DistributionImpl 
{
	private double _Prob = 0.0;
	//private boolean isDebug = true;

	// ***************************************************************************
  // * Constructor
	// ***************************************************************************
	public GeometricDist() { this(0.5); }
	
	public GeometricDist(double prob) { this.setParameters(prob); } 
	
	// ***************************************************************************
  // * Public methods
	// ***************************************************************************
	public void setParameters(double prob) { this._Prob = prob; } 

	// ***************************************************************************
  // * Static methods
	// ***************************************************************************
	public static double density(int k, double prob)
	{
		return prob * Math.pow((1.0 - prob), k);
	}

	// ---------------------------------------------------------------------------
	public static double distribution(int k, double prob)
	{
		return 1.0 - Math.pow((1.0 - prob), k+1);
	}

	// ---------------------------------------------------------------------------
	// mean = (1 - p) / p
	public static double mean(double prob)
	{
		return ((1.0 - prob) / prob);
	}

	// ---------------------------------------------------------------------------
	/*
	 * Variance = (1 - p) / p^2
	 */
	public static double variance(double prob)
	{
		return (1.0 - prob) / (prob * prob);
	}
	
	// ---------------------------------------------------------------------------
	public static double meanToProb(double mean)
	{
		return (1.0 / (1.0 + mean));
	}
	
	// ***************************************************************************
  // * Override methods
	// ***************************************************************************
	@Override
	public DistributionType type()  { return DistributionType.Geometric; }

	@Override
	public double rand()
	{
  	int value = -1;

  	int stream = 1 + this._RAND.nextInt(LCGRand.MAX_STREAMS);
  	double U = LCGRand.lcgrand(stream);
  	value = (int) (Math.log(U) / Math.log(1.0 - this._Prob));
  	
  	return value;
	}
	
	// ---------------------------------------------------------------------------
	@Override
  public String toString()
  {
  	StringBuffer sb = new StringBuffer();
  	
  	String format = null;
  	
  	format = "Type: %s";
  	sb.append(String.format(format, this.type().toString()));
  	
  	format = "\r\n\r\nFormula: ";
  	sb.append(String.format(format));
  	
  	double prob = this._Prob;
  	
  	format = "\r\n  Prob: %f";
  	sb.append(String.format(format, prob));

  	format = "\r\n  Mean: %f";
  	sb.append(String.format(format, GeometricDist.mean(prob)));
  	
  	format = "\r\n  Variance: %f";
  	sb.append(String.format(format, GeometricDist.variance(prob)));
  	
  	sb.append(super.toString());
  	
  	return sb.toString();
  }

  // ---------------------------------------------------------------------------
	@Override
	public List<Double> getXcoordinates(List<Double> samples)
	{
		List<Double> coordinates = new LinkedList<Double>();
		
		Collections.sort(samples);
		
		int size = samples.size();
		double min = Math.floor(samples.get(0));
		double max = Math.ceil(samples.get(size-1));

		// 因為 Geometric 分佈在 X坐標軸為 min時有值，為方便顯示其值，所以從-1開始
		min -= 1.0;
		
		double scale = IDistribution.X_SCALE;
		double interval = 1.0 / scale;
		int steps = (int) ((max - min) / interval);
		
		for (int i=0; i<=steps; i++)
		{
			double x_posi = min + interval * i; 
			x_posi = Math.round(x_posi * scale) / scale;
			coordinates.add(x_posi);
		}
		
		return coordinates;
	}
	
  // ---------------------------------------------------------------------------
	@Override
	public Map<Double, Double> densitys(Map<Double, Double> probs)
	{
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
		
//		double scale = IDistribution.X_SCALE;
		double density = 0;
		
		for (Iterator<Double> it = probs.keySet().iterator(); it.hasNext(); )
		{
			double key = it.next(); 
			density = probs.get(key);
			map.put(key, density);
		}
		
		return map;
	}

}
