package com.example.bazy2webapp;

import com.example.bazy2webapp.database.MongoDBController;
import com.example.bazy2webapp.database.user.User;
import org.bson.types.ObjectId;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "giveBikeBackServlet", value = "/give-bike-back-servlet")
public class GiveBikeBackServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        RequestDispatcher rd = request.getRequestDispatcher("rentedbikes");

        //Pobieramy dane na temat użytkownika w sesji
        HttpSession session = request.getSession();
        User user = MongoDBController.getUserByUsernameFromDatabase((String)session.getAttribute("username"));

        //Pobieramy id roweru który użytkownik chce oddac
        String bikeIdStr = request.getParameter("id");
        ObjectId bikeId = new ObjectId(bikeIdStr);

        //Zmieniamy pole taken wypożyczonego roweru
        MongoDBController.updateBikeTakenField(bikeId, false);

        //Dodajemy relację użytkownika z wypożyczonym rowerem do kolekcji bike-user
        MongoDBController.removeBikeUserRelationFromDatabase(bikeIdStr);

        rd.forward(request, response);
    }
}
