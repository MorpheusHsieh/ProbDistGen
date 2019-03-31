package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

abstract public class DistributionImpl implements IDistribution
{
	protected Random _RAND = null;
	
	//private boolean isDebug = true;
	
  // ***************************************************************************
  // * Constructor
	// ***************************************************************************
	public DistributionImpl()
	{
		this._RAND = new Random(System.currentTimeMillis()); 
	}
	
  // ***************************************************************************
  // * Override methods
	// ***************************************************************************
	@Override
	public List<Double> getSamples(int num)
	{
		List<Double> rands = new LinkedList<Double>();
		for (int i=0; i<num; i++) rands.add(this.rand());
		return rands;
	}

	// ---------------------------------------------------------------------------
	@Override
	public double mean(List<Double> samples)
	{
		double mean = 0.0; 
		
		for (Iterator<Double> it = samples.iterator(); it.hasNext(); )
			mean += it.next();
		mean /= samples.size();
		
		return mean;
	}

	// ---------------------------------------------------------------------------
	@Override
	public double variance(List<Double> samples)
	{
		double variance = 0.0;

		double mean = this.mean(samples); 
		for (Iterator<Double> it = samples.iterator(); it.hasNext(); )
		{
			double sigma = it.next() - mean;
			variance += sigma * sigma;
		}
		variance /= (samples.size() - 1);
		
		return variance;
	}

	// ---------------------------------------------------------------------------
	@Override
	/**
	 * 將X軸等分為 scale 個間隔, 計算每一個間隔中出現的隨機亂數個數比例
	 */
	public Map<Double, Double> probabilitys(List<Double> samples)
	{
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();

		// 建立 X 軸座標
		List<Double> coordinates = this.getXcoordinates(samples);
		for (Double posi : coordinates) map.put(posi, 0.0);

		// 計算各個區間中有多少個隨機亂數
		for (Iterator<Double> it = samples.iterator(); it.hasNext(); )
		{
			double sample = it.next();

			Iterator<Double> positions  = coordinates.iterator();
			
			double locate = 0;
			while (positions.hasNext())
			{
				locate = positions.next();
				if (sample <= locate) break;
			}
			
			double value = map.get(locate);
			map.put(locate, value + 1);
		}

		// 將各個間隔中的隨機亂數個數轉換成百分比，即為 P(x) = { X=x } 的PDF
		double size = samples.size();
		for (Iterator<Double> it = map.keySet().iterator(); it.hasNext(); )
		{
			double key = it.next();
			double prob = map.get(key) / size;
  		map.put(key, prob);
		}
		
		return map;
	}

