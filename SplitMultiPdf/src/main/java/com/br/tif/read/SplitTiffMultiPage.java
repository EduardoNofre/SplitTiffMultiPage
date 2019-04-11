package com.br.tif.read;
import java.awt.AWTException;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.TIFFEncodeParam;

public class SplitTiffMultiPage {

	File fileDiretorio = new File("C:\\Users\\eduardo.sa\\Desktop\\TesteEdu\\"); 

	public void  splitTifMulti() throws IOException, AWTException 
	{       

		File[] listaFiles = fileDiretorio.listFiles();
		
		String dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

		try{
			if (listaFiles.length > 0)
			{
				for(int countFile=0; countFile<listaFiles.length; countFile++)
				{
					System.out.println(listaFiles[countFile]);
					
					FileSeekableStream ss = new FileSeekableStream(listaFiles[countFile]);
					
					ImageDecoder dec = ImageCodec.createImageDecoder("tiff", ss, null);
					
					int count = dec.getNumPages();
					
					TIFFEncodeParam param = new TIFFEncodeParam();
					
					param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
					
					param.setLittleEndian(false);
					
					System.out.println("This TIF has " + count + " image(s)");

					for (int i = 0; i < count; i++) 
					{
						RenderedImage page = dec.decodeAsRenderedImage(i);
						
						File fileExist = new File("C:\\Users\\eduardo.sa\\Desktop\\TesteEdu\\"+dateFormat+ countFile +i + ".tif");
						
						System.out.println("Saving " + fileExist.getCanonicalPath());
						
						ParameterBlock pb = new ParameterBlock();
						
						pb.addSource(page);
						
						pb.add(fileExist.toString());
						
						pb.add("tiff");
						
						pb.add(param);
						
						RenderedOp r = JAI.create("filestore",pb);
						
						r.dispose();
					}
				}
				WriteListOFFilesIntoExcel();
			}  

			else
			{
				System.out.println("Arquivo Não encontrado");
			}
		} 

		catch(Exception ex)

		{
			System.out.println("Error: "+ex);
		}
	}

	public void WriteListOFFilesIntoExcel(){

		File[] listaFiles = fileDiretorio.listFiles();
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(fileDiretorio.listFiles()));


		try {
			String filenameXls = "C:\\Users\\eduardo.sa\\Desktop\\TesteEdu\\listasFIles.xls" ;
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			
			HSSFSheet sheet = workbook.createSheet("FirstSheet"); 

			for (int file=0; file<listaFiles.length; file++) {
				
				System.out.println(listaFiles[file]);
				
				Row r = sheet.createRow(file);
				
				r.createCell(0).setCellValue(files.get(file).toString());
			}

			FileOutputStream fileOut = new FileOutputStream(filenameXls);
			
			workbook.write(fileOut);
			
			fileOut.close();
			
			System.out.println("Arquivo gerado com sucesso");

		}
		catch(Exception ex){

			System.out.println("Error: "+ex);   
		}
	}
	public static void main(String[] args) throws IOException, AWTException {
		
		new SplitTiffMultiPage().splitTifMulti();

	}
}