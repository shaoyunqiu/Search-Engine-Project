import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.util.*;

import java.math.*;
import java.net.*;
import java.io.*;


public class ImageServer extends HttpServlet{
	public static final int PAGE_RESULT=10;
	public static final int MAX_SIM_NUM=9;
	public static final String indexDir="forIndex";
	public static final String picDir="";
	private ImageSearcher search=null;
	private VerticalIndexs viIndex=null;
	private Trie simSearcher=null;
	public ImageServer(){
		super();
		search=new ImageSearcher(new String(indexDir+"/index"));
		search.loadGlobals(new String(indexDir+"/global.txt"));
		viIndex = new VerticalIndexs(indexDir+"/verticalIndex");
		simSearcher = new Trie(indexDir+"/titles/alltitles.txt");
	}
	
	public ScoreDoc[] showList(ScoreDoc[] results,int page){
		if(results==null || results.length<(page-1)*PAGE_RESULT){
			return null;
		}
		int start=Math.max((page-1)*PAGE_RESULT, 0);
		int docnum=Math.min(results.length-start,PAGE_RESULT);
		ScoreDoc[] ret=new ScoreDoc[docnum];
		for(int i=0;i<docnum;i++){
			ret[i]=results[start+i];
		}
		return ret;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");  
		request.setCharacterEncoding("utf-8");
		String queryString=request.getParameter("query");
		String pageString=request.getParameter("page");
		String operationString=request.getParameter("operation");
		String replaceString=request.getParameter("replaceNo");
		String similarityString=(request.getParameter("similarity") == null ? "default" : request.getParameter("similarity"));
		String imgUrlString=request.getParameter("imgurl");
		int similarityChoice = (similarityString.indexOf("simple") >= 0 ? 0 : 1);
		// replace a news item
		if (operationString != null && operationString.indexOf("replace") > -1 && replaceString != null) {
			int replaceNo=Integer.parseInt(replaceString);
			int page=Integer.parseInt(pageString);
			TopDocs results=search.searchQuery(queryString, "title",similarityChoice, 100);
			if (results != null) {
				if(results.scoreDocs.length >= page * PAGE_RESULT + replaceNo) {
					ArrayList<String> queryWords = search.splitQuery(queryString);
					ScoreDoc newDoc = results.scoreDocs[page*PAGE_RESULT + replaceNo];
					Document doc = search.getDoc(newDoc.doc);
					String title = doc.get("title");
					String url = doc.get("url");
					String content = doc.get("content");
					System.out.println("doc=" + newDoc.doc + " score="
							+ newDoc.score + " url= "
							+ doc.get("url")+ " title= "+doc.get("title"));
					response.setContentType("text/xml; charset=UTF-8");  
				    response.setHeader("Cache-Control","no-cache");  
				    PrintWriter out = response.getWriter();
				    out.println("<response>");  
				    for (int i = 0; i < queryWords.size(); i ++) {
				    	out.println("<queryWords>" + queryWords.get(i) + "</queryWords>");
				    }
				    out.println("<title>" + title + "</title>");  
				    out.println("<url>" + url + "</url>");  
				    out.println("<content>" + content + "</content>"); 
				    out.println("</response>");  
				    out.close(); 
				}
			}
		}
		// request similar query 
		else if (operationString != null && operationString.indexOf("simQuery") > -1) {
			// System.out.println("SimQuery " + queryString);
			ArrayList<String> simQuery = simSearcher.Search(queryString, MAX_SIM_NUM);
//			for(int i = 0; i < simQuery.size(); i ++)
//				System.out.print(simQuery.get(i) + " ");
//			System.out.println("");
			// TODO: add similar query to this list
			// simQuery.add(queryString);
			// simQuery.add(queryString + "返回数据1");  
			// simQuery.add(queryString + "返回数据2");  
			// simQuery.add(queryString + "不是有的人天生吃素的");  
			// simQuery.add(queryString + "不是有的人天生吃素的");  
			// simQuery.add(queryString + "2012是真的");  
			// simQuery.add(queryString + "2012是假的"); 
			
			response.setContentType("text/xml; charset=UTF-8");  
		    response.setHeader("Cache-Control","no-cache");  
		    PrintWriter out = response.getWriter();
		    out.println("<response>");  
		    for (int i = 0; i < simQuery.size(); i ++) {
		    	out.println("<simQuery>" + simQuery.get(i) + "</simQuery>");
		    }
		    out.println("</response>");  
		    out.close(); 
		}
		// request search results
		else {
			int page=1;
			if(pageString!=null){
				page=Integer.parseInt(pageString);
			}
			if(queryString==null){
				System.out.println("null query");
				//request.getRequestDispatcher("/Image.jsp").forward(request, response);
			}else{
				System.out.println(queryString);
				System.out.println(URLDecoder.decode(queryString,"utf-8"));
				System.out.println(URLDecoder.decode(queryString,"gb2312"));
				System.out.println("Similarity: " + similarityChoice);
				String[] titles=null;
				String[] urls=null;
				String[] contents=null;
				String[] imgUrls=null;
				String[][] extendTitles=null;
				String[][] extendUrls=null;
				String[][] extendContents=null;
				boolean[] hasExtend=null;
				ArrayList<String> simQuery = simSearcher.Search(queryString, MAX_SIM_NUM);
				// TODO: add similar query to this list
				// simQuery.add(queryString);
				// simQuery.add(queryString + "返回数据1");  
				// simQuery.add(queryString + "返回数据2");  
				// simQuery.add(queryString + "不是有的人天生吃素的");  
				// simQuery.add(queryString + "不是有的人天生吃素的");  
				// simQuery.add(queryString + "2012是真的");  
				// simQuery.add(queryString + "2012是假的"); 
				TopDocs results=search.searchQuery(queryString, "title", similarityChoice ,100);
				ArrayList<String> queryWords = search.splitQuery(queryString);
				if (results != null) {
					ScoreDoc[] hits = showList(results.scoreDocs, page);
					if (hits != null) {
						titles = new String[hits.length];
						urls = new String[hits.length];
						contents = new String[hits.length];
						imgUrls = new String[hits.length];
						extendTitles = new String[hits.length][4];
						extendUrls = new String[hits.length][4];
						extendContents = new String[hits.length][4];
						hasExtend = new boolean[hits.length];
						for (int i = 0; i < hits.length && i < PAGE_RESULT; i++) {
							Document doc = search.getDoc(hits[i].doc);
							System.out.println("doc=" + Integer.parseInt(doc.get("id")) + " score="
									+ hits[i].score + " url= "
									+ doc.get("url")+ " imgUrl= "+(doc.get("imgurl").length())+" title= "+doc.get("title"));
							titles[i] = doc.get("title");
							urls[i] = doc.get("url");
							contents[i] = doc.get("content");
							imgUrls[i]= doc.get("imgurl"); 
							hasExtend[i] = false;
							if(viIndex.hasVerticla(Integer.parseInt(doc.get("id")))) {
								hasExtend[i] = true;
								String[][] v = viIndex.getVertical(Integer.parseInt(doc.get("id"))) ;
								for(int j = 0 ; j < 4 ; j ++){
									extendTitles[i][j] = v[0][j];
									extendUrls[i][j] = v[1][j];
									extendContents[i][j] = v[2][j];
								}
							}
						}
	
					} else {
						System.out.println("page null");
					}
				}else{
					System.out.println("result null");
				}
				int Num = (results == null ? 0 : results.scoreDocs.length);
				request.setAttribute("currentQuery",queryString);
				request.setAttribute("currentPage", page);
				request.setAttribute("queryWords", queryWords);
				request.setAttribute("titles", titles);
				request.setAttribute("urls", urls);
				request.setAttribute("contents", contents);
				request.setAttribute("totalNum", Num);
				request.setAttribute("imgUrls", imgUrls);
				request.setAttribute("simQuery", simQuery);
				request.setAttribute("extendTitles", extendTitles);
				request.setAttribute("extendUrls", extendUrls);
				request.setAttribute("extendContents", extendContents);
				request.setAttribute("hasExtend", hasExtend);
				request.getRequestDispatcher("/imageshow.jsp").forward(request,
						response);
			}
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}
