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
</style>

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
            <a class="fake-link-scroll navbar-brand">“清新”搜索</a>
            <a class="navbar-brand">&gt;</a>
            <a class="navbar-brand" href="<%=path %>/imagesearch.jsp">回到主页</a>
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
				<input class="form-control" name="query" value="<%=currentQuery%>" type="text" style="width:100%; height:35px;"/>
				</label>
				<label style="width:15%;vertical-align:middle">
				<button type="submit" class="btn btn-info" style="width:100%;font-size:15px">搜索</button>
				</label>
				</div>
			</form>
		</div>
		<br>
		<% 
		 	String[] titles=(String[]) request.getAttribute("titles");
		 	String[] urls=(String[]) request.getAttribute("urls");
		 	int hits=(Integer) request.getAttribute("totalNum");
		%>
		<div id="Layer2">
		 <div id="imagediv">共搜索到<%=hits %>条新闻（最多展示100张）
		 <br></br>
		 <% 
		 	if(titles!=null && titles.length>0){
		 		for(int i=0;i<titles.length;i++){
		 			String temp = titles[i];
		 			ArrayList<String> s = new ArrayList();
		 			int index = temp.indexOf(currentQuery.trim());
					while(index > -1) {
						//System.out.println(temp.substring(0, index));
						s.add(temp.substring(0, index));
						s.add(temp.substring(index, index + currentQuery.trim().length()));
						temp = temp.substring(index + currentQuery.trim().length(), temp.length());
						index = temp.indexOf(currentQuery.trim());
					}
					if (temp.length() > 0) {
						s.add(temp);
					}
					int length = s.size();%>
		 		<div style="width:56%">
		 				<a class="fake-link" href="<%=urls[i]%>" target="_blank">
				  			<p style="font-size:0px;display:inline;"><%=(currentPage-1)*10+i+1%>. 
				  			<%for(int j = 0; j < length; j ++) {
				  				if(s.get(j).indexOf(currentQuery) < 0) {%>
				  					<span style="font-size:21px;"><%=s.get(j).trim()%></span>
				  			<% } else {%>
				  					<span style="color:red;font-size:21px;"><%=s.get(j).trim() %></span>
				  			<% };
				  			};%>
				  			</p>
				  			<hr>
				  			</br>	
				 		</a>
			 		
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
