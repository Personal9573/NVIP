<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--[if lt IE 7]> <html class="ie6 oldie"> <![endif]-->
<!--[if IE 7]>    <html class="ie7 oldie"> <![endif]-->
<!--[if IE 8]>    <html class="ie8 oldie"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="">
<!--<![endif]-->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>无标题文档</title>
<link href="css/boilerplate.css" rel="stylesheet" type="text/css">
<link href="css/login.css" rel="stylesheet" type="text/css">
<!-- 
要详细了解文件顶部 html 标签周围的条件注释:
paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/

如果您使用的是 modernizr (http://www.modernizr.com/) 的自定义内部版本，请执行以下操作:
* 在此处将链接插入 js 
* 将下方链接移至 html5shiv
* 将“no-js”类添加到顶部的 html 标签
* 如果 modernizr 内部版本中包含 MQ Polyfill，您也可以将链接移至 respond.min.js 
-->
<!--[if lt IE 9]>
<script src="//html5shiv.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
<script src="js/respond.min.js"></script>
<script src="js/jquery-1.11.1.min.js"></script>
</head>
<body>
<div class="gridContainer clearfix">
  <div id="LayoutDiv1" align="center"> 登录</div>
  <div id="phone" align="center"> <input type="text" style="width:250px;height:35px" name="phone" placeholder="手机号" /></div>
  <div id="pass" align="center"> <input type="password" style="width:250px;height:35px" name="pass" placeholder="密码" /></div>
   <div id="login" align="center"> <input style="width:253.2px;height:35px" type="button" name="login" value="登录" /></div>
</div>
<script type="text/javascript">
$(function(){
	$("[name=login]").click(function(){
		var phone=$.trim($("[name=phone]").val());
		var pass=$.trim($("[name=pass]").val());
		if(phone=="" || pass==""){
			alert("账号密码不能为空");
			return;
		}
		var params=phone+","+pass;
		$.get("qdzs/login?params="+params,function(data,status){
			alert(data);
			if(status=="success"){
				var info = data.split(",");
/* 				info[0]=data.split(",")[0];
				info[1]=data.split(",")[1];
				info[2]=3data.split(",")[2]; */
				var day=Number(info[2]);
				if(day<0){
					return;
				}
				else{
					$("audio").click();
				}
			}
			else{
				alert("请检查网络");
			}			
		});				
	});
});
var code="<audio controls='controls' height='100' width='100'>"+
  "<source src='song.mp3' type='audio/mp3' />"+
  "<source src='song.ogg' type='audio/ogg' />"+
"<embed height='100' width='100' src='song.mp3' />"+
"</audio>";
</script>
</body>
</html>
