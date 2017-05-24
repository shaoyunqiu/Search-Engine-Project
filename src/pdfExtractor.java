import java.io.File;
import java.io.FileInputStream;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.omg.CORBA.INTERNAL;


public class pdfExtractor {
	public static String getpdfText(String filename){
		String content = "" ;
		try {
			FileInputStream fis = new FileInputStream(new File(filename)) ;
			PDFParser p = new PDFParser(fis) ;
			p.parse();
			PDFTextStripper ts = new PDFTextStripper() ;
			PDDocument document = p.getPDDocument() ;
			content = ts.getText(document) ;
			fis.close();
			document.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("pdfExtractor exception : " + filename);
		}		
		return content ;
	}
	
	/*public static void main(String[] argv) {
		String filename = "../data/pdf/0.pdf" ;
		String text = pdfExtractor.getpdfText(filename) ;
		System.out.println(text);
	}*/
}
