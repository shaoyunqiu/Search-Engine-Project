import java.util.HashMap;
import java.util.Map;


public class VerticalIndexs {
	public Map<Integer, Integer> idMap ;
	public String[][] urls ;
	public String[][] titles ;
	public String[][] contents ;
	
	public VerticalIndexs() {
		// TODO Auto-generated constructor stub
		idMap = new HashMap<Integer, Integer>() ;
		urls = new String[12][4] ;
		titles = new String[12][4] ;
		contents = new String[12][4] ;
	}
}
