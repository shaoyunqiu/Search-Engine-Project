import java.io.File;
import java.io.FileInputStream;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.omg.CORBA.INTERNAL;


public class pdfExtractor {
	static public String getpdfText(String filename){
		String content = "" ;
		try {
			FileInputStream fis = new FileInputStream(filename) ;
			PDFParser p = new PDFParser(fis) ;
			p.parse();
			PDFTextStripper ts = new PDFTextStripper() ;
			content = ts.getText(p.getDocument()) ;
			fis.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("pdfExtractor exception : " + filename);
		}		
		return content ;
	}
}
