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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import javax.xml.parsers.*; 

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ImageIndexer {
	private Analyzer analyzer; 
    private IndexWriter indexWriter;
    private float averageLength=1.0f;
    private int fileNum = 0;
    
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
			if(fileNum % 10000==0){
				System.out.println("process "+fileNum);
			}
			fileNum++;
			
			JSONObject jo = readJson(filename);
    		String title = jo.getString("title");
    		String id = jo.getString("id");
    		String url = UrlNorm.urlNormalize(jo.getString("url"));
    		String content = title;
    		for(int i = 0; i < 10; i ++) {
    			content += ('，' + title);
    		}
    		Document document  =   new  Document();
			Field titleField  =   new  Field( "title" ,title,Field.Store.YES, Field.Index.ANALYZED);
			Field urlField  =   new  Field( "url" ,url,Field.Store.YES, Field.Index.NO);
			Field idField = new Field("id" ,id,Field.Store.YES, Field.Index.NO);
			Field contentField = new Field("content", content,Field.Store.YES, Field.Index.ANALYZED);
			
			averageLength += title.length();
			document.add(titleField);
			document.add(urlField);
			document.add(idField);
			document.add(contentField);
			indexWriter.addDocument(document);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void traverseIndex(String path) {
    	try {
	        File rootDir = new File(path);
	        if (rootDir.exists()) {
	            File[] files = rootDir.listFiles();
	            for (File file : files) {
	            	if (file.getName().indexOf(".json") > -1) {
	            		String filename = file.getAbsolutePath();
	            		indexSpecialFile(filename);
	            	}
	            }
	            averageLength /= indexWriter.numDocs();
				System.out.println("average length = "+averageLength);
				System.out.println("total "+indexWriter.numDocs()+" documents");
				indexWriter.close();
	        } else {
	            System.out.println("Root dir do not exist!");
	        }
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
	
	public static void main(String[] args) {
		ImageIndexer indexer=new ImageIndexer("forIndex/index");
		
		String path="../data/html/";
        indexer.traverseIndex(path);
        indexer.saveGlobals("forIndex/global.txt");
        
	}
}
