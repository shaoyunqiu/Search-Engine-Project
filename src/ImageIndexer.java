import java.io.*;
import java.util.*;

import org.w3c.dom.*;   
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import javax.xml.parsers.*; 

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.NewBeanInstanceStrategy;

public class ImageIndexer {
	private Analyzer analyzer; 
    private IndexWriter indexWriter;
    private float averageLength=1.0f;
    private int fileNum = 0;
    private Map<Integer, Double> PRMap ;
    
    public ImageIndexer(String indexDir){
    	analyzer = new IKAnalyzer();
    	try{
    		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, analyzer);
    		Directory dir = FSDirectory.open(new File(indexDir));
    		indexWriter = new IndexWriter(dir,iwc);
    		indexWriter.setSimilarity(new SimpleSimilarity());
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
    
    public void getPageRank(String _filename) {
		File file = new File(_filename) ;
		try {
			BufferedReader buffReader = new BufferedReader(new FileReader(file)) ;
			String lineString = null ;
			PRMap = new HashMap<Integer, Double>() ;
			while((lineString = buffReader.readLine()) != null){
				String[] after = lineString.split(",") ;
				if(after.length < 2) continue ;
				else{
					Integer id = Integer.valueOf(after[0]) ;
					Double pr = Double.valueOf(after[1]) ;
					PRMap.put(id, pr) ;
				}
			}
			buffReader.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("get PageRank failed");
		}
		//System.out.println(PRMap.size());
		System.out.println("get PageRank success");
	}
    
    
    public void saveGlobals(String filename){
    	try{
    		PrintWriter pw=new PrintWriter(new File(filename));
    		pw.println(averageLength);
    		pw.close();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
    
    public JSONObject readJson(String filename) {
        File file=new File(filename);
        BufferedReader reader=null;
        String jsonContent="";
        JSONObject jo = null;
        
        try {
            reader=new BufferedReader(new FileReader(file));
            String tempString=null;
            while((tempString=reader.readLine())!=null){
                jsonContent+=tempString;
            }
            jo = JSONObject.fromObject(jsonContent);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        finally {
            if(reader!=null){
	            try {
	                reader.close();
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
            }
        }
        return jo;
    }
	
	/** 
	 * <p>
	 * index sogou.xml 
	 * 
	 */
	public void indexSpecialFile(String filename){
		try{
			if(fileNum % 1000==0){
				System.out.println("process "+fileNum);
			}
			//fileNum++;
			MyJsonDealer jsonDealer = new MyJsonDealer(filename) ;
			String title = "" ;
			String url = "" ;
			int id = -1 ;
			String bodytext = "" ;
			String keywords = "" ;
			String hreftext = "" ;
			Double pr = new Double(0) ;
			String content = "" ;
			ArrayList<String> headtext = new ArrayList<String>();
			if(jsonDealer.ReadFile()){
				jsonDealer.getContent();
				title = jsonDealer.title ;
				id = jsonDealer.id ;
				url = UrlNorm.urlNormalize(jsonDealer.url) ;
			}
			else {
				System.out.println("file is not exist, continue " + filename);
				return ; // some error return
			}
			if(id < 52305){
				filename = filename.replace(".json", ".html") ; //change to html file
				HtmlContent myhtml = new HtmlContent(filename) ;
				if(!(myhtml.createDoc())) return ; // can't open html, return 
				bodytext = myhtml.getBodyText() ; // body text
				headtext = myhtml.getHeads() ; // h1-h6 text
				hreftext = myhtml.getHrefText() ; // all href text
				keywords = myhtml.getKeywords() ; // <meta name="keywords" content= 
				content = myhtml.getContent() ; // article tag or class = content div
				if(PRMap.containsKey(new Integer(id))){
					pr = PRMap.get(new Integer(id)) ; // pagerank
				}
				else {
					System.out.println(id);
				}
			}
			else {
				filename = filename.replace(".json", ".pdf") ;
				bodytext = pdfExtractor.getpdfText(filename) ;
				content = bodytext ;
				hreftext = "" ;
				keywords = "" ;
				headtext.clear();
				for(int i = 0 ; i < 6 ; i ++){
					headtext.add("") ;
				}	
				pr = PRMap.get(new Integer(id)) ;
			}
    		Document document  =   new  Document();
			Field titleField  =   new  Field( "title" ,title,Field.Store.YES, Field.Index.ANALYZED);
			Field urlField  =   new  Field( "url" ,url,Field.Store.YES, Field.Index.NO);
			Field idField = new Field("id", Integer.toString(id), Field.Store.YES, Field.Index.NO) ;
			Field prField = new Field("pagerank", pr.toString(), Field.Store.YES, Field.Index.NO) ;
			Field bodytextField = new Field("bodytext", bodytext, Field.Store.YES, Field.Index.ANALYZED) ;
			Field hrefField = new Field("hreftext", hreftext, Field.Store.YES, Field.Index.ANALYZED) ;
			Field keywordsField = new Field("keywords", keywords, Field.Store.YES, Field.Index.ANALYZED);
			Field contentField = new Field("content", content, Field.Store.YES, Field.Index.ANALYZED) ;
			Field h1Field = new Field("h1", headtext.get(0), Field.Store.YES, Field.Index.ANALYZED) ;
			Field h2Field = new Field("h2", headtext.get(1), Field.Store.YES, Field.Index.ANALYZED) ;
			Field h3Field = new Field("h3", headtext.get(2), Field.Store.YES, Field.Index.ANALYZED) ;
			Field h4Field = new Field("h4", headtext.get(3), Field.Store.YES, Field.Index.ANALYZED) ;
			Field h5Field = new Field("h5", headtext.get(4), Field.Store.YES, Field.Index.ANALYZED) ;
			Field h6Field = new Field("h6", headtext.get(5), Field.Store.YES, Field.Index.ANALYZED) ;
			fileNum ++ ;
			averageLength += title.length(); // need some change
			document.add(titleField);
			document.add(urlField);
			document.add(idField);
			document.add(prField);
			document.add(bodytextField);
			document.add(hrefField);
			document.add(keywordsField);
			document.add(contentField);
			document.add(h1Field);
			document.add(h2Field);
			document.add(h3Field);
			document.add(h4Field);
			document.add(h5Field);
			document.add(h6Field);
			indexWriter.addDocument(document);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void traverseIndex(String path1, String path2) {
    	try {
	        File rootDirhtml = new File(path1);
	        if (rootDirhtml.exists()) {
	            File[] fileshtml = rootDirhtml.listFiles();
	            for (File file : fileshtml) {
	            	if (file.getName().indexOf(".json") > -1) {
	            		String filename = file.getAbsolutePath();
	            		indexSpecialFile(filename);
	            	}
	            }
	        } else {
	            System.out.println("Root dir do not exist!-->" + path1);
	        }
	        File rootDirpdf = new File(path2) ;
	        if(rootDirpdf.exists()){
	        	File[] filespdf = rootDirpdf.listFiles() ;
	        	for(File file:filespdf){
	        		if(file.getName().indexOf(".json") > -1){
	        			String filename = file.getAbsolutePath() ;
	        			indexSpecialFile(filename);
	        		}
	        	}
	        }else {
				System.out.println("Root dir do not exist!-->" + path2);
			}
	        averageLength /= indexWriter.numDocs();
			System.out.println("average length = "+averageLength);
			System.out.println("total "+indexWriter.numDocs()+" documents");
			indexWriter.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
	
	public static void main(String[] args) {
		ImageIndexer indexer=new ImageIndexer("forIndex/index");
		indexer.getPageRank("../tmpres/PR_res.txt");
		
		String path1="../data/html/";
		String path2 = "../data/pdf/" ;
        indexer.traverseIndex(path1, path2);
        indexer.saveGlobals("forIndex/global.txt");
        
	}
}
