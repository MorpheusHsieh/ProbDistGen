package main;



import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import main.ErrorFunction;

public class NormalDist extends DistributionImpl
{
	private double _Lcoation = 0.0;
	private double _Scale = 1.0;
	
	private double _SecondRand = 0.0;
  private boolean _SecondValid = false;
	
  private boolean isDebug = true;
  
	// ***************************************************************************
  // * Constructor
	// ***************************************************************************
	public NormalDist() { this(1.0); }
	
	public NormalDist(double scale) { this(0.0, scale); } 

	public NormalDist(double location, double scale) 
	{
		this.setParameters(location, scale);
	}
	
	// ***************************************************************************
  // * Static methods
	// ***************************************************************************
  public void setParameters(double location, double scale)
  {
		this._Lcoation = location;
		this._Scale = scale;
  }
	
	// ***************************************************************************
  // * Static methods
	// ***************************************************************************
	public static double density(double x, double location, double scale)
	{
		double density = 0.0;
		
		double mean = location;
		double stddev = scale;
		
		double part1 = 1.0 / ((Math.sqrt(2.0 * Math.PI)) * stddev);
		double part2 = -1 * Math.pow((x - mean), 2.0) / (2.0 * stddev * stddev);
		density = part1 * Math.exp(part2);
		
		return density;
	}
	
  // ---------------------------------------------------------------------------
	public static double distribution(double x, double location, double scale)
	{
		double dist = -1.0;

		double mean = location;
		double stddev = scale;
		double z = (x - mean)/((Math.sqrt(2.0) * stddev));
	  dist = (1 / 2.0) * (1.0 + ErrorFunction.erf(z));

	  return dist;
	}

  // ---------------------------------------------------------------------------
	public static double mean(double location)
	{
		return location;
	}

  // ---------------------------------------------------------------------------
	public static double median(double location)
	{
		return location;
	}
	
  // ---------------------------------------------------------------------------
	public static double variance(double scale)
	{
		return scale * scale;
	}
	
	// ***************************************************************************
  // * Override methods
	// ***************************************************************************
	@Override
	public DistributionType type()  { return DistributionType.Normal; }

	// ---------------------------------------------------------------------------
	@Override
	public double rand()
	{
		double rand = 0.0;
		
		if (this._SecondValid) 
		{
      this._SecondValid = false;
      return this._SecondRand;
    }

	  double mu = this._Lcoation;
	  double sigma = this._Scale;
	  assert(sigma > 0);
		
		int stream1 = 1 + this._RAND.nextInt(LCGRand.MAX_STREAMS);
		int stream2 = 1 + this._RAND.nextInt(LCGRand.MAX_STREAMS);
	  
		double v1, v2, w;
		do {
			v1 = 2.0 * LCGRand.lcgrand(stream1) - 1.0;
			v2 = 2.0 * LCGRand.lcgrand(stream2) - 1.0;
			w = v1 * v1  + v2 * v2;
		} while (w > 1.0);
		
	  double y1 = v1 * Math.sqrt(-2.0 * Math.log(w) / w);
	  double y2 = v2 * Math.sqrt(-2.0 * Math.log(w) /w);
	  
	  double x1 = mu + y1 * sigma;
	  double x2 = mu + y2 * sigma;
	  
	  this._SecondRand = x2;
	  this._SecondValid = true;
	  rand = x1;
	  
		return rand;
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
  	
  	double mu = this._Lcoation;
  	format = "\r\n  Location: %f";
  	sb.append(String.format(format, mu));
  	
  	double sigma = this._Scale;
  	format = "\r\n  Scale: %f";
  	sb.append(String.format(format, sigma));

  	format = "\r\n  Mean: %f";
  	sb.append(String.format(format, NormalDist.mean(mu)));

  	format = "\r\n  Median: %f";
  	sb.append(String.format(format, NormalDist.median(mu)));
  	
  	format = "\r\n  Variance: %f";
  	sb.append(String.format(format, NormalDist.variance(sigma)));
  	
  	sb.append(super.toString());
  	
  	return sb.toString();
  }

  // ---------------------------------------------------------------------------
	@Override
	public List<Double> getXcoordinates(List<Double> samples)
	{
		List<Double> coordinates = new LinkedList<Double>();
		
		double min = samples.get(0), max = samples.get(0);
		for (Iterator<Double> it = samples.iterator(); it.hasNext(); )
		{
			double sample = it.next();
			if (sample < min) min = sample;
			if (sample > max) max = sample;
		}
		if (!isDebug) 
		{
			String format = "\r\nBefore:\r\n  Min: %f\r\n  Max: %f";
			System.out.println(String.format(format, min, max));
		}
		
		min = Math.floor(min);
		max = Math.ceil(max);
		
		// Normal 分佈應為左右對稱
		double mean = this._Lcoation;
		double left = mean - min;
		double right = max - mean;
		if (left < right) min = mean - right; else max = mean + left;
		
		double interval = 1.0 / (double) IDistribution.X_SCALE;
		int steps = (int) ((max - min) / interval);
		
		if (!isDebug)
		{
			System.out.println("\r\nAfter:");
			String format = "\r\nMin: %f\r\nMax: %f\r\nInterval: %f\r\nSteps: %d";
			System.out.println(String.format(format, min, max, interval, steps));
		}
		
		double scale = 1.0 / interval;
		for (int i=0; i<=steps; i++)
		{
			double x_posi = min + interval * i; 
			x_posi = Math.round(x_posi * scale) / scale;
			coordinates.add(x_posi);
		}
		
		if (!isDebug)
		{
			System.out.println("\r\nCoordinates:");
			for (Double posi : coordinates) System.out.println(posi);
		}
		
		return coordinates;
	}
	
  // ---------------------------------------------------------------------------
	@Override
	public Map<Double, Double> densitys(Map<Double, Double> probs)
	{
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
		
		double density = 0;
		double scale = IDistribution.X_SCALE;
		
		for (Iterator<Double> it = probs.keySet().iterator(); it.hasNext(); )
		{
			double key = it.next(); 
			double value = probs.get(key);
			density = value * scale;
			//density = value;
			map.put(key, density);
		}
		
		// 在X坐標軸為0時的機率密度
		double x = 0; 
		density = NormalDist.density(x, this._Lcoation, this._Scale);
		map.put(x, density);
		
		return map;
	}
}
