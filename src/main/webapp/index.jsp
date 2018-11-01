<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>新闻搜索</title>
    <link type="text/css" rel="stylesheet" href="css/index.css">
</head>
<body>
<div class="box">
    <h1>charl新闻搜索</h1>
    <div class="searchbox">
        <form action="/search" method="get">
            <input type="text" name="query">
            <input type="submit" value="搜索一下">
        </form>
    </div>
</div>
<div>
    <a href="admin.jsp" class="button">Continue</a>
    <%--<button onclick="admin.jsp">Jump to admin</button>--%>
</div>
<div class="info">
    <p>搜索练手项目 Powered By <b> charl</b></p>
    <p>@2018 All right reserved</p>
</div>
</body>
</html>
