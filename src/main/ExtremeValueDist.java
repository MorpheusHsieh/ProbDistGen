package main;



import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExtremeValueDist extends DistributionImpl
{
	private double _Location = -1;
	private double _Scale = -1;

	// ***************************************************************************
  // * Constructor
	// ***************************************************************************
	public ExtremeValueDist() { this(1.0); }

	public ExtremeValueDist(double scale) { this(0.0, scale); }	

	public ExtremeValueDist(double location, double scale)
	{
		this.setParameters(location, scale);
	}
	
	
	// ***************************************************************************
  // * Public Methods
	// ***************************************************************************
	public void setParameters(double location, double scale)
	{
		assert (scale > 0);
		this._Location = location;
		this._Scale = scale;
	}
	
	// ---------------------------------------------------------------------------
	public static double density(double x, double location, double scale) 
	{
		double density = -1;
		
		double a = location, b = scale;
		double z = (x - a) / b;
		density = (1 / b) * Math.exp(z - Math.exp(z));
		
		return density;
	}
	
	// ---------------------------------------------------------------------------
	public static double distribution(double x, double location, double scale) 
	{
		double density = -1;
		
		double a = location, b = scale;
		double z = (x - a) / b;
		density = 1.0 - Math.exp(-Math.exp(z));
		
		return density;
	}
	
	// ---------------------------------------------------------------------------
	public static double median(double location, double scale)
	{
	  double median = -1;
	  
	  median = location + scale * Math.log(Math.log(2)); 
	  
	  return median;
	}
	
	// ---------------------------------------------------------------------------
	public static double mean(double location, double scale) 
	{
		double mean = -1;
		
		double gamma = 0.57721; // Euler's constant
		mean = location - scale * gamma; 
				
		return mean;
	}

	// ---------------------------------------------------------------------------
	public static double variance(double scale) 
	{
		double variance = -1;
		
		variance = scale * scale * Math.PI * Math.PI / 6; 
				
		return variance;
	}
	
	
	// ***************************************************************************
  // * Override Methods
	// ***************************************************************************
	@Override
	public DistributionType type() { return DistributionType.ExtremeValue; }

	// ---------------------------------------------------------------------------
	@Override
	public double rand()
	{
		double rand = -1;
		
		int stream = 1 + this._RAND.nextInt(LCGRand.MAX_STREAMS);
		double u = LCGRand.lcgrand(stream);
	  rand = this._Location +	this._Scale * Math.log(-Math.log(u));
		
		return rand;
	}

	// ---------------------------------------------------------------------------
	@Override
	public List<Double> getXcoordinates(List<Double> samples)
	{
		List<Double> coordinates = new LinkedList<Double>();
		
		int size = samples.size();
		double min = samples.get(0);
		double max = samples.get(size-1);
		for (Iterator<Double> it = samples.iterator(); it.hasNext(); )
		{
			double sample = it.next();
			if (sample < min) min = sample;
			if (sample > max) max = sample;
		}
		min = Math.floor(min);
		max = Math.ceil(max);
		
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
		
		for (Iterator<Double> it = probs.keySet().iterator(); it.hasNext(); )
		{
			double key = it.next(); 
			double value = probs.get(key);
			map.put(key, value * scale);
		}
		
		return map;
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
  	
  	format = "\r\n  Range\t[%s, %s]";
  	sb.append(String.format(format, "-Infinity", "Infinity"));
  	
  	format = "\r\n  Location: %f";
  	double location = this._Location;
  	sb.append(String.format(format, location));
  	
  	format = "\r\n  Scale: %f";
  	double scale = this._Scale;
  	sb.append(String.format(format, scale));

  	format = "\r\n  Mean: %f";
  	double mean = ExtremeValueDist.mean(location, scale);
  	sb.append(String.format(format, mean));
  	
  	format = "\r\n  Variance: %f";
  	double variance = ExtremeValueDist.variance(scale);
  	sb.append(String.format(format, variance));
  	
  	sb.append(super.toString());
  	
  	return sb.toString();
  }
}
