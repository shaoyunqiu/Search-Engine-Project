import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TrieNode {
	public int wordnum ;
	public Map<Character, TrieNode> child ;
	
	public TrieNode() {
		// TODO Auto-generated constructor stub
		wordnum = 0 ;
		child = new HashMap<Character, TrieNode>() ;
	}
}
