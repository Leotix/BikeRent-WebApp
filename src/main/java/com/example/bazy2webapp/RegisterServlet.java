package com.example.bazy2webapp;

import com.example.bazy2webapp.database.MongoDBController;
import java.io.*;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "registerServlet", value = "/register-servlet")
public class RegisterServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Upewniamy się, że jesteśmy podłączeni do bazy
        MongoDBController.connectToDB();

        RequestDispatcher rdBack = request.getRequestDispatcher("register");
        RequestDispatcher rdIndex = request.getRequestDispatcher("/");
        HttpSession session = request.getSession();

        //Zbieramy informacje o nowym użytkowniku z formularza rejestracji
        String newUsername = request.getParameter("username");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");
        String newEmail = request.getParameter("email");

        //Sprawdzamy zgodność haseł
        if(!password.equals(password2)){
            session.setAttribute("errorMessage", "Passwords don't match!");
            rdBack.forward(request, response);
        }
        //Sprawdzamy, czy wpisane hasło ma co najmniej 6 znaków długości
        else if(password.length() < 6){
            session.setAttribute("errorMessage", "Password must be at least 6 characters long!");
            rdBack.forward(request, response);
        }
        //Sprawdzamy, czy użytkownik nie zostawił pustych pól
        else if(newUsername.isEmpty() || newEmail.isEmpty()){
            session.setAttribute("errorMessage", "You can't leave empty fields!");
            rdBack.forward(request, response);
        }
        //Sprawdzamy czy wpisana nazwa użytkownika ma co najmniej 3 znaki długości
        else if(newUsername.length() < 3){
            session.setAttribute("errorMessage", "Username must be at least 3 characters long!");
            rdBack.forward(request, response);
        }
        //Sprawdzamy poprawność wpisanego emaila
        else if(newEmail.length() < 6 || !newEmail.contains("@") || !newEmail.contains(".")){
            session.setAttribute("errorMessage", "Incorrect email!");
            rdBack.forward(request, response);
        }
        else {
            try {
                /* Sprawdzamy, czy użytkownik nie chce użyć istniejącej
                nazwy użytkownika lub maila, który jest już w użyciu */
                List<String> allUsernames = MongoDBController.getAllUsernamesFromDatabase();
                List<String> allEmails = MongoDBController.getAllEmailsFromDatabase();
                if(allUsernames.contains(newUsername)){
                    session.setAttribute("errorMessage", "This username is already in use!");
                    rdBack.forward(request, response);
                }
                else if(allEmails.contains(newEmail)){
                    session.setAttribute("errorMessage", "This email is already in use!");
                    rdBack.forward(request, response);
                }
                //Użytkownik spełnił wszystkie warunki rejestracji, dodajemy do bazy danych
                else {
                    MongoDBController.addUserToDatabase(newUsername, password, newEmail);
                    session.setAttribute("username", newUsername);
                    session.setAttribute("errorMessage", null);
                    rdIndex.forward(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}