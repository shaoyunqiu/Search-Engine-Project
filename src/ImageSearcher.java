import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.Weight;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

public class ImageSearcher {
	private IndexReader reader;
	private IndexSearcher searcher;
	private Analyzer analyzer;
	private float avgLength=1.0f;
	private final int maxTermNum = 100;
	private int simchoice ;
	
	public ImageSearcher(String indexdir){
		analyzer = new IKAnalyzer();
		try{
			reader = IndexReader.open(FSDirectory.open(new File(indexdir)));
			searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new SimpleSimilarity());
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> splitQuery(String queryString){
		ArrayList<String> terms = new ArrayList<String>(0) ;
		StringReader reader = new StringReader(queryString) ;
		TokenStream ts = analyzer.tokenStream(" ", reader) ;
		CharTermAttribute termAttribute  = ts.getAttribute(CharTermAttribute.class) ;
		try {
			while(ts.incrementToken()){
				terms.add(termAttribute.toString()) ;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Splitword error");
			e.printStackTrace();
		}
		return terms ;
	}
	
	public ArrayList<Term> splitTerms(ArrayList<String> queryWords, String field) {
		ArrayList<Term> termlist = new ArrayList<Term>(0) ;
		for (int i = 0; i < queryWords.size(); i++) {
			termlist.add(new Term(field, queryWords.get(i))) ;
		}
		return termlist ;
	}
	
	public TopDocs searchQuery(String queryString, String field, int choice,int maxnum){
		try {
			simchoice = choice ;
			if(simchoice == 0){
				searcher.setSimilarity(new SimpleSimilarity());
			}
			else {
				searcher.setSimilarity(new DefaultSimilarity());
			}
			//Term term=new Term(field,queryString);
			// get split word query
			//ArrayList<String> querywords = splitQuery(queryString) ;
			//ArrayList<Term> termlist = splitTerms(querywords, field) ; // termlist word use later
			
			/* split query into terms */
			/*Term[] terms = new Term[maxTermNum];
			int termNum = 0;
	        System.out.print("Term: ");
			for(int i = 0; i < termlist.size(); i ++) {
				System.out.print(querywords.get(i) + "  ");
				terms[i] = termlist.get(i);
				termNum ++;
			}
			if(termNum == 0) {
	        	if(queryString != null) {
		        	termNum = 1;
		        	terms[0] = new Term(field, queryString);
		        	System.out.print(queryString + " ");
	        	}
	        	else {
	        		return null;
	        	}
	        }
	        System.out.println();
	        System.out.println("Term Num: " + termNum);*/
			
	        
			//Query query=new SimpleQuery(term,avgLength);
			String[] fields = { "title", "keywords", "h1", "h2", "h3", "h4", "h5", "h6", "content", "hreftext"};
			Map<String, Float> boosts = new HashMap<String, Float>() ;
			boosts.put("title",10.0f) ;
			boosts.put("keywords", 1.0f/2) ;
			boosts.put("h1", 1.0f/3) ;
			boosts.put("h2", 1.0f/4) ;
			boosts.put("h3", 1.0f/5) ;
			boosts.put("h4", 1.0f/6) ;
			boosts.put("h5", 1.0f/7) ;
			boosts.put("h6", 1.0f/8) ;
			boosts.put("hreftext", 1.0f/8) ;
			boosts.put("content", 5.0f) ;
//			String[] fields = {"title", "content"} ;
//			Map<String, Float> boosts= new HashMap<String, Float>() ;
//			boosts.put("title", 10.0f) ;
//			boosts.put("content", 1.0f) ;
			
			/*boosts.put("title",10.0f) ;
			boosts.put("keywords", 1.0f) ;
			boosts.put("h1", 0.0f) ;
			boosts.put("h2", 0.0f) ;
			boosts.put("h3", 0.0f) ;
			boosts.put("h4", 0.0f) ;
			boosts.put("h5", 0.0f) ;
			boosts.put("h6", 0.0f) ;
			boosts.put("hreftext", 0.0f) ;
			boosts.put("content", 5.0f) ;*/
			MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_35, fields, analyzer, boosts) ;
			parser.setDefaultOperator(Operator.OR);
			Query query = parser.parse(queryString) ;
			System.out.println("QueryParser: " + query.toString());
			//Query query = new MultiFieldQueryParser(Version.LUCENE_35, fields, analyzer).parse(queryString);  
	        //Query query = new MultiQuery(terms, avgLength, termNum, avgLength);
			query.setBoost(1.0f);
			//Weight w=searcher.createNormalizedWeight(query);
			//System.out.println(w.getClass());
			TopDocs results = searcher.search(query, maxnum);
			System.out.println(results);
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Document getDoc(int docID){
		try{
			return searcher.doc(docID);
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void loadGlobals(String filename){
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line=reader.readLine();
			avgLength=Float.parseFloat(line);
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public float getAvg(){
		return avgLength;
	}
	
	public static void main(String[] args){
		ImageSearcher search=new ImageSearcher("forIndex/index");
		search.loadGlobals("forIndex/global.txt");
		System.out.println("avg length = "+search.getAvg());
		
		/*TopDocs results=search.searchQuery("小胖", "abstract", 100);
		ScoreDoc[] hits = results.scoreDocs;
		for (int i = 0; i < hits.length; i++) { // output raw format
			Document doc = search.getDoc(hits[i].doc);
			System.out.println("doc=" + hits[i].doc + " score="
					+ hits[i].score+" picPath= "+doc.get("picPath"));
		}*/
	}
}
