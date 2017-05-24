
public class UrlNorm {
	public static String urlNormalize(String _url) {
		_url = _url.replace("\\", "/");
		if(!(_url.startsWith("http://"))){
			if(_url.startsWith("/")){
				_url = "http://news.tsinghua.edu.cn" + _url;
			}
			else {
				_url = "http://" + _url ;
			}
		}
		return _url ;
	}
	
	/*public static void main(String[] argv) {
		String test1 = "news.tsinghua.edu.cn\\publish\\thunews\\index.html" ;
		String test2 = "/publish/new.html" ;
		String test3 = "\\\\index.html" ;
		test1 = UrlNorm.urlNormalize(test1) ;
		test2 = UrlNorm.urlNormalize(test2) ;
		test3 = UrlNorm.urlNormalize(test3) ;
		System.out.println(test1);
		System.out.println(test2);
		System.out.println(test3);
	}*/
}
