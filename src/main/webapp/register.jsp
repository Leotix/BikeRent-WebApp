<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>BikeRent - Register</title>
        <style>
            <%@include file="/WEB-INF/style/form.css"%>
        </style>
    </head>
    <body>
        <%-- Przekierowujemy użytkownika z tej strony jeśli już jest zalogowany--%>
        <%
            if(session.getAttribute("username") != null){
                request.getRequestDispatcher("/").forward(request, response);
            }
        %>
        <jsp:include page="header.jsp"/>
        <div class="wrapper">
            <table class="content-table">
                <form action="register-servlet" autocomplete="off" method="post">
                    <tbody>
                    <tr>
                        <td>
                            <label>Username</label>
                        </td>
                        <td>
                            <input type="text" name="username" placeholder="Username"/>
                        </td>

                    </tr>

                    <tr>
                        <td>
                            <label>Password</label>
                        </td>
                        <td>
                            <input type="password" name="password" placeholder="Password"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label>Confirm password</label>
                        </td>
                        <td>
                            <input type="password" name="password2" placeholder="Confirm password"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label>Email</label>
                        </td>
                        <td>
                            <input type="text" name="email" placeholder="Email"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="submit-button">
                            <input type="submit" value="Register" />
                        </td>
                        <td class="error-message">
                            <%
                                String errorMessage = (String) session.getAttribute("errorMessage");
                                if(errorMessage != null){ %>
                            <%=errorMessage%>
                            <%
                                }
                            %>
                        </td>
                    </tr>
                    </tbody>
                </form>
            </table>
        </div>
        <jsp:include page="footer.jsp"/>
    </body>
</html>
