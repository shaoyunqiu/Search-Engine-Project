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
	public static final String indexDir="forIndex";
	public static final String picDir="";
	private ImageSearcher search=null;
	public ImageServer(){
		super();
		search=new ImageSearcher(new String(indexDir+"/index"));
		search.loadGlobals(new String(indexDir+"/global.txt"));
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
		if (operationString != null && replaceString != null) {
			int replaceNo=Integer.parseInt(replaceString);
			int page=Integer.parseInt(pageString);
			TopDocs results=search.searchQuery(queryString, "title", 100);
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
				String[] titles=null;
				String[] urls=null;
				String[] contents=null;
				TopDocs results=search.searchQuery(queryString, "title", 100);
				ArrayList<String> queryWords = search.splitQuery(queryString);
				if (results != null) {
					ScoreDoc[] hits = showList(results.scoreDocs, page);
					if (hits != null) {
						titles = new String[hits.length];
						urls = new String[hits.length];
						contents = new String[hits.length];
						for (int i = 0; i < hits.length && i < PAGE_RESULT; i++) {
							Document doc = search.getDoc(hits[i].doc);
							System.out.println("doc=" + hits[i].doc + " score="
									+ hits[i].score + " url= "
									+ doc.get("url")+ " title= "+doc.get("title"));
							titles[i] = doc.get("title");
							urls[i] = doc.get("url");
							contents[i] = doc.get("content");
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
