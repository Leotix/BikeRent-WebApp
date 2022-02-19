package com.example.bazy2webapp;

import com.example.bazy2webapp.database.MongoDBController;
import com.example.bazy2webapp.database.user.PasswordEncrypter;
import com.example.bazy2webapp.database.user.User;

import java.io.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "loginServlet", value = "/login-servlet")
public class LoginServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Upewniamy się, że jesteśmy podłączeni do bazy
        MongoDBController.connectToDB();

        RequestDispatcher rdBack = request.getRequestDispatcher("login");
        RequestDispatcher rdIndex = request.getRequestDispatcher("/");
        HttpSession session = request.getSession();

        //Pobieramy informacje wpisane przez użytkownika w formularzu logowania
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        //Sprawdzamy czy użytkownik nie zostawił pustego pola
        if(username.isEmpty() || password.isEmpty()){
            session.setAttribute("errorMessage", "You can't leave empty fields!");
            rdBack.forward(request, response);
        }
        else{
            try{
                //Sprawdzamy czy w bazie danych jest użytkownik o podanej nazwie
                User user = MongoDBController.getUserByUsernameFromDatabase(username);
                if(user == null){
                    session.setAttribute("errorMessage", "Can't find user with such username!");
                    rdBack.forward(request, response);
                }
                //Znaleziono użytkownika o podanej nazwie w bazie danych
                else{

                    //Podane hasło nie zgadza się z podaną nazwą użytkownika
                    String encryptedPassword = PasswordEncrypter.encrypt(password, username);
                    if(!user.getPassword().equals(encryptedPassword)){
                        System.out.println();
                        session.setAttribute("errorMessage", "Incorrect password!");
                        rdBack.forward(request, response);
                    }
                    //Użytkownik spełnił wszystkie warunki logowania
                    else{
                        session.setAttribute("username", username);
                        session.setAttribute("errorMessage", null);
                        rdIndex.forward(request, response);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}