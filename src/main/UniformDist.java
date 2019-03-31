package main;



import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UniformDist extends DistributionImpl
{
  private double _Xmin = 0;
  private double _Xmax = 0;
  
  // ***************************************************************************
  // * Constructor
  // ***************************************************************************
  public UniformDist() { this(0, 1.0); }

  public UniformDist(double xMin, double xMax) 
  {
  	this.setParameters(xMin, xMax);
  }
	
  // ***************************************************************************
  // * Public methods
  // ***************************************************************************
  public void setParameters(double xMin, double xMax) 
  { 
  	this._Xmin = xMin;
  	this._Xmax = xMax;
  }
  
  // ***************************************************************************
  // * Static methods
  // ***************************************************************************
	public static double density(double x, double xMin, double xMax)
	{
		double density = 0;
		
		density = ((x < xMin) || (x >= xMax)) ? 0 : (1.0 / (xMax - xMin)); 
		
		return density;
	}

	// ---------------------------------------------------------------------------
	public static double distribution(double x, double xMin, double xMax)
	{
		double dist = 0;
		
		dist = (x < xMin) ? 0 : (x > xMax) ? 1 : ((x - xMin) / (xMax - xMin));

		return dist;
	}
	
	// ---------------------------------------------------------------------------
	public static double median(double xMin, double xMax)
	{
	  return ((xMin + xMax) / 2.0);
	}

	// ---------------------------------------------------------------------------
	public static double mean(double xMin, double xMax)
	{
		return ((xMin + xMax) / 2.0);
	}
	
	// ---------------------------------------------------------------------------
	public static double variance(double xMin, double xMax)
	{
		return (Math.pow((xMax - xMin), 2.0) / 12.0);
	}

  // ***************************************************************************
  // * Override methods
  // ***************************************************************************
  @Override
  public DistributionType type() { return DistributionType.Uniform; }

  // ---------------------------------------------------------------------------
  @Override
  public double rand()
  {
  	double rand = 0;
  	
    double a = this._Xmin;
    double b = this._Xmax;
  	assert(a < b);

  	int stream = 1+ this._RAND.nextInt(LCGRand.MAX_STREAMS);
  	rand = a + (b - a) * LCGRand.lcgrand(stream);
  	
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

  	double a = this._Xmin;
  	format = "\r\n  Xmin: %f";
  	sb.append(String.format(format, a));
  	
  	double b = this._Xmax;
  	format = "\r\n  Xmax: %f";
  	sb.append(String.format(format, b));
  	
  	format = "\r\n  Median: %f";
  	sb.append(String.format(format, UniformDist.median(a, b)));

  	format = "\r\n  Mean: %f";
  	sb.append(String.format(format, UniformDist.mean(a, b)));
  	
  	format = "\r\n  Variance: %f";
  	sb.append(String.format(format, UniformDist.variance(a, b)));
  	
  	sb.append(super.toString());
  	
  	return sb.toString();
  }

  // ---------------------------------------------------------------------------
	@Override
	public List<Double> getXcoordinates(List<Double> samples)
	{
		List<Double> coordinates = new LinkedList<Double>();
		
		int size = samples.size();
		
		Collections.sort(samples);
		double min = Math.floor(samples.get(0));
		double max = Math.ceil(samples.get(size-1));
		
		// Uniform 分佈的座標點從最小值-1 到最大值+1
		min -= 1;
		max += 1;
		
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
		
		double scale = IDistribution.X_SCALE;
		for (Iterator<Double> it = probs.keySet().iterator(); it.hasNext(); )
		{
			double key = it.next(); 
			double value = probs.get(key);
			double density = value * scale;
			map.put(key, density);
		}
		
		// 在X坐標軸為 A 時的機率密度
		double x = this._Xmin; 
		double density = UniformDist.density(x, this._Xmin, this._Xmax);
		map.put(x, density);
		
		return map;
	}
	
}
