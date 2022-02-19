<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>BikeRent</title>
        <style>
            <%@include file="/WEB-INF/style/main_site.css"%>
        </style>
    </head>

    <body>
        <jsp:include page="header.jsp"/>

        <%-- Ustawiamy errorMessage na null, żeby po powrocie do formularzy nie było komunikatu o błędzie --%>
        <%
            session.setAttribute("errorMessage", null);
            session.setAttribute("alphabeticalSortCounter", 0);
            session.setAttribute("wheelSizeSortCounter", 0);
        %>

        <%-- Banner strony głównej --%>
        <p class="banner"><i class="fa fa-bicycle" aria-hidden="true"></i>BikeRent</p>

        <%-- Dlaczego my? --%>
        <div class="main-container-first">
            <div class="why-us">
                <h2>Why us?</h2>
                <p>You don't need to pay for public transportation and it can save you many other expenses. Therefore, you can easily save the desired amount of money to spend on something better such as eating dinner. Not only this, it is really easy to ride the bike that you have rented so this would be really supportive for you.</p>
            </div>
        </div>
        <jsp:include page="footer.jsp"/>
    </body>
</html>