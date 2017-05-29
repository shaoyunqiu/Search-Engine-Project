<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
response.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String imagePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
%>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>TsingNews Search</title>
<!-- stylesheets -->
<link href="<%=path %>/static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="<%=path %>/static/font-awesome/css/font-awesome.min.css" rel="stylesheet">
<link href="<%=path %>/static/base/css/fonts.min.css" rel="stylesheet">
<link href="<%=path %>/static/base/css/eden.min.css" rel="stylesheet">
<link href="<%=path %>/static/animate/css/animate.min.css" rel="stylesheet">
<link href="<%=path %>/static/base/css/styles.css" rel="stylesheet">

<!-- script references -->
<script src="<%=path %>/static/jquery/jquery.min.js"></script>
<script src="<%=path %>/static/jquery/jquery.js"></script>
<script src="<%=path %>/static/jquery/jquery.history.min.js"></script>
<script src="<%=path %>/static/jquery/jquery.scrollto.min.js"></script>
<script src="<%=path %>/static/jquery/jquery.fixer.min.js"></script>
<script src="<%=path %>/static/bootstrap/js/bootstrap.js"></script>
<script src="<%=path %>/static/base/js/scripts.js"></script>



<style>
    *{
        margin: 0;
        padding: 0;
    }
    html,body { height: 100%;}
    .wrap{
        min-height: 100%;
        position: relative;
    }
    .main{
        padding-bottom: 60px;
    }
    footer {
        margin-top: -60px;
    }
    
    .nav label {
	    transition: opacity .2s;
		opacity:0;
		color:#FFFFFF;
	    font-size: 100pt;
	    text-align: center;
	    font-family: "Varela Round", sans-serif;
	    background-color: rgba(220, 220, 220, .3);
	    text-shadow: 0px 0px 15px rgb(119, 119, 119);
	}
	.nav label:hover { opacity: 1; }
</style>
 
<script>
$().ready(function() {
	$( ".nav label" ).each(function() {
		var curRow = $(this).parent().prev();
		$(this).css("height", curRow.css("height"));
		$(this).css("line-height", curRow.css("height"));
		//alert(curRow.css("height"));
	});
	$(".res-img").each(function() {
		$(this).css("height", $(this).next().css("height") - 1);
	});
	$( ".nav label" ).hover(//绑定了鼠标进入和鼠标移开的两个参数  
	  function() {  
	  	var curRow = $(this).parent().prev();
	  	curRow.css("background-color","rgba(220, 220, 220, .3)"); 
	  }, function() {
	  	var curRow = $(this).parent().prev();
	  	curRow.css("background-color","#FFF"); 
	  }
	);  
});

var XMLHttpReq;  
var replaceNo = 0;
//创建XMLHttpRequest对象         
function createXMLHttpRequest() {  
    if(window.XMLHttpRequest) { //Mozilla 浏览器  
        XMLHttpReq = new XMLHttpRequest();  
    }  
    else if (window.ActiveXObject) { // IE浏览器  
        try {  
            XMLHttpReq = new ActiveXObject("Msxml2.XMLHTTP");  
        } catch (e) {  
            try {  
                XMLHttpReq = new ActiveXObject("Microsoft.XMLHTTP");  
            } catch (e) {}  
        }  
    }  
}  
//发送请求函数  
function sendRequest(obj) {  
    createXMLHttpRequest();  
    var queryString = $("#queryString").html();
    var page = $("li.active a").html();
    var requrl = "ImageServer?operation=replace&query=" + queryString + "&page=" + page + "&replaceNo=" + replaceNo; 
    XMLHttpReq.open("GET", requrl, true);  
    XMLHttpReq.onreadystatechange = function(){processResponse(obj);};//指定响应函数  
    XMLHttpReq.send(null);  // 发送请求  
}  
// 处理返回信息函数  
function processResponse(obj) {
    if (XMLHttpReq.readyState == 4) { // 判断对象状态  
        if (XMLHttpReq.status == 200) { // 信息已经成功返回，开始处理信息   
            DisplayNewDoc(obj);   
        } else { //页面不正常  
            window.alert("Servlet Error.");  
        }  
    }  
}  

