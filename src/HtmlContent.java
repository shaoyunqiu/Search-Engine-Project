import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.CORBA.PUBLIC_MEMBER;

public class HtmlContent {
	
	String filename ;
	org.jsoup.nodes.Document doc ;
	
	public HtmlContent(String _filename) {
		// TODO Auto-generated constructor stub
		filename = _filename ;
	}
	
	public boolean createDoc() {
		try {
			File file = new File(filename) ;
			if(!file.exists()) {
				System.out.println("can't open the file: " + filename);
				return false ;
			}
			doc = org.jsoup.Jsoup.parse(file, "UTF-8", "http://example.com/") ;
			return true ;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("can't open the file: " + filename);
			return false ;
		}
	}
	
	public String getBodyText() {
		String bodyString = "" ;
		try {
			if(doc.body() != null) bodyString = doc.body().text() ;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("can't get the body text: " + filename);
		}
		return bodyString ;
	}
	
	public ArrayList<String> getLinksAll(){
		ArrayList<String> allurls = new ArrayList<String>() ;
		Element body = doc.body() ;
		Elements es = body.select("a") ;
		for(Iterator<Element> it = es.iterator() ; it.hasNext() ;){
			Element e = (Element)it.next() ;
			String url = e.attr("href") ;
			url = UrlNorm.urlNormalize(url) ;
			allurls.add(url) ;
		}
		return allurls ;
	}
	
	public ArrayList<String> getHeads() {
		ArrayList<String> headStrings = new ArrayList<String>() ;
		for(int i = 1 ; i < 7 ; i ++){
			String nowtag = "h"+Integer.toString(i) ;
			String tmp = "" ;
			Element body = doc.body() ;
			Elements heads = body.getElementsByTag(nowtag) ;
			if(heads.size() == 0){
				headStrings.add("");
				continue ;
			}
			for(Element e: heads){
				tmp += e.text() ;
				tmp += "\t" ; // split by tag
			}
			headStrings.add(tmp) ;
		}
		return headStrings ;
	}
	
	public String getHrefText() {
		String herfString = "" ;
		Element body = doc.body() ;
		Elements es = body.select("a");
		for(Iterator<Element> it = es.iterator(); it.hasNext() ; ){
			Element e = (Element)it.next() ;
			herfString += e.text() ;
			herfString += "\t" ; // split by tab
		}
		return herfString ;
	}
	
	public String getContent() { // get text in article tag first, if not get text in class content div
		String content = "" ;
		Elements es_article = doc.select("article") ;
		if(es_article.size() > 0){
			for(Element e:es_article){
				content += e.text() ;
				content += "\t" ;
			}
		}
		else {
			Elements es_content = doc.select("div.content") ;
			if(es_content.size() > 0){
				for(Element e:es_content){
					content += e.text() ;
					content += "\t" ;
				}
			}
		}
		return content ;
	}
	
	
	public String getKeywords() {
		String keywords = "" ;
		Elements es = doc.select("meta[name=keywords]") ;
		if(es.size() > 0){
			for(Element e:es){
				keywords += e.attr("content") ;
				keywords += "\t" ;
			}
		}
		return keywords ;
	}
	
	/*public static void main(String[] argv) {
		HtmlContent test = new HtmlContent("../data/html/10.html") ;
		if(test.createDoc()){
			String key = test.getKeywords() ;
			String content = test.getContent() ;
			System.out.println("key is: " + key);
			System.out.println("content is " + content);
		}
		else {
			System.out.println("can't open html");
		}		
	}*/
}
