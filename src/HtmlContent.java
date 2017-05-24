import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.CORBA.PUBLIC_MEMBER;

public class HtmlContent {
	public static String getContent(String filename) {
		String content = "" ;
		try {
			File filein = new File(filename) ;
			if(filein == null) return content ;
			org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(filein, "GBK","http://example.com/") ;
			String title = doc.title() ;
			content += title ;
			if(doc.body() != null) content += doc.body().text() ;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("open file fail " + filename) ;
		}
		//System.out.println(content);
		return content ;
	}
	
	public static ArrayList<String> getLinksAll(File file) {
		ArrayList<String> allurls = new ArrayList<String>() ;
		try {
			if(file == null){
				System.out.println("can't open the file" + file.getAbsolutePath());
				return allurls ;
			}
			org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(file, "GBK", "http://example.com/") ;
			Element body = doc.body() ;
			Elements es = body.select("a") ;
			for(Iterator<Element> it = es.iterator() ; it.hasNext() ;){
				Element e = (Element)it.next() ;
				String url = e.attr("href") ;
				url = UrlNorm.urlNormalize(url) ;
				allurls.add(url) ;
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("get url array fail");
		}
		return allurls ;
	}
}
