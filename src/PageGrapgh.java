import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class PageGrapgh {
	Map<String, Integer> urlMap ;
	
	public PageGrapgh(Map<String, Integer> _map) {
		// TODO Auto-generated constructor stub
		urlMap = _map ;
	}
	
	public ArrayList<Integer> url2int(ArrayList<String> _urls){
		ArrayList<Integer> nodeids = new ArrayList<Integer>() ;
		int size = _urls.size() ;
		for(int i = 0 ; i < size ; i ++){
			String url = _urls.get(i) ;
			if(urlMap.containsKey(url)){
				nodeids.add(new Integer(urlMap.get(url))) ;
			}
			else {
				continue ;
			}
		}
		return nodeids ;
	}
	
	public static void main(String[] argv) {
		String outpath = "../tmpres/graph.txt" ;
		Map<String, Integer> url_id = MyMapBuilder.geturlmap("../data/html", 52305, "../data/pdf", 19) ;
		PageGrapgh pageGrapgh = new PageGrapgh(url_id) ;
		File file = new File(outpath) ;
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			String path = "../data/html" ;
			for(int i = 0 ; i < 52305 ; i++){
				String filename = path + "/" + Integer.toString(i) + ".html" ;
				File input = new File(filename) ;
				if(input.exists()){
					String ans = Integer.toString(i) + ":" ;
					ArrayList<String> urls = HtmlContent.getLinksAll(input) ;
					ArrayList<Integer> ids = pageGrapgh.url2int(urls) ;
					int size = ids.size() ;
					if(size == 0) continue ;
					else if (size < 2) {
						ans += ids.get(0).toString() + "\n" ;
					}
					else {
						for(int j = 0 ; j < size -1; j ++){
							ans += ids.get(j).toString() + "," ;
						}
						ans += ids.get(size-1).toString() + "\n" ;
					}
					out.write(ans.getBytes());
				}
				else {
					System.out.println("file not exit, next");
					continue ;
				}
				if(i % 10000 == 0) System.out.println("html 10000 finish");
			}
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("write graph faiiled");
		}
		System.out.println("get pageGraph finish");
	}
}
