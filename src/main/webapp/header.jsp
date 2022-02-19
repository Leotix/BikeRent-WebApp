<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <style>
            @import url("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css");
            <%@include file="/WEB-INF/style/navbar.css"%>
        </style>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
        <script src="http://code.jquery.com/ui/1.9.2/jquery-ui.js"></script>
        <script><%@include file="/WEB-INF/script/navbar_scroll.js"%></script>
    </head>
    <body>
        <%
            String username = (String) session.getAttribute("username");
            boolean isUserLoggedIn = username != null;
        %>
        <ul class="header-class" style="z-index: 999;">
            <li id="logo" style="float:left"><a id="active" href="index"><i class="fa fa-bicycle"></i>BikeRent</a></li>
            <%
                if(!isUserLoggedIn){ %>
                    <li><a href="register"><i class="fa fa-user-plus" aria-hidden="true"></i>Register</a></li>
                    <li><a href="login"><i class="fa fa-sign-in" aria-hidden="true"></i>Login</a></li>
            <% }
                else{ %>
                    <li><a href="logout-servlet"><i class="fa fa-sign-out" aria-hidden="true"></i>Logout</a></li>
                    <li><a href="profile"><i class="fa fa-user" aria-hidden="true"></i> <%=username%></a></li>
                    <li><a href="bikes"><i class="fa fa-plus-square"></i> Rent bike</a> </li>
            <%
                }
            %>
        </ul>
    </body>
</html>
