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
	 * �NX�b������ scale �Ӷ��j, �p��C�@�Ӷ��j���X�{���H���üƭӼƤ��
	 */
	public Map<Double, Double> probabilitys(List<Double> samples)
	{
		TreeMap<Double, Double> map = new TreeMap<Double, Double>();

		// �إ� X �b�y��
		List<Double> coordinates = this.getXcoordinates(samples);
		for (Double posi : coordinates) map.put(posi, 0.0);

		// �p��U�Ӱ϶������h�֭��H���ü�
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

		// �N�U�Ӷ��j�����H���üƭӼ��ഫ���ʤ���A�Y�� P(x) = { X=x } ��PDF
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
  	// �N�H���üƴ������v�K�ר��
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

	//�p�G excel �ɮפ��s�b�h�إ߷s��
	File excel = new File(filename);
	if (!excel.exists()) this.createNewExcel(filename, rows, cols);

	// Ū�� excel �ɮ�
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
	
	// �q Excel ��Ū���� 0��, �ñN�� 1, 2��M��
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

	// �g�J�s�����
	Font font = wb.createFont();		
	font.setFontName("Times New Roman");    // �sù��
	font.setFontHeightInPoints((short) 12); // 12���r 

	// �]�w cell  �˦�
	CellStyle cs = wb.createCellStyle();
  cs.setFont(font);                                       // �]�w�r��
  cs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // �����m��
	
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

			  cs.setAlignment(HSSFCellStyle.ALIGN_CENTER); // ���D�C�����m��,
        cell.setCellStyle(cs);
				cell.setCellValue(fieldnames[colnum]);
			}
			continue;
		}

		double key = keyArray.get(rownum - 1);
		double pdf = pdfs.get(key);
		double cdf = cdfs.get(key);

		cs.setAlignment(HSSFCellStyle.ALIGN_RIGHT);  // ���D�C�����m�k

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
	
	// �N workbook �g�J xls �ɮ�
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