function splitText(queryWords, text, queryWordsNum) {
	var s = new Array();
	var turn = 0;
	var index = text.indexOf(queryWords[0]);
	for(var j = 0; j < queryWordsNum; j ++) {
		var curIndex = text.indexOf(queryWords[j].trim());
		if(curIndex > -1 && (index < 0 || curIndex < index)) {
			turn = j;
			index = curIndex;
		}
	}
	while(index > -1) {
		s.push(text.substring(0, index));
		s.push(text.substring(index, index + queryWords[turn].length));
		text = text.substring(index + queryWords[turn].length, text.length);
		turn = 0;
		index = text.indexOf(queryWords[0].trim());
		for(var j = 1; j < queryWordsNum; j ++) {
			var curIndex = text.indexOf(queryWords[j].trim());
			if(curIndex > -1 && (index < 0 || curIndex < index)) {
				turn = j;
				index = curIndex;
			}
		}
	}
	if (text.length > 0) {
		s.push(text);
	}
	return s;
}

function DisplayNewDoc(obj) { 
	var curRow = $(obj).prev();
	var rowParent = $(obj).parent();
	var curHeight = curRow.css("height");
    var title = XMLHttpReq.responseXML.getElementsByTagName("title")[0].firstChild.nodeValue;  
    var url = XMLHttpReq.responseXML.getElementsByTagName("url")[0].firstChild.nodeValue;  
    var content = XMLHttpReq.responseXML.getElementsByTagName("content")[0].firstChild.nodeValue;
    var queryWordsNum = XMLHttpReq.responseXML.getElementsByTagName("queryWords").length;
    var queryWords = new Array(queryWordsNum);
    for (var i = 0; i < queryWordsNum; i ++) {
    	var queryWord = XMLHttpReq.responseXML.getElementsByTagName("queryWords")[i].firstChild.nodeValue;
    	queryWords[i] = queryWord;
    } 
    replaceNo += 1;
    var splitTitle = splitText(queryWords, title, queryWordsNum);
    if (content.length > 120) {
    	content = content.substring(0, 120);
    	content += "...";
    }
    var splitContent = splitText(queryWords, content, queryWordsNum);
    var urlDisplay = url;
    if (url.length > 80) {
    	urlDisplay = url.substring(0, 80);
    	urlDisplay += "...";
    }
    var rowHtml = '<div class="col-xs-7" >\
    				<a class="fake-link" href="' + url + '" target="_blank">' +
    					'<p style="font-size:0px;display:inline;">';
    for (var i = 0; i < splitTitle.length; i ++) {
    	var findFlag = false;
    	for (var k = 0; k < queryWordsNum; k ++) {
    		if(splitTitle[i].indexOf(queryWords[k]) > -1) {
    			findFlag = true;
    			rowHtml += ('<span style="color:red;font-size:18px;">' + splitTitle[i].trim() + '</span>');
    			break;
    		}
    	} 
    	if(!findFlag) {
    		rowHtml += ('<span style="color:#0000B0;font-size:18px;">' + splitTitle[i].trim() + '</span>')
    	}
    }
    rowHtml = rowHtml + '</p> \
    				</a> \
    				<p style="font-size:0px;">';
    for (var i = 0; i < splitContent.length; i ++) {
    	var findFlag = false;
    	for (var k = 0; k < queryWordsNum; k ++) {
    		if(splitContent[i].indexOf(queryWords[k]) > -1) {
    			findFlag = true;
    			rowHtml += ('<span style="color:red;font-size:12px;letter-spacing:1px;">' + splitContent[i].trim() + '</span>');
    			break;
    		}
    	} 
    	if(!findFlag) {
    		rowHtml += ('<span style="font-size:12px;letter-spacing:1px;">' + splitContent[i].trim() + '</span>')
    	}
    }
    rowHtml = rowHtml +	'<br><a class="fake-link" href="' + url + '" target="_blank">\
    						<span style="color:#00A000;font-size:13px;padding-top:10px;">' + urlDisplay + '</span>\
    					</a>\
    				</p>\
    			</div>\
    			<div onclick=sendRequest(this) class="col-xs-2 nav">\
					<label style="width:110px;">&#x203a;</label>\
				</div>';
    rowParent.animate({opacity:0}, 200);
    setTimeout(function(){
    	rowParent.html(rowHtml);
    	rowParent.children().each(function() {
    		$(this).css("height", curHeight);
   			var arrowLabel = $(this).find("label");
   			$( ".nav label" ).hover(//绑定了鼠标进入和鼠标移开的两个参数  
				function() {  
					var curRow = $(this).parent().prev();
					curRow.css("background-color","rgba(220, 220, 220, .3)"); 
				}, function() {
					var curRow = $(this).parent().prev();
					curRow.css("background-color","#FFF"); 
				}
			); 
    		arrowLabel.css("height", curHeight);
    		arrowLabel.css("line-height", curHeight);
    	});
    },200)
    rowParent.animate({opacity:1}, 400);
    //alert(title);      
}
</script>

