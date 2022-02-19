<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <style>
        <%@include file="/WEB-INF/style/profile.css"%>
    </style>
    <script><%@include file="/WEB-INF/script/sidebar_scroll.js"%></script>
    <%
    //Pobieramy nazwę zalogowanego użytkownika z sesji
        String username = (String) session.getAttribute("username");

    //Odsyłamy użytkownika z tej strony, jeśli się wylogował
        if(username == null){
            RequestDispatcher rd = request.getRequestDispatcher("login");
            session.setAttribute("errorMessage", "You must login in order to access user profile");
            rd.forward(request, response);
        }
    %>
    <title><%=username%>'s Profile</title>
</head>
    <body>
        <jsp:include page="profile_bars.jsp"/>
        <div class="user">
            <i class="fa fa-user" aria-hidden="true"></i>
            <h2><%=username%>'s profile</h2>
        </div>
        <jsp:include page="footer.jsp"/>
    </body>
</html>
