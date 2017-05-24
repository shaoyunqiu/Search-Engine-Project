import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.persistence.Id;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class JsonDealer {
	public String filepath ;
	public String data ;
	public String title ;
	public String url;
	public int id ;
	
	public JsonDealer(String path) {
		// TODO Auto-generated constructor stub
		filepath = path ;
	}
	
	public void ReadFile(){
		BufferedReader br = null ;
		data = "" ;
		try {
			FileInputStream fileInputStream = new FileInputStream(filepath) ;
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream) ;
			br = new BufferedReader(inputStreamReader) ;
			String tmp = null ;
			while((tmp = br.readLine()) != null){
				data += tmp ;
			}
			br.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("read json data fail: " + filepath);
		}finally{
			if(br != null){
				try {
					br.close(); 
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		}
	}
	
	public void getContent(){
		JSONObject obj = JSONObject.fromObject(data) ;
		id = obj.getInt("id") ;
		title = obj.getString("title") ;
		url = obj.getString("url") ;
	}
	
	/*public static void main(String[] argv) {
		JsonDealer jsonDealer = new JsonDealer("data/html/1.json") ;
		jsonDealer.ReadFile();
		jsonDealer.getContent();
		System.out.println("id is :" + jsonDealer.id);
		System.out.println("title is :" + jsonDealer.title);
		System.out.println("url is : " + jsonDealer.url);
	}*/
}
