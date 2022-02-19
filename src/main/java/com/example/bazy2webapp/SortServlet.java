package com.example.bazy2webapp;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "sortServlet", value = "/sort-servlet")
public class SortServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("bikes");
        HttpSession session = request.getSession();

        //Pobieramy informacje jaki typ sortowania wybrał użytkownik
        String typeOfSort = request.getParameter("sort");
        session.setAttribute("typeOfSort", typeOfSort);

        rd.forward(request, response);
    }
}
