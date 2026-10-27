
package com.digitalwallet.controller;

import com.digitalwallet.dao.WalletDAO;
import com.digitalwallet.dao.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

    private final WalletDAO walletDAO = new WalletDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            request.setAttribute(
                    "error",
                    "Please enter email and password."
            );

            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);

            return;
        }

        try {

            int userId = walletDAO.loginUser(
                    email.trim(),
                    password
            );

            if (userId > 0) {

                /*
                 * Create a session for the logged-in user.
                 */
                HttpSession session =
                        request.getSession(true);

                /*
                 * Save user ID in session.
                 */
                session.setAttribute(
                        "userId",
                        userId
                );

                /*
                 * Load user information from database.
                 */
                User user =
                        walletDAO.getUser(userId);

                if (user == null) {

                    session.invalidate();

                    request.setAttribute(
                            "error",
                            "Unable to load your account."
                    );

                    request.getRequestDispatcher(
                            "/login.jsp"
                    ).forward(request, response);

                    return;
                }

                /*
                 * Save user details in session.
                 */
                session.setAttribute(
                        "accountHolder",
                        user.getName()
                );

                session.setAttribute(
                        "email",
                        user.getEmail()
                );

                session.setAttribute(
                        "mobile",
                        user.getMobile()
                );

                session.setAttribute(
                        "balance",
                        user.getBalance()
                );

                /*
                 * Redirect to wallet.
                 */
                response.sendRedirect(
                        request.getContextPath()
                                + "/wallet"
                );

            } else {

                request.setAttribute(
                        "error",
                        "Invalid email or password."
                );

                request.getRequestDispatcher(
                        "/login.jsp"
                ).forward(request, response);
            }

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute(
                    "error",
                    "Unable to connect to the database."
            );

            request.getRequestDispatcher(
                    "/login.jsp"
            ).forward(request, response);
        }
    }
}
