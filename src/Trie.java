import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


public class Trie {
	private TrieNode root ;
	private ArrayList<String> defaultstr ;
	//public ArrayList<String> alltitles ;
	public Trie(String filename) {
		// TODO Auto-generated constructor stub
		root = new TrieNode() ;
		defaultstr = new ArrayList<String>() ;
		defaultstr.add("清华大学新闻网") ;
		//alltitles = new ArrayList<String>() ;
		BuildTrie(filename) ;
	}
	
	public void BuildTrie(String filename) {
		File file = new File(filename) ;
		int cnt = 0 ;
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"Unicode")) ;
			String title = null ;
			while ((title = bufferedReader.readLine()) != null) {
				String[] after = title.split(" |【|】|-") ;
				int size = after.length ;
				if(size == 0) continue ;
				else {
					for(int i = 0 ; i < size ; i ++){
						InsertString(after[i]) ;
					}
				}
				++ cnt ;
				//if (cnt % 10000 == 0) System.out.println("10000 str");
			}
			bufferedReader.close();
			System.out.println(cnt);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("buildTrie failed");
		}
	}
	
	public void InsertString(String s) {
		if(root == null || s.length() == 0){
			//System.out.println("no string to insert");
			return ;
		}
		TrieNode curr = root ;
		int size = s.length() ;
		for(int i = 0 ; i < size ; i ++){
			Character now = s.charAt(i) ;
			if(curr.child.containsKey(now)){
				curr = curr.child.get(now) ;
			}
			else {
				TrieNode node = new TrieNode() ;
				curr.child.put(now, node) ;
				curr = node ;
			}
		}
		++ curr.wordnum ;
	}
	
	public ArrayList<String> Search(String s, int maxnum) {
		ArrayList<String> res = new ArrayList<String>() ;
		if(s.length() == 0 || root == null) return defaultstr ;
		String[] after = s.split(" ") ;
		int size = after.length ;
		String target ;
		if(size > 0){
			target = after[size-1] ;
		}else {
			return defaultstr ;
		}
		TrieNode node = SearchPreNode(target) ;
		//Boolean flag = true ;
		AddBFS(node, s, res, maxnum);
		return res ;
	}
	
	private TrieNode SearchPreNode(String s) {
		TrieNode curr = root ;
		int size = s.length() ;
		for(int i = 0 ; i < size ; i ++){
			Character now = s.charAt(i) ;
			if(curr.child.containsKey(now)){
				curr = curr.child.get(now) ;
			}
			else {
				break ;
			}
		}
		return curr ;
	}
	
	/*private void AddStr(TrieNode node, String preString, ArrayList<String> res, int maxnum, Boolean flag) {
		if(flag.booleanValue() == false) return ;
		if(res.size() >= maxnum){
			flag = false ;
			return ;
		}
		if(node.wordnum > 0){
			res.add(preString) ;
		}
		if(node.child.size() == 0){
			return ;
		}
		else {
			for(Map.Entry<Character, TrieNode> entry: node.child.entrySet()){
				if(flag.booleanValue() == false) return ;
				if(res.size() >= maxnum){
					flag = false ;
					return ;
				}
				AddStr(entry.getValue(), preString+entry.getKey(), res, maxnum, flag);
				if(flag.booleanValue() == false) return ;
				if(res.size() >= maxnum){
					flag = false ;
					return ;
				}
			}
		}
	}*/
	
	private void AddBFS(TrieNode node, String preString, ArrayList<String> res, int maxnum) {
		Queue<TrieNode> queue = new LinkedList<TrieNode>() ;
		Queue<String> sq = new LinkedList<String>() ;
		queue.add(node) ;
		sq.add(preString) ;
		int cnt = 0 ;
		while(cnt < maxnum && !queue.isEmpty()){
			TrieNode tmp = queue.poll() ;
			String nowString = sq.poll() ;
			//System.out.println(nowString + " " + queue.size() + " " + sq.size());
			//System.out.println(tmp.wordnum);
			if(tmp.wordnum > 0){
				//System.out.println(tmp.wordnum + nowString);
				res.add(nowString) ;
				++ cnt ;
				if(cnt >= maxnum) break ;
			}
			if(tmp.child.size() == 0) continue ;
			for(Map.Entry<Character, TrieNode> entry:tmp.child.entrySet()){
				queue.add(entry.getValue()) ;
				sq.add(nowString + entry.getKey()) ;
			}
		}
		System.out.println(cnt);
	}
	
	/*private void AddStrB(TrieNode node, String preString, ArrayList<String> res,int maxnum) {
		if(node.wordnum > 0) res.add(preString) ;
		if(res.size() > maxnum) return ;
		if(node.child.size() == 0) return ;
		TrieNode layer1 ;
		TrieNode layer2 ;
		for(Map.Entry<Character, TrieNode> e1: node.child.entrySet()){
			layer1 = e1.getValue() ;
			if(layer1.wordnum > 0) res.add(preString + e1.getKey()) ;
			if(layer1.child.size() == 0) continue ;
			if(res.size() >= maxnum) return ;
			for(Map.Entry<Character, TrieNode> e2: layer1.child.entrySet()){
				layer2 = e2.getValue() ;
				if(layer2.wordnum > 0) res.add(preString+e1.getKey()+e2.getKey()) ;
				if(res.size() >= maxnum) return ;
			}
		} // maxdepth 2 layer
	}*/
	
	/*public static void main(String[] argv) {
		String path = "forIndex/titles/alltitles.txt" ;
		//String path = "../Test/alltitles.txt" ;
		Trie test = new Trie(path) ;
		int maxnum = 9 ;
		System.out.println("test null");
		//long startime = System.currentTimeMillis() ;
		ArrayList<String> res = test.Search("刘奕群", maxnum) ;
		for(int i = 0 ; i < maxnum && i < res.size() ; i ++){
			System.out.println(res.get(i));
		}
		System.out.println("test 清华");
		res = test.Search("清华", maxnum) ;
		for(int i = 0 ; i < maxnum && i < res.size() ; i ++){
			System.out.println(res.get(i));
		}
		//long endtime = System.currentTimeMillis() ;
		//System.out.println("exe time is " + (endtime-startime));
	}*/
}
