<%@ page import="com.example.bazy2webapp.database.MongoDBController" %>
<%@ page import="com.example.bazy2webapp.database.Bike" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>All bikes</title>
        <style>
            <%@include file="/WEB-INF/style/bikes.css"%>
        </style>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.1/dist/js/bootstrap.bundle.min.js"></script>
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
        List<Bike> allBikes = null;
        //Pobieramy informacje o sortowaniu
        String typeOfSort = (String) session.getAttribute("typeOfSort");
        if(typeOfSort == null) {
            //Pobieramy wszystkie rowery z bazy do wyświetlenia
            allBikes = MongoDBController.getAllBikesFromDatabase();
        }
        else {
            allBikes = MongoDBController.getSortedBikeList(typeOfSort, session);
        }

        //Sprawdzamy ile rowerów jest dostępnych
        int countNotTaken = 0;
        for(Bike bike : allBikes){
            if(!bike.isTaken()) countNotTaken++;
        }
    %>
    <body>
        <jsp:include page="header.jsp"/>
        <% if(countNotTaken == 0){%>
            <h2 class="no-bikes">No bikes are available at the moment...</h2>
        <%}%>
        <div class="sort-options">
            <p>Filters</p>
            <a href="sort-servlet?sort=alphabetical" alt="Alphabetical"><i class="fa fa-sort-alpha-asc" aria-hidden="true"></i></a>
            <a href="sort-servlet?sort=wheel_size" alt="Wheel Size"><i class="fa fa-sort-numeric-asc" aria-hidden="true"></i></a>
        </div>
        <div class="container pb-5 mb-sm-1">
            <div class="row">
                <%-- Wyświetlanie wszystkich rowerów --%>
                <%for(Bike bike : allBikes){
                        if(!bike.isTaken()){%>
                            <div class="col-md-4 col-sm-6">
                                <div class="card border-0 mb-grid-gutter">
                                    <a class="card-img-tiles">
                                        <div class="main-img"><img src=<%=bike.getImg_url()%>></div>
                                    </a>
                                    <div class="card-body border mt-n1 py-4 text-center">
                                        <h2 class="h5 mb-1">
                                            <%=bike.getBrand()%> <%=bike.getModel()%>
                                        </h2>
                                        <span class="d-block mb-3 font-size-xs text-muted">
                                            Wheel size: <%=bike.getWheel_size()%><br>
                                            <span class="font-weight-semibold">Type: <%=bike.getType()%>
                                            </span>
                                        </span>
                                        <% String link = "rent-bike-servlet?id="+bike.get_id(); %>
                                        <a class="btn btn-pill btn-outline-primary btn-sm" href=<%=link%>>Rent</a>
                                    </div>
                                </div>
                            </div>
                <%      }
                    }%>
            </div>
        </div>
        <jsp:include page="footer.jsp"/>
    </body>
</html>
