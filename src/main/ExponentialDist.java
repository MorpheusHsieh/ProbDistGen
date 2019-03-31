package main;



import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExponentialDist extends DistributionImpl
{
	private double _Location = 0.0;
	private double _Scale = 0.0; 
	
	// ***************************************************************************
  // * Constructor
	// ***************************************************************************
	public ExponentialDist() { this(1.0); }

	public ExponentialDist(double scale) { this(0.0, scale); }
	
	public ExponentialDist(double location, double scale) 
	{
		this.setParameters(location, scale);
	}
	
	// ***************************************************************************
  // * Static methods
	// ***************************************************************************
  public void setParameters(double location, double scale) 
  {
  	this._Location = location;
  	this._Scale = scale; 
  }
	
	// ***************************************************************************
  // * Static methods
	// ***************************************************************************
	public static double density(double x, double location, double scale)
	{
		double density = 0.0;
		
		if (x >= location) 
		{
			double z = (x - location) / scale;
			density = (1.0 / scale) * Math.exp(-z);
		}
			
		return density;
	}

	// ---------------------------------------------------------------------------
	public static double distribution(double x, double location, double scale)
	{
		double dist = 0.0;
		
		if (x >= location)
		{
			double z = (x - location) / scale;
			dist = 1.0 - Math.exp(-z);
		}
		
		return dist;
	}
	
	// ---------------------------------------------------------------------------
	public static double median(double location, double scale)
	{
		return location + scale * Math.log(2);
	}

	// ---------------------------------------------------------------------------
	public static double mean(double location, double scale)
	{
		return location + scale;
	}

	// ---------------------------------------------------------------------------
	public static double variance(double location, double scale)
	{
		return scale * scale;
	}

	// ***************************************************************************
  // * Override methods
	// ***************************************************************************
	@Override
	public DistributionType type()  { return DistributionType.Exponential; }

	@Override
	public double rand()
	{
  	double rand = 0;
  	
  	int stream = 1 + this._RAND.nextInt(LCGRand.MAX_STREAMS);
  	double u = LCGRand.lcgrand(stream);
  	rand = this._Location - this._Scale * Math.log(u);
  	
  	return rand;
	}
	
	// ---------------------------------------------------------------------------
  public String toString()
  {
  	StringBuffer sb = new StringBuffer();
  	String format = null;
  	
  	format = "Type: %s";
  	sb.append(String.format(format, this.type().toString()));
  	
  	format = "\r\n\r\nFormula: ";
  	sb.append(String.format(format));
  	
  	format = "\r\n  Range\t[%f, %s]";
  	sb.append(String.format(format, 0.0, "Infinity"));
  	
  	format = "\r\n  Location: %f";
  	double a = this._Location;
  	sb.append(String.format(format, a));

  	format = "\r\n  Scale: %f";
  	double b = this._Scale;
  	sb.append(String.format(format, b));
  	
  	format = "\r\n  Mean: %f";
  	sb.append(String.format(format, ExponentialDist.mean(a, b)));
  	
  	format = "\r\n  Variance: %f";
  	sb.append(String.format(format, ExponentialDist.variance(a, b)));
  	
  	sb.append(super.toString());
  	
  	return sb.toString();
  }

  // ---------------------------------------------------------------------------
	@Override
	public List<Double> getXcoordinates(List<Double> samples)
	{
		List<Double> coordinates = new LinkedList<Double>();
		
		double min = 0;
		double max = samples.get(0);
		for (Iterator<Double> it = samples.iterator(); it.hasNext(); )
		{
			double sample = it.next();
			if (sample > max) max = sample;
		}
		max = Math.ceil(max);
		
		double x_scale = IDistribution.X_SCALE;
		double interval = 1.0 / x_scale ;
		int steps = (int) ((max - min) / interval);
		
		for (int i=0; i<=steps; i++)
		{
			double x_posi = min + interval * i; 
			x_posi = Math.round(x_posi * x_scale) / x_scale;
			coordinates.add(x_posi);
		}

		return coordinates;
	}

  // ---------------------------------------------------------------------------
	@Override
	public Map<Double, Double> densitys(Map<Double, Double> probs)
	{
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
		
		double x_scale = IDistribution.X_SCALE;
		
		for (Iterator<Double> it = probs.keySet().iterator(); it.hasNext(); )
		{
			double x = it.next(); 
			double value = probs.get(x);
		  double density = value * x_scale;
			map.put(x, density);
		}
		
		// Exponential 分佈在X坐標軸為0時有最高點
		double x = 0;
		double a = this._Location;
		double b = this._Scale;
		double density = ExponentialDist.density(x, a, b);
		map.put(x, density);
		
		return map;
	}
}