</head>

<body>
<%
	String currentQuery=(String) request.getAttribute("currentQuery");
	int currentPage=(Integer) request.getAttribute("currentPage");
%>
<!-- header -->
<div id="top-nav" class="navbar navbar-default navbar-static-top affix">
    <div class="container-fluid">
        <div class="nav navbar-nav navbar-left hidden-xs">
            <a class="fake-link-scroll navbar-brand">“清新”搜索&nbsp;&nbsp;&nbsp;一见倾心</a>
            <a class="navbar-brand">&gt;</a>
            <a class="fake-link-scroll navbar-brand">清华大学新闻搜索平台</a>
        </div>
        <div class="nav navbar-nav navbar-right hidden-xs">
            <a class="navbar-brand" href="<%=path %>/imagesearch.jsp">
            	<i class="fa fa-reply"></i>&nbsp;&nbsp;回到主页
            </a>
        </div>
    </div>
    <!-- /container -->
</div>
<!-- /Header -->
<!-- Main -->
<div class="wrap">
	<div class="container-fluid main">
		<!-- Main page -->
		<div id="Layer1" style="width:100%">
			<form class="form" name="form1" method="get" action="ImageServer" style="width:70%">
				<div>
				<label style="width:80%;vertical-align:middle">
				<input class="form-control" name="query" value="<%=currentQuery%>" type="text" style="width:100%; height:40px;"/>
				</label>
				<label style="width:15%;vertical-align:middle">
				<button type="submit" class="btn btn-info" style="width:100%;font-size:15px">搜索</button>
				</label>
				</div>
				<label><input type="radio" name="similarity" value="default"/>&nbsp;VSM模型&nbsp;&nbsp;&nbsp;&nbsp;</label>
				<label><input type="radio" name="similarity" value="simple"/>&nbsp;BM25模型</label>
				&nbsp;&nbsp;&nbsp;&nbsp;（不选择情况下默认为VSM模型）
			</form>
		</div>
		<br>
		<span id="queryString" style="display:none;"><%=currentQuery %></span>
		<% 
		 	String[] titles=(String[]) request.getAttribute("titles");
		 	String[] urls=(String[]) request.getAttribute("urls");
		 	String[] contents=(String[]) request.getAttribute("contents");
		 	int hits=(Integer) request.getAttribute("totalNum");
		 	ArrayList<String> queryWords = (ArrayList<String>) request.getAttribute("queryWords");
		%>
		<div id="Layer2">
		 <div id="imagediv">共搜索到<%=hits %>条新闻（最多展示100条）
		 <br></br>
		 <% 
		 	if(titles!=null && titles.length>0){
		 		for(int i=0;i<titles.length;i++){
		 			// 是否显示图片结果
		 			boolean imageDisplay = false;
		 			// 是否显示扩展链接
		 			boolean extendLinkDisplay = true;
		 			// 按查询分词分割title
		 			String temp = titles[i];
		 			ArrayList<String> s = new ArrayList<String>();
		 			int turn = 0;
					int index = temp.indexOf(queryWords.get(0).trim());
					for(int j = 1; j < queryWords.size(); j ++) {
						int curIndex = temp.indexOf(queryWords.get(j).trim());
						if(curIndex > -1 && (index < 0 || curIndex < index)) {
							turn = j;
							index = curIndex;
						}
					}
					while(index > -1) {
						//System.out.println(temp.substring(0, index));
						s.add(temp.substring(0, index));
						s.add(temp.substring(index, index + queryWords.get(turn).length()));
						temp = temp.substring(index + queryWords.get(turn).length(), temp.length());
						turn = 0;
						index = temp.indexOf(queryWords.get(0).trim());
						for(int j = 1; j < queryWords.size(); j ++) {
							int curIndex = temp.indexOf(queryWords.get(j).trim());
							if(curIndex > -1 && (index < 0 || curIndex < index)) {
								turn = j;
								index = curIndex;
							}
						}
					}
					if (temp.length() > 0) {
						s.add(temp);
					}
					int length = s.size();
					
					// 截取content长度为120
					if (contents[i].length() > 120) {
						contents[i] = contents[i].substring(0,120);
						contents[i] += "...";
					}
					// 按查询分词分割content
					temp = contents[i];
		 			ArrayList<String> splitContent = new ArrayList<String>();
		 			turn = 0;
					index = temp.indexOf(queryWords.get(0).trim());
					for(int j = 1; j < queryWords.size(); j ++) {
						int curIndex = temp.indexOf(queryWords.get(j).trim());
						if(curIndex > -1 && (index < 0 || curIndex < index)) {
							turn = j;
							index = curIndex;
						}
					}
					while(index > -1) {
						//System.out.println(temp.substring(0, index));
						splitContent.add(temp.substring(0, index));
						splitContent.add(temp.substring(index, index + queryWords.get(turn).length()));
						temp = temp.substring(index + queryWords.get(turn).length(), temp.length());
						turn = 0;
						index = temp.indexOf(queryWords.get(0).trim());
						for(int j = 1; j < queryWords.size(); j ++) {
							int curIndex = temp.indexOf(queryWords.get(j).trim());
							if(curIndex > -1 && (index < 0 || curIndex < index)) {
								turn = j;
								index = curIndex;
							}
						}
					}
					if (temp.length() > 0) {
						splitContent.add(temp);
					}
					int lengthContent = splitContent.size();
					
					// 展示的url
					String urlDisplay = "";
					if (urls[i].length() > 80) {
						urlDisplay = urls[i].substring(0,80);
						urlDisplay += "...";
					}%>
				<div class="row" style="font-size:0px;">
			 		<div class="col-xs-7" >
			 				<a class="fake-link" href="<%=urls[i]%>" target="_blank">
					  			<p style="font-size:0px;display:inline;"><%=(currentPage-1)*10+i+1%>. 
					  			<%for(int j = 0; j < length; j ++) {
					  				boolean findFlag = false;
					  				for(int k = 0; k < queryWords.size(); k ++) {
						  				if(s.get(j).indexOf(queryWords.get(k)) > -1) {
						  					findFlag = true;%>
						  					<span style="color:red;font-size:18px;"><%=s.get(j).trim()%></span>
						  			 <% 	break;    
						  			    }
						  			};
						  			if(!findFlag) {%>
					  					<span style="color:#0000B0;font-size:18px;"><%=s.get(j).trim() %></span>
					  			<%  };
					  			};%>
					  			</p>
					  				
					 		</a>
					 		<% if(imageDisplay) {%>
					 		<div class="row" style="font-size:0px;">
			
					 			<img  class="col-xs-3 res-img"  
					 			      src="http://news.tsinghua.edu.cn/publish/thunews/9659/20170527105113586692524/1495853645052.jpg" />
					 		
					 			<div class="col-xs-9" >
					 		<%}; %>
						 		<p style="font-size:0px;">
						 			<%
						 			for(int j = 0; j < lengthContent; j ++) {
						  				boolean findFlag = false;
						  				for(int k = 0; k < queryWords.size(); k ++) {
							  				if(splitContent.get(j).indexOf(queryWords.get(k)) > -1) {
							  					findFlag = true;%>
							  					<span style="color:red;font-size:12px;letter-spacing:1px;"><%=splitContent.get(j).trim()%></span>
							  			 <% 	break;    
							  			    }
							  			};
							  			if(!findFlag) {%>
						  					<span style="font-size:12px;letter-spacing:1px;"><%=splitContent.get(j).trim() %></span>
						  			<%  };
						  			};%>
					  				<br>
					  				<a class="fake-link" href="<%=urls[i]%>" target="_blank" >
						  				<span style="color:#00A000;font-size:13px;padding-top:10px;"><%=urlDisplay %></span>
						  			</a>
					  			</p>
					  		<% if(imageDisplay) {%>
					  			</div>
				  			</div>
				  			<%}; %>
				 	</div>
				 	<div onclick=sendRequest(this) class="col-xs-2 nav">
				 		<label style="width:110px;">&#x203a;</label>
				 	</div>
			 	</div>
			 	<% if(extendLinkDisplay) { %>
			 	<div class="row" style="font-size:0px">
			 		<div class="col-xs-7">
			 			<div class="row" style="font-size:0px">
			 			<div class="col-xs-1"></div>
			 			<div class="col-xs-4">
			 				<span style="color:#0000B0;font-size:18px;">综合新闻</span><br>
			 				<span style="font-size:12px;letter-spacing:1px;">综合新闻综合新闻综合新闻综合新闻综合新闻</span>
			 			</div>
			 			<div class="col-xs-1"></div>
			 			<div class="col-xs-4">
			 				<span style="color:#0000B0;font-size:18px;">网站地图</span><br>
			 				<span style="font-size:12px;letter-spacing:1px;">综合新闻综合新闻综合新闻综合新闻综合新闻</span>
			 			</div>
			 			</div>
			 			
			 			<div class="row" style="font-size:0px; margin-top:10px;">
			 			<div class="col-xs-1"></div>
			 			<div class="col-xs-4">
			 				<span style="color:#0000B0;font-size:18px;">要闻聚焦</span><br>
			 				<span style="font-size:12px;letter-spacing:1px;">综合新闻综合新闻综合新闻综合新闻综合新闻</span>
			 			</div>
			 			<div class="col-xs-1"></div>
			 			<div class="col-xs-4">
			 				<span style="color:#0000B0;font-size:18px;">关于我们</span><br>
			 				<span style="font-size:12px;letter-spacing:1px;">综合新闻综合新闻综合新闻综合新闻综合新闻</span>
			 			</div>
			 			</div>
			 		</div>
			 	</div>
			 	<% };%>
			 	<div class="row" style="font-size:0px">
			 		<div class="col-xs-7">
			 			<hr style="margin-bottom:10px;">
			 		</div>
			 	</div>
		 		<%}; %>
		 	<%}else{ %>
		 		<br>
		 		<strong style="font-size:40px;">没有找到相关新闻</strong>
		 	<%}; %>
		 </div>
		 <%if(hits > 10) { 
		   int maxPage = hits / 10 + (hits % 10 == 0 ? 0 : 1);%>
		 <div class="pull-left" width="56%">
		 	<ul class="pagination pull-right">
				<%if(currentPage>1){ %>
					<li><a href="ImageServer?query=<%=currentQuery%>&page=<%=currentPage-1%>">&laquo;</a></li>
				<%}; %>
				<%for (int i=Math.max(1,currentPage-5);i<currentPage;i++){%>
					<li><a href="ImageServer?query=<%=currentQuery%>&page=<%=i%>"><%=i%></a></li>
				<%}; %>
				<li class="active"><a href="#"><%=currentPage%></a></li>
				<%for (int i=currentPage+1;i<=maxPage;i++){ %>
					<li><a href="ImageServer?query=<%=currentQuery%>&page=<%=i%>"><%=i%></a></li>
				<%}; %>
				<%if(currentPage < maxPage){ %>
					<li><a href="ImageServer?query=<%=currentQuery%>&page=<%=currentPage+1%>">&raquo;</a></li>
				<%}; %>
			</ul>
		  </div>
		  <%}; %>
		</div>
	</div>
</div>
<!-- /Main -->

</body>
