package com.example.bazy2webapp;

import com.example.bazy2webapp.database.MongoDBController;
import com.example.bazy2webapp.database.user.PasswordEncrypter;
import com.example.bazy2webapp.database.user.User;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "changeDataServlet", value = "/change-data-servlet")
public class ChangeDataServlet extends HttpServlet{
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestDispatcher rd = request.getRequestDispatcher("change-data");
        List<String> whatToChangeList = new ArrayList<>();
        boolean allConditionsFullfilled = true;
        String message = "";

        //Pobieramy informacje o zalogowanym użytkowniku
        HttpSession session = request.getSession();
        String sessionUsername = (String) session.getAttribute("username");
        User sessionUser = MongoDBController.getUserByUsernameFromDatabase(sessionUsername);

        //Upewniamy się, że jesteśmy podłączeni do bazy
        MongoDBController.connectToDB();

        //Pobieramy informacje z formularza
        String newUsername = request.getParameter("username");
        String newPassword = request.getParameter("password");
        String newPasswordConfirmation = request.getParameter("password2");
        String newEmail = request.getParameter("email");

        //Wykonujemy poniższy kod, tylko jeśli mamy co najmniej jedno niepuste pole
        if(!newUsername.isEmpty() || !newPassword.isEmpty() || !newPasswordConfirmation.isEmpty() || !newEmail.isEmpty()) {
            //Zmiana nazwy użytkownika
            if (!newUsername.isEmpty()) {
                //Sprawdzamy czy użytkownik chce zmienić nazwę na inną od poprzedniej
                if (newUsername.equals(sessionUsername)) {
                    session.setAttribute("message", "New username must be different than current username!");
                    System.out.println(session.getAttribute("message"));
                    allConditionsFullfilled = false;
                    rd.forward(request, response);
                }
                //Sprawdzamy czy użytkownik wpisał co najmniej 3-literową nazwę
                else if (newUsername.length() < 3) {
                    session.setAttribute("message", "New username must be at least 3 characters long!");
                    allConditionsFullfilled = false;
                    rd.forward(request, response);
                }
                //Sprawdzamy czy użytkownik nie chce zmienić nazwy na taką która już istnieje
                else if(MongoDBController.doesUsernameExistsInDatabase(newUsername)){
                    session.setAttribute("message", "This username is taken!");
                    allConditionsFullfilled = false;
                    rd.forward(request, response);
                }
                //Nazwa wpisana w formularzu spełnia warunki, robimy update nazwy
                else {
                    whatToChangeList.add("username");
                    message += "Username\n";
                }
            }

            //Zmiana hasła
            //Sprawdzamy czy użytkownik nie zostawił pustych pól z hasłem jeśli chce je zmienić
            if(!newPassword.isEmpty() || !newPasswordConfirmation.isEmpty()) {
                if ((!newPassword.isEmpty() && newPasswordConfirmation.isEmpty()) ||
                        (newPassword.isEmpty() && !newPasswordConfirmation.isEmpty())) {
                    session.setAttribute("message", "In order to change password, both password fields must be filled!");
                    allConditionsFullfilled = false;
                    rd.forward(request, response);
                }
                //Oba pola z hasłem w formularzu zostały wypełnione
                else {
                    //Sprawdzamy czy oba wpisane hasła są takie same
                    if (!newPassword.equals(newPasswordConfirmation)) {
                        session.setAttribute("message", "Passwords don't match!");
                        allConditionsFullfilled = false;
                        rd.forward(request, response);
                    }
                    //Sprawdzamy czy obecne hasło użytkownika jest takie samo jak wpisane w formularzu
                    else {
                        try {
                            if(newPassword.length() < 6){
                                session.setAttribute("message", "New password must contain at least 6 characters!");
                                allConditionsFullfilled = false;
                                rd.forward(request, response);
                            }
                            else if (newPassword.equals(PasswordEncrypter.decrypt(sessionUser.getPassword(), sessionUsername))) {
                                session.setAttribute("message", "In order to change password, new password must be different than current!");
                                allConditionsFullfilled = false;
                                rd.forward(request, response);
                            }
                            //Oba hasła spełniły wymagania, robimy update hasła
                            else {
                                whatToChangeList.add("password");
                                message += "Password\n";
                            }
                        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //Zmiana maila
            if (!newEmail.isEmpty()) {
                //Sprawdzamy czy użytkownik chce zmienić nazwę na inną od poprzedniej
                if (newEmail.equals(sessionUser.getEmail())) {
                    session.setAttribute("message", "New email must be different than current email!");
                    allConditionsFullfilled = false;
                    rd.forward(request, response);
                }
                //Sprawdzamy czy użytkownik wpisał poprawny email
                else if (newEmail.length() < 6 || !newEmail.contains("@") || !newEmail.contains(".")) {
                    allConditionsFullfilled = false;
                    session.setAttribute("message", "Incorrect email!");
                    rd.forward(request, response);
                }
                //Email wpisany w formularzu spełnia warunki, robimy update nazwy
                else {
                    whatToChangeList.add("email");
                    message += "Email\n";
                }
            }
            //Wprowadzamy zmiany dla wybranych pól jeśli wszystkie warunki zostały spełnione
            if(allConditionsFullfilled){
                message += "changed successfully";
                session.setAttribute("message", message);
                if(whatToChangeList.contains("username")){
                    MongoDBController.updateUsersUsername(sessionUsername, newUsername, session);
                    sessionUsername = newUsername;
                }
                if(whatToChangeList.contains("password")){
                    MongoDBController.updateUsersPassword(sessionUsername, newPassword);
                }
                if(whatToChangeList.contains("email")){
                    MongoDBController.updateUsersEmail(sessionUsername, newEmail);
                }
                rd.forward(request, response);
            }
        }
        else{
            session.setAttribute("message", "Nothing to change");
            rd.forward(request, response);
        }
        allConditionsFullfilled = true;
    }
}