	// ---------------------------------------------------------------------------
	@Override
	public Map<Double, Double> distributions(Map<Double, Double> probs)
	{
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();
		
		double cdf = 0.0;
		for (Iterator<Double> it = probs.keySet().iterator(); it.hasNext(); )
		{
			double key = it.next();
			double value = probs.get(key);
  		cdf += value;
			map.put(key, cdf);
		}
		
		return map;
	}

	
	// ---------------------------------------------------------------------------
  public void toExcel(List<Double> samples)
  {
  	// 將隨機亂數換成機率密度函數
  	Map<Double, Double> probs = this.probabilitys(samples);
  	Map<Double, Double> pdfs = this.densitys(probs); 
  	Map<Double, Double> cdfs = this.distributions(probs); 
  	ArrayList<Double> keyArray = new ArrayList<Double>(pdfs.keySet());
  	
  	/*
  	 * 2019.03.18 Added start: Create a sub directory to output excel files
  	 */
    String currentDir = System.getProperty("user.dir");
    String outputDir = currentDir + "\\output";
    // System.out.println(outputDir);

    boolean isExist = new File(outputDir).exists();
  	if (!isExist) {
      String formatted = "\r\nOutput dir (%s) not exist, creating...";
      String outstr = String.format(formatted, outputDir);
      System.out.println(outstr);

      boolean success = new File(outputDir).mkdirs();
      if (!success) {
        formatted = "\r\nOutput dir (%s) make failure...";
        outstr = String.format(formatted, outputDir);
        System.out.println(outstr);
        System.exit(-1); 
      } else {
        formatted = "\r\nCreate dir (%s) success...";
        outstr = String.format(formatted, outputDir);
        System.out.println(outstr);
      }
  	}
  	// 2019.03.28 Added end
  	
  	String filename = outputDir + "\\" + this.type().toString() + ".xls";
  	String fieldnames[] = { "Steps", "PDF", "CDF" };

	int cols = fieldnames.length;
	int rows =keyArray.size();

	//如果 excel 檔案不存在則建立新檔
	File excel = new File(filename);
	if (!excel.exists()) this.createNewExcel(filename, rows, cols);

	// 讀取 excel 檔案
	Workbook wb = null;
	try	{
		FileInputStream inStream = new FileInputStream(filename);
		wb = new HSSFWorkbook(inStream);
		inStream.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}	catch (IOException e) {
		e.printStackTrace();
	}
	
	// 從 Excel 中讀取第 0頁, 並將第 1, 2行清除
	Sheet sheet = wb.getSheetAt(0);
	
	int colToRemove[] = { 1, 2 };
	Iterator<Row> rowIter = sheet.iterator();
	while (rowIter.hasNext()) 
	{
	   Row row = rowIter.next();
	   if (row == null) continue;
	   for (int i=0; i<colToRemove.length; i++)
	   {
		   Cell cell = row.getCell(colToRemove[i]);
		   if (cell != null) row.removeCell(cell);
	   }
	}

	// 寫入新的資料
	Font font = wb.createFont();		
	font.setFontName("Times New Roman");    // 新羅馬
	font.setFontHeightInPoints((short) 12); // 12號字 

	// 設定 cell  樣式
	CellStyle cs = wb.createCellStyle();
  cs.setFont(font);                                       // 設定字體
  cs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直置中
	
	for (int rownum = 0; rownum <= rows; rownum++)
	{
		Row row = sheet.getRow(rownum);
		if (row == null) row = sheet.createRow(rownum);
		
		Cell cell = null;

		if (rownum == 0)
		{
			for (int colnum = 0; colnum < cols; colnum++)
			{
				cell = row.getCell(colnum);
				if (cell == null) cell = row.createCell(colnum);

			  cs.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 標題列水平置中,
        cell.setCellStyle(cs);
				cell.setCellValue(fieldnames[colnum]);
			}
			continue;
		}

		double key = keyArray.get(rownum - 1);
		double pdf = pdfs.get(key);
		double cdf = cdfs.get(key);

		cs.setAlignment(HSSFCellStyle.ALIGN_RIGHT);  // 標題列水平置右

		cell = row.getCell(0);
		if (cell == null) cell = row.createCell(0);
    cell.setCellStyle(cs);
		cell.setCellValue(key);

		cell = row.getCell(1);
		if (cell == null) cell = row.createCell(1);
    cell.setCellStyle(cs);
		cell.setCellValue(pdf);

		cell = row.getCell(2);
		if (cell == null) cell = row.createCell(2);
    cell.setCellStyle(cs);
		cell.setCellValue(cdf);
	}
	
	// 將 workbook 寫入 xls 檔案
	try	
	{
		FileOutputStream outStream = new FileOutputStream(filename);
		wb.write(outStream);
		outStream.close();
	} 
	catch (FileNotFoundException e)	{
		e.printStackTrace();
	} catch (IOException e) {
	  e.printStackTrace();
	}
  	
  }
  
  // ---------------------------------------------------------------------------
  private void createNewExcel(String filename, int rows, int cols)
  {
		HSSFWorkbook wb =  new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		
		Row row = sheet.createRow(0);
		row.createCell(0);
		
		try	
		{
			FileOutputStream outStream = new FileOutputStream(filename);
			wb.write(outStream);
			outStream.close();
		} 
		catch (FileNotFoundException e)	{
			e.printStackTrace();
		} catch (IOException e) {
		  e.printStackTrace();
		}
  }

	// ---------------------------------------------------------------------------
  @Override
  public String toString()
  {
  	StringBuffer sb = new StringBuffer();
  	String format = null;
  	
  	format = "\r\n\r\nExperiment: ";
  	sb.append(String.format(format));
  	
  	int num = IDistribution.EXPERIMENT_COUNT;
  	List<Double> rands = this.getSamples(num);
  			
  	format = "\r\n  Samples: %d";
  	sb.append(String.format(format, num));

  	double mean = this.mean(rands);
  	format = "\r\n  Mean: %f";
  	sb.append(String.format(format, mean));
  	
  	double variance = this.variance(rands);
  	format = "\r\n  Variance: %f";
  	sb.append(String.format(format, variance));
  	
  	return sb.toString();
  }

}
