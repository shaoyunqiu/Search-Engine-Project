import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;

import javax.persistence.Id;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class MyJsonDealer {
	public String filepath ;
	public String data ;
	public String title ;
	public String url;
	public int id ;
	
	public MyJsonDealer(String path) {
		// TODO Auto-generated constructor stub
		filepath = path ;
	}
	
	public boolean ReadFile(){
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
			return true ;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("read json data fail: " + filepath);
			return false ;
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
	
	public static void main(String[] argv) {
		ArrayList<String> alltitles = new ArrayList<String>() ;
		for(int i = 0 ; i < 52305 ; i ++){
			String filename = "../data/html/" + Integer.toString(i) + ".json" ;
			MyJsonDealer myJsonDealer= new MyJsonDealer(filename) ;
			if(myJsonDealer.ReadFile()){
				myJsonDealer.getContent();
				alltitles.add(myJsonDealer.title) ;
			}
			else {
				System.out.println("can not open " + filename);
				continue ;
			}
			if(i % 10000 == 0) System.out.println("html 10000");
		}
		for(int i = 0 ; i < 19 ; i ++){
			String filename = "../data/pdf/" + Integer.toString(i) + ".json" ;
			MyJsonDealer myJsonDealer = new MyJsonDealer(filename) ;
			if(myJsonDealer.ReadFile()){
				myJsonDealer.getContent();
				alltitles.add(myJsonDealer.title) ;
			}
			else {
				System.out.println("can't open " + filename);
				continue ;
			}
		}
		String outpath = "forIndex/titles/alltitles.txt" ;
		try {
			File file = new File(outpath) ;
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), "Unicode") ;
			for(int i = 0 ; i < alltitles.size() ; i ++){
				String t = alltitles.get(i) ;
				t = t.trim() ;
				t = t + "\n" ;
				out.write(t);
			}
			out.close();
			System.out.println("write title finish");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("write title failed");
		}
	}
}
