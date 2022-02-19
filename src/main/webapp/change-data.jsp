<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <style>
            <%@include file="/WEB-INF/style/profile.css"%>
            <%@include file="/WEB-INF/style/form_change-data.css"%>
        </style>
        <title>Change data</title>
    </head>
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
    <body>
        <%-- Nagłówek i menu boczne --%>
        <jsp:include page="profile_bars.jsp"/>

        <div class="wrapper">
            <table class="content-table">
                <form action="change-data-servlet" autocomplete="off" method="post">
                    <tbody>
                    <tr>
                        <td>
                            <label>New username</label>
                        </td>
                        <td>
                            <input type="text" name="username" placeholder="New username"/>
                        </td>

                    </tr>

                    <tr>
                        <td>
                            <label>New password</label>
                        </td>
                        <td>
                            <input type="password" name="password" placeholder="New password"/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <label>Confirm new password</label>
                        </td>
                        <td>
                            <input type="password" name="password2" placeholder="Confirm new password"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label>New email</label>
                        </td>
                        <td>
                            <input type="text" name="email" placeholder="New email"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="submit-button">
                            <input type="submit" value="Update" />
                        </td>
                        <td class="message">
                            <%
                                String message = (String) session.getAttribute("message");
                                if(message != null){ %>
                            <%=message%>
                            <%
                                }
                            %>
                        </td>
                    </tr>
                    </tbody>
                </form>
            </table>
            <table>
                <tr>
                    <form action="delete-account-servlet" method="post">
                        <td class="submit-button">
                            <input type="submit" value="Delete account" />
                        </td>
                    </form>
                </tr>
            </table>
        </div>

        <jsp:include page="footer.jsp"/>
        <% session.setAttribute("message", null); %>
    </body>
</html>
