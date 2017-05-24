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
		} // map html and node
		
		for(int i = 0 ; i < upper2 ; i ++){
			String filename = path2 + "/" + Integer.toString(i) + ".json" ;
			MyJsonDealer jsonDealer = new MyJsonDealer(filename) ;
			if(jsonDealer.ReadFile()){
				jsonDealer.getContent();
				String url = UrlNorm.urlNormalize(jsonDealer.url) ;
				url_id.put(url, new Integer(jsonDealer.id+1+upper1)) ;
			}else {
				continue ;
			}
		} //map pdf and node
		return url_id ;
	}
	
	public static void main(String[] argv) {
		String outpath = "../tmpres/nodemap.txt";
		Map<String, Integer> url_id ;
		url_id = MyMapBuilder.geturlmap("../data/html", 100, "../data/pdf", 0) ;
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
