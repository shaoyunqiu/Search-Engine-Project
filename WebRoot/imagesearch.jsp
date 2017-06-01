<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
System.out.println(request.getCharacterEncoding());
response.setCharacterEncoding("utf-8");
System.out.println(response.getCharacterEncoding());
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
System.out.println(path);
System.out.println(basePath);
%>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>TsingNews Search</title>
<link rel="shortcut icon" href="<%=path %>/static/active.png" type="image/x-icon" />
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
    #search {
		 position: absolute;
		 top: 0;
		 bottom: 0;
		 left:30px;
		 right:0;
		 margin: auto;
		 width:50%;
		 height: 45%;
	}
	.search_suggest{ z-index:999; border:1px solid #999999; margin-top:-5px;padding-top:5px;height:auto;display:none;}  
	.search_suggest li{height:24px; overflow:hidden; padding-left:10px; line-height:24px; background:#FFFFFF; cursor:default;}  
	.search_suggest li:hover{background:#DDDDDD;}  
	.search_suggest li.hover{background:#DDDDDD;} 
</style>

<script>
$().ready(function() {
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
	        //suggestWrap.bind('blur',function(){setTimeout(hideSuggest,100);})  
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
	
	var XMLHttpReq;  
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
	function sendRequestforSim(query) {  
	    createXMLHttpRequest();  
	    var requrl = "servlet/ImageServer?operation=simQuery&query=" + query; 
	    XMLHttpReq.open("GET", requrl, true);  
	    XMLHttpReq.onreadystatechange = function(){processResponse();};//指定响应函数  
	    XMLHttpReq.send(null);  // 发送请求  
	}  
	// 处理返回信息函数  
	function processResponse() {
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

  

</script>

</head>
<body>
<!-- header -->
<div id="top-nav" class="navbar navbar-default navbar-static-top affix">
    <div class="container-fluid">
        <div class="nav navbar-nav navbar-left hidden-xs">
            <a class="fake-link-scroll navbar-brand">“清新”搜索&nbsp;&nbsp;&nbsp;一见倾心</a>
            <a class="navbar-brand">&gt;</a>
            <a class="fake-link-scroll navbar-brand">清华大学新闻搜索平台</a>
        </div>
    </div>
    <!-- /container -->
</div>
<!-- /Header -->

<!-- Main -->
<div class="wrap">
	<!-- Main page -->
	 <div id="search">
	 	<div id="information" class="tab-pane fade active in">
	        <div style="font-size:62px; text-align:center; margin-bottom:40px; margin-right:69px;">
	        	TsingNews Search
	    	</div>
	    </div>
		<form class="form" name="form1" method="get" action="servlet/ImageServer" style="width:100%">
			<label style="width:80%;vertical-align:middle">
			<input autocomplete="off" class="form-control input_search_key" id="gover_search_key" name="query" type="text" style="width:100%; height:40px;vertical-align:middle;"/>
			</label>
			<label style="width:15%;vertical-align:middle">
			<button type="submit" class="btn btn-info" style="width:100%;font-size:15px;">搜索</button>
			</label>
			<div class="search_suggest" id="gov_search_suggest">  
                <ul> 
                </ul>  
            </div>
		</form>
	</div>
	<div class="container-fluid main">

	</div>
</div>
<!-- /Main -->

<footer class="text-center">TsingNews Search&nbsp; @copyright 2017  shaoyunqiu THUwangcy </footer>
</body>
</html>
