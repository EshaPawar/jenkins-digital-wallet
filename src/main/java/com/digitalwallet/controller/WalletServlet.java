package com.digitalwallet.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WalletServlet extends HttpServlet {

    private static final double INITIAL_BALANCE = 10000.00;
    private static final double REFRESH_CREDIT = 100.00;

    private String getCurrentTime() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        String start = request.getParameter("start");
        String refresh = request.getParameter("refresh");
        String action = request.getParameter("action");

        /*
         * OPEN WALLET
         * start=true means this is a fresh wallet opening.
         * Reset the wallet to its initial state.
         */
        if ("true".equals(start)) {

            session.setAttribute("balance", INITIAL_BALANCE);
            session.setAttribute("refreshCount", 0);
            session.setAttribute("lastAddedAmount", 0.00);
            session.setAttribute("message", "");
            session.setAttribute("refreshedAt", getCurrentTime());
            session.setAttribute(
                    "latestActivity",
                    "Wallet opened successfully. No refresh performed yet."
            );
        }

        /*
         * If the wallet has never been initialized,
         * initialize it with refresh count 0.
         */
        if (session.getAttribute("balance") == null) {

            session.setAttribute("balance", INITIAL_BALANCE);
            session.setAttribute("refreshCount", 0);
            session.setAttribute("lastAddedAmount", 0.00);
            session.setAttribute("message", "");
            session.setAttribute("refreshedAt", getCurrentTime());
            session.setAttribute(
                    "latestActivity",
                    "Wallet opened successfully. No refresh performed yet."
            );
        }

        /*
         * REFRESH WALLET
         *
         * Every refresh:
         * 1. Adds ₹100
         * 2. Increases refresh count by 1
         * 3. Updates refresh time
         */
        if ("true".equals(refresh)) {

            double balance =
                    (Double) session.getAttribute("balance");

            int refreshCount =
                    (Integer) session.getAttribute("refreshCount");

            balance = balance + REFRESH_CREDIT;
            refreshCount = refreshCount + 1;

            session.setAttribute("balance", balance);
            session.setAttribute("refreshCount", refreshCount);
            session.setAttribute("lastAddedAmount", REFRESH_CREDIT);
            session.setAttribute("refreshedAt", getCurrentTime());

            session.setAttribute(
                    "latestActivity",
                    "₹100.00 demo credit added during the latest refresh."
            );

            session.setAttribute(
                    "message",
                    "Wallet refreshed successfully. ₹100.00 has been added."
            );
        }

        /*
         * ADD MONEY
         */
        if ("add".equals(action)) {

            String amountParameter =
                    request.getParameter("amount");

            try {

                double amount =
                        Double.parseDouble(amountParameter);

                if (amount > 0) {

                    double balance =
                            (Double) session.getAttribute("balance");

                    balance = balance + amount;

                    session.setAttribute("balance", balance);
                    session.setAttribute("lastAddedAmount", amount);
                    session.setAttribute("refreshedAt", getCurrentTime());

                    session.setAttribute(
                            "latestActivity",
                            String.format(
                                    "₹%.2f was manually added to the wallet.",
                                    amount
                            )
                    );

                    session.setAttribute(
                            "message",
                            String.format(
                                    "₹%.2f added successfully to your wallet.",
                                    amount
                            )
                    );

                } else {

                    session.setAttribute(
                            "message",
                            "Please enter an amount greater than ₹0."
                    );
                }

            } catch (NumberFormatException e) {

                session.setAttribute(
                        "message",
                        "Please enter a valid amount."
                );
            }
        }

        /*
         * Send session values to JSP.
         */
        request.setAttribute(
                "balance",
                session.getAttribute("balance")
        );

        request.setAttribute(
                "refreshCount",
                session.getAttribute("refreshCount")
        );

        request.setAttribute(
                "lastAddedAmount",
                session.getAttribute("lastAddedAmount")
        );

        request.setAttribute(
                "refreshedAt",
                session.getAttribute("refreshedAt")
        );

        request.setAttribute(
                "message",
                session.getAttribute("message")
        );

        request.setAttribute(
                "latestActivity",
                session.getAttribute("latestActivity")
        );

        /*
         * Open dashboard.
         */
        request.getRequestDispatcher(
                "/WEB-INF/views/dashboard.jsp"
        ).forward(request, response);
    }
}