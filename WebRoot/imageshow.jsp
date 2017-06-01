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
<link rel="shortcut icon" href="<%=path %>/static/active.png" type="image/x-icon" />
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
	.search_suggest{ position:absolute;z-index:999; border:1px solid #999999; margin-top:-5px;padding-top:5px;display:none;background:#FFFFFF;}  
	.search_suggest li{height:24px; overflow:hidden; padding-left:10px; line-height:24px; background:#FFFFFF; cursor:default;}  
	.search_suggest li:hover{background:#DDDDDD;}  
	.search_suggest li.hover{background:#DDDDDD;} 
	
	.page li {
		float:left; list-style:none;
		margin-left:6px;
		margin-right:6px;
	}
	.page a {
		border:1px solid #ccc;
		font-size:17px;
		padding-left:12px;
		padding-right:12px;
		padding-top:7px;
		padding-bottom:7px;
	}
	.page a.active {
		border:0px solid #ccc;
	}
	.page span {
		color:#0000B0;
	}
	.page img {
		width:25px; height:25px;
		margin-top:-50px;
		margin-left:7px;
	}
	.page li:nth-child(odd) img {
		margin-top:-70px;
	}
</style>
 
<script>
$().ready(function() {
	$( ".nav label" ).each(function() {
		var curRow = $(this).parent().prev();
		$(this).css("height", curRow.css("height"));
		$(this).css("line-height", curRow.css("height"));
		//alert(curRow.css("height"));
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
	$(".res-img").each(function() {
		$(this).css("height", $(this).next().css("height"));
		//alert($(this).parent().parent().parent().html());
	}); 
	
	$( ".search_suggest" ).each(function() {
		$(this).css("width", $(this).prev().prev().css("width"));
	});
	//实例化输入提示的JS,参数为进行查询操作时要调用的函数名  
	var searchSuggest =  new oSearchSuggest(sendRequestforSim); 
	
	$(document).mouseup(function(e){
		var _con = $('#gov_search_suggest');   // 设置目标区域
		if(!_con.is(e.target) && _con.has(e.target).length === 0){ // Mark 1
			$('#gov_search_suggest').hide();
		}
	});   
	
	//实现搜索输入框的输入提示js类  
	function oSearchSuggest(searchFuc){  
	    var input = $('#gover_search_key');  
	    var suggestWrap = $('#gov_search_suggest');  
	    var key = "";  
	    var init = function(){  
	        input.bind('keyup',sendKeyWord);  
	        //input.bind('blur',function(){setTimeout(hideSuggest,100);})  
	    }  
	    var hideSuggest = function(){  
	        suggestWrap.hide();  
	    }  
	      
	    //发送请求，根据关键字到后台查询  
	    var sendKeyWord = function(event){  
	        //键盘选择下拉项  
	        if(event.keyCode == 38||event.keyCode == 40){  
	            var current = suggestWrap.find('li.hover');  
	            if(event.keyCode == 38){  
	                if(current.length>0){  
	                    var prevLi = current.removeClass('hover').prev();  
	                    if(prevLi.length>0){  
	                        prevLi.addClass('hover');  
	                        input.val(prevLi.html());  
	                    }  
	                    else {
	                    	var first = suggestWrap.find('li:last');  
		                    first.addClass('hover');  
		                    input.val(first.html()); 
	                    }
	                }else{  
	                    var last = suggestWrap.find('li:last');  
	                    last.addClass('hover');  
	                    input.val(last.html());  
	                }  
	                  
	            }else if(event.keyCode == 40){  
	                if(current.length>0){  
	                    var nextLi = current.removeClass('hover').next();  
	                    if(nextLi.length>0){  
	                        nextLi.addClass('hover');  
	                        input.val(nextLi.html());  
	                    }  
	                    else {
	                    	var first = suggestWrap.find('li:first');  
		                    first.addClass('hover');  
		                    input.val(first.html()); 
	                    }
	                }else{  
	                    var first = suggestWrap.find('li:first');  
	                    first.addClass('hover');  
	                    input.val(first.html());  
	                }  
	            }  
	              
	        //输入字符  
	        }else{   
	            var valText = $.trim(input.val());  
	            if(valText ==''||valText==key){  
	                return;  
	            }  
	            searchFuc(valText);  
	            key = valText;  
	        }             
	          
	    }  
	    //请求返回后，执行数据展示  
	    this.dataDisplay = function(data){ 
	        if(data.length<=0){  
	            suggestWrap.hide();  
	            return;  
	        }  
	        
	        //往搜索框下拉建议显示栏中添加条目并显示  
	        var li;  
	        var tmpFrag = document.createDocumentFragment();  
	        suggestWrap.find('ul').html('');  
	        for(var i=0; i<data.length; i++){  
	            li = document.createElement('LI');  
	            li.innerHTML = data[i];  
	            tmpFrag.appendChild(li);  
	        }  
	        suggestWrap.find('ul').append(tmpFrag);  
	        suggestWrap.show();  
	          
	        //为下拉选项绑定鼠标事件  
	        suggestWrap.find('li').hover(function(){  
	                suggestWrap.find('li').removeClass('hover');  
	                $(this).addClass('hover');  
	          
	            },function(){  
	                $(this).removeClass('hover');  
	        });
	        suggestWrap.find('li').each(function() {
	        	$(this).bind('click',function(){ 
		            input.val($(this).html());  
		            suggestWrap.hide();  
		            $(".form").submit();
		        });  
	        });
	    }  
	    init();  
	};  

	//发送请求函数  
	function sendRequestforSim(query) {  
	    createXMLHttpRequest();  
	    var requrl = "ImageServer?operation=simQuery&query=" + query; 
	    XMLHttpReq.open("GET", requrl, true);  
	    XMLHttpReq.onreadystatechange = function(){processResponseSim();};//指定响应函数  
	    XMLHttpReq.send(null);  // 发送请求  
	}  
	// 处理返回信息函数  
	function processResponseSim() {
	    if (XMLHttpReq.readyState == 4) { // 判断对象状态  
	        if (XMLHttpReq.status == 200) { // 信息已经成功返回，开始处理信息   
	            addSimQuery();  
	        } else { //页面不正常  
	            window.alert("Servlet Error.");  
	        }  
	    }  
	}  
	
	function addSimQuery() {
		var simQueryNum = XMLHttpReq.responseXML.getElementsByTagName("simQuery").length;
		var simQuery = new Array(simQueryNum);
	    for (var i = 0; i < simQueryNum; i ++) {
	    	var queryWord = XMLHttpReq.responseXML.getElementsByTagName("simQuery")[i].firstChild.nodeValue;
	    	simQuery[i] = queryWord;
	    } 
	    searchSuggest.dataDisplay(simQuery);  
	}
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
    var page = $("li a.active span").html();
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
				<input autocomplete="off" class="form-control input_search_key" id="gover_search_key" name="query" value="<%=currentQuery%>" type="text" style="width:100%; height:40px;"/>
				</label>
				<label style="width:15%;vertical-align:middle">
				<button type="submit" class="btn btn-info" style="width:100%;font-size:15px">搜索</button>
				</label>
				<div class="search_suggest" id="gov_search_suggest">  
	                <ul> 
	                </ul>  
	            </div>
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
		 	String[] imgUrls=(String[]) request.getAttribute("imgUrls");
		 	String[][] extendTitles=(String[][]) request.getAttribute("extendTitles");
		 	String[][] extendUrls=(String[][]) request.getAttribute("extendUrls");
		 	String[][] extendContents=(String[][]) request.getAttribute("extendContents");
		 	boolean[] hasExtend=(boolean[]) request.getAttribute("hasExtend");
		 	ArrayList<String> simQuery=(ArrayList<String>) request.getAttribute("simQuery");
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
		 			if(imgUrls[i].length() != 0) 
		 				imageDisplay = true;
		 			// 是否显示扩展链接
		 			boolean extendLinkDisplay = false;
		 			if(hasExtend[i]) {
		 				extendLinkDisplay = true;
		 				for(int j = 0; j < 4; j ++) {
		 					if(extendContents[i][j].length() > 29) {
		 						extendContents[i][j] = extendContents[i][j].substring(0,29);
		 						extendContents[i][j] += "...";
		 					}
		 				}
		 			}
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
					String urlDisplay = urls[i];
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
			
					 			<img  class="col-xs-3 res-img" style="height:96px;" src="<%=imgUrls[i] %>" />
					 		
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
				  			<% if(extendLinkDisplay) { %>
					 			<div class="row" style="font-size:0px; margin-top:10px;">
						 			<div class="col-xs-1"></div>
						 			<%for(int j = 0; j < 2; j ++) { %>
						 			<div class="col-xs-4">
						 				<a class="fake-link" href="<%=extendUrls[i][j]%>" target="_blank">
						 				<span style="color:#0000B0;font-size:18px;"><%=extendTitles[i][j]%></span><br>
						 				</a>
						 				<span style="font-size:12px;letter-spacing:1px;"><%=extendContents[i][j]%></span>
						 			</div>
						 			<div class="col-xs-1"></div>
						 			<%}; %>
					 			</div>
					 			
					 			<div class="row" style="font-size:0px; margin-top:10px;">
						 			<div class="col-xs-1"></div>
						 			<%for(int j = 2; j < 4; j ++) { %>
						 			<div class="col-xs-4">
						 				<a class="fake-link" href="<%=extendUrls[i][j]%>" target="_blank">
						 				<span style="color:#0000B0;font-size:18px;"><%=extendTitles[i][j]%></span><br>
						 				</a>
						 				<span style="font-size:12px;letter-spacing:1px;"><%=extendContents[i][j]%></span>
						 			</div>
						 			<div class="col-xs-1"></div>
						 			<%}; %>
					 			</div>
						 	<% };%>
				 	</div>
				 	<div onclick=sendRequest(this) class="col-xs-2 nav">
				 		<label style="width:110px;">&#x203a;</label>
				 	</div>
			 	</div>
			 	
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
		 <div class="row">
			 <div class="col-xs-7" >
			 	<h4>相关搜索</h4>
			 	<% for(int i = 0; i <= simQuery.size() / 3; i ++) {%>
			 	<div class="row" style="margin-top:5px;">
			 		<% for(int j = 0; j < 3 && i * 3 + j < simQuery.size(); j ++) {
			 		   		String sim = simQuery.get(i * 3 + j);
			 		   		if(sim.length() > 11) {
			 		   			sim = sim.substring(0, 11);
			 		   			sim += "...";
			 		   		}%>
			 		<div class="col-xs-4" >
					 	<a class="fake-link" style="font-size:16px;color:#0000B0;"
					 	 href="ImageServer?query=<%=simQuery.get(i * 3 + j)%>">
					 		<%=sim%>
					 	</a>
					</div>
					<% }; %>
				</div>
				<% }; %>	  
			 </div>
		</div>
		<br></br>
		<%if(hits > 10) { 
		   int maxPage = hits / 10 + (hits % 10 == 0 ? 0 : 1);%>
		 <div class="pull-left page" width="56%">
		 	<ul>
		 		<li></li>
		 	</ul>
		 </div>
		 <br>
		 <div class="pull-left page" width="56%">
		 	<ul>
				<%if(currentPage>1){ %>
					<li style="margin-top:21px;"><a href="ImageServer?query=<%=currentQuery%>&page=<%=currentPage-1%>"><span>&lt;&nbsp;上一页</span></a></li>
				<%}; %>
				<%for (int i=Math.max(1,currentPage-10);i<currentPage;i++){%>
					<li><img src="<%=path %>/static/inactive.png"><br><a href="ImageServer?query=<%=currentQuery%>&page=<%=i%>"><span><%=i%></span></a></li>
				<%}; %>
				<li><img src="<%=path %>/static/active.png"><br><a class="active" href="#"><span><%=currentPage%></span></a></li>
				<%for (int i=currentPage+1;i<=maxPage;i++){ %>
					<li><img src="<%=path %>/static/inactive.png"><br><a href="ImageServer?query=<%=currentQuery%>&page=<%=i%>"><span><%=i%></span></a></li>
				<%}; %>
				<%if(currentPage < maxPage){ %>
					<li style="margin-top:21px;"><a href="ImageServer?query=<%=currentQuery%>&page=<%=currentPage+1%>"><span>下一页&nbsp;&gt;</span></a></li>
				<%}; %>
			</ul>
		  </div>
		  <%}; %>
		</div>
	</div>
</div>
<!-- /Main -->

</body>
