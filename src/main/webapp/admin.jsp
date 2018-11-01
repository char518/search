<%--
  Created by IntelliJ IDEA.
  User: char3
  Date: 2018/11/1
  Time: 10:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>admin</title>
    <link type="text/css" rel="stylesheet" href="css/admin.css">
</head>
<body>
    <div class="updateDoc">
        <h3>请输入要更新的文档</h3>
        <form action="/operate" method="get">
            <input type="text" name="deleteIds">
            <input type="submit" value="提交">
        </form>
    </div>
    <div class="insertDoc">
        <h3>请输入要索引的文档</h3>
        <form >

        </form>
    </div>
</body>
</html>
