
package com.digitalwallet.controller;

import com.digitalwallet.dao.WalletDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RegisterServlet extends HttpServlet {

    private final WalletDAO walletDAO = new WalletDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/register.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");
        String password = request.getParameter("password");

        // Validate input fields
        if (name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || mobile == null || mobile.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            request.setAttribute(
                    "error",
                    "Please fill in all fields."
            );

            request.getRequestDispatcher("/register.jsp")
                    .forward(request, response);

            return;
        }

        try {

            // Register user in database
            int userId = walletDAO.registerUser(
                    name.trim(),
                    email.trim(),
                    mobile.trim(),
                    password
            );

            if (userId > 0) {

                /*
                 * Registration successful.
                 * Redirect user to login page.
                 */
                response.sendRedirect(
                        request.getContextPath()
                                + "/login"
                                + "?registered=true"
                );

            } else {

                request.setAttribute(
                        "error",
                        "Registration failed. Please try again."
                );

                request.getRequestDispatcher("/register.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {

            // Print the complete error in the Tomcat console
            e.printStackTrace();

            /*
             * Show the actual database error on the registration page.
             * This is temporary and is being used to identify
             * the reason registration is failing.
             */
            String errorMessage =
                    "Registration failed: " + e.getMessage();

            /*
             * Handle duplicate email separately.
             */
            if (e.getMessage() != null
                    && e.getMessage()
                       .toLowerCase()
                       .contains("duplicate")) {

                errorMessage =
                        "An account with this email already exists.";
            }

            request.setAttribute(
                    "error",
                    errorMessage
            );

            request.getRequestDispatcher(
                    "/register.jsp"
            ).forward(request, response);
        }
    }
}
