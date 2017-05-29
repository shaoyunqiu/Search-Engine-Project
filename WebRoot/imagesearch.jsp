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
</style>

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
			<input class="form-control" name="query" type="text" style="width:100%; height:40px;vertical-align:middle;"/>
			</label>
			<label style="width:15%;vertical-align:middle">
			<button type="submit" class="btn btn-info" style="width:100%;font-size:15px;">搜索</button>
			</label>
		</form>
	</div>
	<div class="container-fluid main">

	</div>
</div>
<!-- /Main -->

<footer class="text-center">TsingNews Search&nbsp; @copyright 2017  shaoyunqiu THUwangcy </footer>
</body>
</html>
