package com.example.bazy2webapp;

import com.example.bazy2webapp.database.Bike;
import com.example.bazy2webapp.database.MongoDBController;
import com.example.bazy2webapp.database.user.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "deleteAccountServlet", value = "/delete-account-servlet")
public class DeleteAccountServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        RequestDispatcher rd = request.getRequestDispatcher("login");

        session.setAttribute("errorMessage", "Account deleted successfully");
        String sessionUsername = (String) session.getAttribute("username");

        //Usuwamy wszystkie 'rezerwacje rowerów' użytkownika
        List<Bike> usersBikeList = MongoDBController.getAllRentedBikesFromDatabase(sessionUsername);
        for(Bike bike : usersBikeList){
            MongoDBController.removeBikeUserRelationFromDatabase(String.valueOf(bike.get_id()));
            MongoDBController.updateBikeTakenField(bike.get_id(), false);
        }

        //Usuwamy użytkownika z bazy danych
        MongoDBController.deleteUserFromDatabase(sessionUsername);
        session.setAttribute("username", null);
        rd.forward(request, response);
    }
}
