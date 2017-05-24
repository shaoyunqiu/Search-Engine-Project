import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;


public class MyMapBuilder {
	public static Map<String, Integer> geturlmap(String path1, int upper1, String path2, int upper2) {
		Map<String, Integer> url_id = new HashMap<String, Integer>() ;
		for(int i = 0 ; i < upper1 ; i ++){
			String filename = path1 + "/"+Integer.toString(i) + ".json" ;
			MyJsonDealer jsonDealer = new MyJsonDealer(filename) ;
			if(jsonDealer.ReadFile()){
				jsonDealer.getContent();
				String url = UrlNorm.urlNormalize(jsonDealer.url) ;
				url_id.put(url, new Integer(jsonDealer.id)) ;
			}
			else {
				continue ;
			}
			if(i % 10000 == 0) System.out.println("html 10000 finish");
		} // map html and node
		
		for(int i = 0 ; i < upper2 ; i ++){
			String filename = path2 + "/" + Integer.toString(i) + ".json" ;
			MyJsonDealer jsonDealer = new MyJsonDealer(filename) ;
			if(jsonDealer.ReadFile()){
				jsonDealer.getContent();
				String url = UrlNorm.urlNormalize(jsonDealer.url) ;
				url_id.put(url, new Integer(jsonDealer.id)) ;
			}else {
				continue ;
			}
		} //map pdf and node
		System.out.println("pdf read finish");
		return url_id ;
	}
	
	public static Map<Integer, String> getIdMap(String path1, int upper1, String path2, int upper2){
		System.out.println("get id-url map");
		Map<Integer, String> id_url = new HashMap<Integer, String>() ;
		for(int i = 0 ; i < upper1 ; i ++){
			String filename = path1 + "/"+Integer.toString(i) + ".json" ;
			MyJsonDealer jsonDealer = new MyJsonDealer(filename) ;
			if(jsonDealer.ReadFile()){
				jsonDealer.getContent();
				String url = UrlNorm.urlNormalize(jsonDealer.url) ;
				id_url.put(new Integer(jsonDealer.id), url) ;
			}
			else {
				continue ;
			}
			if(i % 10000 == 0) System.out.println("html 10000 finish");
		} // map node id and url
		
		for(int i = 0 ; i < upper2 ; i ++){
			String filename = path2 + "/" + Integer.toString(i) + ".json" ;
			MyJsonDealer jsonDealer = new MyJsonDealer(filename) ;
			if(jsonDealer.ReadFile()){
				jsonDealer.getContent();
				String url = UrlNorm.urlNormalize(jsonDealer.url) ;
				id_url.put(new Integer(jsonDealer.id), url) ;
			}else {
				continue ;
			}
		} //map pdf and node
		System.out.println("pdf read finish");
		return id_url ;
	}
	
	public static void main(String[] argv) {
		String outpath = "../tmpres/nodemap.txt";
		Map<String, Integer> url_id ;
		url_id = MyMapBuilder.geturlmap("../data/html", 52305, "../data/pdf", 19) ;
		try {
			File file = new File(outpath) ;
			FileOutputStream out = new FileOutputStream(file) ;
			for(Map.Entry<String, Integer> entry: url_id.entrySet()){
				String outstr = entry.getKey() + "," + entry.getValue().toString() + "\n" ;
				out.write(outstr.getBytes());
			}
			out.close();
			System.out.println("write map finish");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("write map failed");
		}
	}
}
