import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class VerticalIndexs {
	public Map<Integer, Integer> idMap ;
	public ArrayList<String> self_url ;
	public ArrayList<String> title ;
	public ArrayList<String> content ;
	public int[][] choices ;
	public String[][] childurl ;
	public String[][] childtitle ;
	public String[][] childcontent ;
	public int sum ;
	
	public VerticalIndexs() {
		// TODO Auto-generated constructor stub
		idMap = new HashMap<Integer, Integer>() ;
		self_url = new ArrayList<String>() ;
		title = new ArrayList<String>() ;
		content = new ArrayList<String>() ;
		choices = new int[13][4] ;
		sum = 13 ;
		File file= new File("../indexs/record.txt") ;
		int cnt = -1 ;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file)) ;
			String lineString = null ;
			while((lineString = bufferedReader.readLine()) != null){
				String[] after = lineString.split(" ") ;
				if(after.length < 4) continue ;
				else{
					++ cnt ;
					Integer id = Integer.valueOf(after[0]) ;
					idMap.put(id, cnt) ;
					title.add(after[1]) ;
					self_url.add(after[2]) ;
					content.add(after[3]) ;
				}
			}
			bufferedReader.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("open file record failed");
		}
		for(int i = 0 ; i < sum ; i ++){
			choices[i][0] = (i-2+sum) % sum ;
			choices[i][1] = (i-1+sum) % sum ;
			choices[i][2] = (i+1) % sum ;
			choices[i][3] = (i+2) % sum ;
		}
		file = new File("../indexs/child1.txt") ;
		childurl = new String[2][4] ;
		childtitle = new String[2][4] ;
		childcontent = new String[2][4] ;
		cnt = -1 ;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file)) ;
			String lineString = null ;
			while((lineString = bufferedReader.readLine()) != null){
				String[] after = lineString.split(" ") ;
				if(after.length < 3) continue ;
				else{
					++ cnt ;
					childtitle[0][cnt] = after[0] ;
					childurl[0][cnt] = after[1] ;
					childcontent[0][cnt] = after[2] ;
				}
			}
			bufferedReader.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("open child1 failed");
		}
		file = new File("../indexs/child2.txt") ;
		cnt = -1 ;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file)) ;
			String lineString = null ;
			while((lineString = bufferedReader.readLine()) != null){
				String[] after = lineString.split(" ") ;
				if(after.length < 2) continue ;
				else{
					++ cnt ;
					childtitle[1][cnt] = after[0] ;
					childurl[1][cnt] = after[1] ;
					childcontent[1][cnt] = after[2] ;
				}
			}
			bufferedReader.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("open child2 failed");
		}
	}
	
	public boolean hasVerticla(Integer docid){
		if(idMap.containsKey(docid)) return true ;
		else {
			return false ;
		}
	}
	
	public String[][] getVertical(Integer docid){
		String[][] res = new String[3][4] ;
		if(hasVerticla(docid)){
			int index = idMap.get(docid) ;
			if(index < 2){
				for(int i = 0 ; i < 4 ; i ++){
					res[0][i] = childtitle[index][i] ;
					res[1][i] = childurl[index][i] ;
					res[2][i] = childcontent[index][i] ;
				}
			}
			else {
				for(int i = 0 ; i < 4 ; i ++){
					res[0][i] = title.get(choices[index][i]) ;
					res[1][i] = self_url.get(choices[index][i]) ;
					res[2][i] = content.get(choices[index][i]) ;
				}
			}
		}else {
			for(int i = 0 ; i < 2 ; i ++){
				for(int j = 0 ; j < 4 ; j ++){
					res[i][j] = "" ;
				}
			}
		}
		return res ;
	}
	
	/*public static void main(String[] argv) {
		VerticalIndexs vi = new VerticalIndexs() ;
		if(vi.hasVerticla(26168)){
			String[][] v = vi.getVertical(26168) ;
			for(int i = 0 ; i < 4 ; i ++){
				System.out.println(v[0][i] + "\t" + v[1][i] + "\t" + v[2][i]);
			}
		}else {
			System.out.println("26168 has no vertical res");
		}
		
		if(vi.hasVerticla(28443)){
			String[][] v = vi.getVertical(28443) ;
			for(int i = 0 ; i < 4 ; i ++){
				System.out.println(v[0][i] + "\t" + v[1][i] + "\t" + v[2][i]);
			}
		}else {
			System.out.println("28443 has no vertical res");
		}
		
		if(vi.hasVerticla(100)){
			String[][] v = vi.getVertical(26168) ;
			for(int i = 0 ; i < 4 ; i ++){
				System.out.println(v[0][i] + "\t" + v[1][i] + "\t" + v[2][i]);
			}
		}else {
			System.out.println("100 has no vertical res");
		}
	}*/
}

