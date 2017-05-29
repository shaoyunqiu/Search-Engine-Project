
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
	
}
