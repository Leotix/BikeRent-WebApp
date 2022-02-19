package com.example.bazy2webapp;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "logoutServlet", value = "/logout-servlet")
public class LogoutServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/");

        //Ustawiamy używane atrybuty sesji na null
        HttpSession session = request.getSession();
        session.setAttribute("username", null);
        session.setAttribute("errorMessage", null);

        //Odsyłamy użytkownika na strongę główną
        rd.forward(request, response);
    }
}