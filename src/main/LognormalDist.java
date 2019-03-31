package main;



import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import main.ErrorFunction;

public class LognormalDist extends DistributionImpl
{
	private double _Location = 0.0;  // a
	private double _Scale = 0.0;     // mu
  private double _Shape = 0.0;     // sigma
	
	private NormalDist _Gaussian = null;
	
	// ***************************************************************************
  // * Constructor
	// ***************************************************************************
	public LognormalDist() { this(0.0, 0.0, 1.0); }
	
	public LognormalDist(double scale, double shape) { this(0.0, scale, shape); }

	public LognormalDist(double location, double scale, double shape) 
	{
	  this.setParameters(location, scale, shape);
	}
	
	// ***************************************************************************
  // * Static methods
	// ***************************************************************************
	public void setParameters(double location, double scale, double shape)
	{
		this._Location = location;
		this._Scale = scale;
		this._Shape = shape;

		this._Gaussian = new NormalDist(scale, shape);
	}

	// ***************************************************************************
  // * Static methods
	// ***************************************************************************
	public static double density(double x, double location, double scale
			, double shape)
	{
		double density = 0.0;
		
		double a = location;
		double mu = scale;
		double sigma = shape;
		
		assert(sigma > 0);
		if (x <= a) return 0;
		
		double part1 = 1.0 / (x * Math.sqrt(2.0 * Math.PI) * sigma * (x - a));
		double part2 = (-1/2) * Math.pow((Math.log(x-a)-mu), 2.0) / (sigma*sigma);
		density = part1 * Math.exp(part2);
		
		return density;
	}

	// ---------------------------------------------------------------------------
	public static double distribution(double x, double location, double scale
			, double shape)
	{
		double dist = -1.0;
		
		double a = location;
		double sigma = shape;
		double mu = scale;
		
		assert(sigma > 0);
		if (x <= a) return 0;
		
		double z = (Math.log(x-a) - mu) / (Math.sqrt(2) * sigma); 
		dist = (1/2) * (1 + ErrorFunction.erf(z));
		
		return dist;
	}

		
	// ---------------------------------------------------------------------------
	public static double median(double location, double scale)
	{
		return location + Math.exp(scale);
	}

	// ---------------------------------------------------------------------------
	public static double mean(double location, double scale, double shape)
	{
		double z = scale + shape * shape / 2.0;
		return location + Math.exp(z);
	}

	// ---------------------------------------------------------------------------
	public static double variance(double location, double scale, double shape)
	{
		double variance = shape * shape;
		double z = 2.0 * scale + variance;
		return Math.exp(z) * (Math.exp(variance) - 1.0);
	}
	

	// ***************************************************************************
  // * Override methods
	// ***************************************************************************
	@Override
	public DistributionType type()  { return DistributionType.Lognormal; }

	// ---------------------------------------------------------------------------
	@Override
	public double rand() 
	{
		double rand = -1;
		
		double sigma = this._Shape;
		assert(sigma > 0);
		rand = this._Location + Math.exp(this._Gaussian.rand());
		
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

  	double a = this._Location;
  	format = "\r\n  Location: %f";
  	sb.append(String.format(format, a));

  	double mu = this._Scale;
  	format = "\r\n  Scale: %f";
  	sb.append(String.format(format, mu));

  	double sigma = this._Shape;
  	format = "\r\n  Shape: %f";
  	sb.append(String.format(format, sigma));
  	
  	format = "\r\n  Mean: %f";
  	sb.append(String.format(format, LognormalDist.mean(a, mu, sigma)));
  	
  	format = "\r\n  Variance: %f";
  	double variance = LognormalDist.variance(a, mu, sigma);
  	sb.append(String.format(format, variance));

  	sb.append(super.toString());
  	
  	return sb.toString();
  }
  
  // ---------------------------------------------------------------------------
	@Override
	public List<Double> getXcoordinates(List<Double> samples)
	{
		List<Double> coordinates = new LinkedList<Double>();
		
		double min = 0.0;
		double max = samples.get(0);
		for (Iterator<Double> it = samples.iterator(); it.hasNext(); )
		{
			double sample = it.next();
			if (sample > max) max = sample;
		}
		max = Math.ceil(max);
		
		// 建立 X 軸座標
		double interval = 1.0 / (double) IDistribution.X_SCALE;
		int steps = (int) ((max - min) / interval);
		
		double scale = 1.0 / interval;
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
		
		double scale = IDistribution.X_SCALE;
		double density = 0;
		
		for (Iterator<Double> it = probs.keySet().iterator(); it.hasNext(); )
		{
			double key = it.next(); 
			density = probs.get(key) * scale;
			map.put(key, density);
		}
		
		// 在 X坐標軸為 0時的機率密度
		double x = 0; 
		double a = this._Location;
		double mu = this._Scale;
		double sigma = this._Shape;
		density = LognormalDist.density(x, a, mu, sigma);
		map.put(x, density);
		
		return map;
	}
}
