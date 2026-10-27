package com.digitalwallet.controller;

import com.digitalwallet.dao.Transaction;
import com.digitalwallet.dao.User;
import com.digitalwallet.dao.WalletDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class WalletServlet extends HttpServlet {

    private static final double REFRESH_CREDIT = 100.00;
    private static final double MAX_ADD_AMOUNT = 50000.00;

    private final WalletDAO walletDAO = new WalletDAO();


    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        /*
         * User must be logged in.
         */
        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );

            return;
        }

        int userId =
                (Integer) session.getAttribute("userId");

        String action =
                request.getParameter("action");

        String refresh =
                request.getParameter("refresh");


        try {

            /*
             * REFRESH WALLET
             *
             * Add ₹100 to MySQL.
             */
            if ("true".equals(refresh)) {

                boolean success =
                        walletDAO.addMoney(
                                userId,
                                REFRESH_CREDIT
                        );

                if (success) {

                    session.setAttribute(
                            "message",
                            "Wallet refreshed successfully. ₹100.00 has been added."
                    );

                    session.setAttribute(
                            "latestActivity",
                            "₹100.00 demo credit added during the latest refresh."
                    );

                    session.setAttribute(
                            "lastAddedAmount",
                            REFRESH_CREDIT
                    );

                } else {

                    session.setAttribute(
                            "message",
                            "Unable to refresh wallet."
                    );
                }
            }


            /*
             * DOWNLOAD RECEIPT
             */
            if ("downloadReceipt".equals(action)) {

                User user =
                        walletDAO.getUser(userId);

                if (user == null) {

                    session.invalidate();

                    response.sendRedirect(
                            request.getContextPath() + "/login"
                    );

                    return;
                }


                List<Transaction> transactions =
                        walletDAO.getTransactions(userId);


                response.setContentType(
                        "text/plain; charset=UTF-8"
                );


                response.setHeader(
                        "Content-Disposition",
                        "attachment; filename=\"digital-wallet-receipt.txt\""
                );


                PrintWriter out =
                        response.getWriter();


                out.println(
                        "========================================"
                );

                out.println(
                        "       DIGITAL WALLET RECEIPT"
                );

                out.println(
                        "========================================"
                );

                out.println();


                out.println(
                        "Account Holder : "
                                + user.getName()
                );

                out.println(
                        "Email          : "
                                + user.getEmail()
                );

                out.println(
                        "Mobile         : "
                                + user.getMobile()
                );

                out.println(
                        "User ID        : "
                                + user.getId()
                );

                out.println(
                        "Current Balance: ₹"
                                + String.format(
                                        "%.2f",
                                        user.getBalance()
                                )
                );


                out.println();

                out.println(
                        "----------------------------------------"
                );

                out.println(
                        "TRANSACTION HISTORY"
                );

                out.println(
                        "----------------------------------------"
                );


                if (transactions != null &&
                        !transactions.isEmpty()) {

                    for (Transaction transaction :
                            transactions) {

                        out.println(
                                "Transaction ID : "
                                        + transaction.getId()
                        );

                        out.println(
                                "Type           : "
                                        + transaction.getType()
                        );

                        out.println(
                                "Recipient      : "
                                        + transaction.getRecipient()
                        );

                        out.println(
                                "Amount         : ₹"
                                        + String.format(
                                                "%.2f",
                                                transaction.getAmount()
                                        )
                        );

                        out.println(
                                "Status         : "
                                        + transaction.getStatus()
                        );

                        out.println(
                                "Date           : "
                                        + transaction.getTransactionDate()
                        );

                        out.println(
                                "----------------------------------------"
                        );
                    }

                } else {

                    out.println(
                            "No transactions available."
                    );
                }


                out.println();

                out.println(
                        "========================================"
                );

                out.println(
                        "          END OF RECEIPT"
                );

                out.println(
                        "========================================"
                );

                out.flush();

                return;
            }


            /*
             * Load latest user data from MySQL.
             */
            User user =
                    walletDAO.getUser(userId);


            if (user == null) {

                session.invalidate();

                response.sendRedirect(
                        request.getContextPath() + "/login"
                );

                return;
            }


            /*
             * Load latest transactions from MySQL.
             */
            List<Transaction> transactions =
                    walletDAO.getTransactions(userId);


            /*
             * Save latest user information
             * in the session.
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

            session.setAttribute(
                    "transactions",
                    transactions
            );


            /*
             * Send data to dashboard.jsp.
             */
            request.setAttribute(
                    "userId",
                    user.getId()
            );

            request.setAttribute(
                    "accountHolder",
                    user.getName()
            );

            request.setAttribute(
                    "email",
                    user.getEmail()
            );

            request.setAttribute(
                    "mobile",
                    user.getMobile()
            );

            request.setAttribute(
                    "balance",
                    user.getBalance()
            );

            request.setAttribute(
                    "transactions",
                    transactions
            );


            request.setAttribute(
                    "message",
                    session.getAttribute("message")
            );


            request.setAttribute(
                    "latestActivity",
                    session.getAttribute("latestActivity")
            );


            request.setAttribute(
                    "lastAddedAmount",
                    session.getAttribute("lastAddedAmount")
            );


            /*
             * Display-only refresh count.
             */
            Integer refreshCount =
                    (Integer) session.getAttribute(
                            "refreshCount"
                    );


            if (refreshCount == null) {

                refreshCount = 0;

                session.setAttribute(
                        "refreshCount",
                        refreshCount
                );
            }


            request.setAttribute(
                    "refreshCount",
                    refreshCount
            );


            /*
             * Open dashboard.
             */
            request.getRequestDispatcher(
                    "/WEB-INF/views/dashboard.jsp"
            ).forward(request, response);


        } catch (Exception e) {

            e.printStackTrace();


            request.setAttribute(
                    "message",
                    "Database error: " + e.getMessage()
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/dashboard.jsp"
            ).forward(request, response);
        }
    }


    /*
     * ==========================================
     * POST
     * ==========================================
     *
     * Used by:
     * - Add Money
     * - Refresh Wallet
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);


        /*
         * User must be logged in.
         */
        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );

            return;
        }


        int userId =
                (Integer) session.getAttribute("userId");


        String action =
                request.getParameter("action");


        try {

            /*
             * ======================================
             * ADD MONEY
             * ======================================
             */
            if ("add".equals(action)) {

                String amountParameter =
                        request.getParameter("amount");


                if (amountParameter == null ||
                        amountParameter.trim().isEmpty()) {

                    session.setAttribute(
                            "message",
                            "Please enter an amount."
                    );

                } else {

                    try {

                        double amount =
                                Double.parseDouble(
                                        amountParameter
                                );


                        if (amount <= 0) {

                            session.setAttribute(
                                    "message",
                                    "Please enter an amount greater than ₹0."
                            );

                        } else if (amount > MAX_ADD_AMOUNT) {

                            session.setAttribute(
                                    "message",
                                    "Maximum amount allowed in one transaction is ₹50,000."
                            );

                        } else if (
                                Math.abs(
                                        amount * 100
                                                - Math.round(amount * 100)
                                ) > 0.000001
                        ) {

                            /*
                             * Amount validation:
                             * Maximum 2 decimal places.
                             */
                            session.setAttribute(
                                    "message",
                                    "Please enter an amount with a maximum of 2 decimal places."
                            );

                        } else {

                            /*
                             * Update MySQL users.balance
                             * and insert into transactions.
                             */
                            boolean success =
                                    walletDAO.addMoney(
                                            userId,
                                            amount
                                    );


                            if (success) {

                                session.setAttribute(
                                        "message",
                                        String.format(
                                                "₹%.2f added successfully to your wallet.",
                                                amount
                                        )
                                );


                                session.setAttribute(
                                        "latestActivity",
                                        String.format(
                                                "₹%.2f was added to the wallet.",
                                                amount
                                        )
                                );


                                session.setAttribute(
                                        "lastAddedAmount",
                                        amount
                                );

                            } else {

                                session.setAttribute(
                                        "message",
                                        "Unable to add money."
                                );
                            }
                        }


                    } catch (NumberFormatException e) {

                        session.setAttribute(
                                "message",
                                "Please enter a valid amount."
                        );
                    }
                }
            }


            /*
             * ======================================
             * REFRESH WALLET
             * ======================================
             */
            else if ("refresh".equals(action)) {

                boolean success =
                        walletDAO.addMoney(
                                userId,
                                REFRESH_CREDIT
                        );


                if (success) {

                    session.setAttribute(
                            "message",
                            "Wallet refreshed successfully. ₹100.00 has been added."
                    );


                    session.setAttribute(
                            "latestActivity",
                            "₹100.00 demo credit added during the latest refresh."
                    );


                    session.setAttribute(
                            "lastAddedAmount",
                            REFRESH_CREDIT
                    );


                    Integer refreshCount =
                            (Integer) session.getAttribute(
                                    "refreshCount"
                            );


                    if (refreshCount == null) {
                        refreshCount = 0;
                    }


                    refreshCount++;


                    session.setAttribute(
                            "refreshCount",
                            refreshCount
                    );


                } else {

                    session.setAttribute(
                            "message",
                            "Unable to refresh wallet."
                    );
                }
            }


            /*
             * Invalid action.
             */
            else {

                session.setAttribute(
                        "message",
                        "Invalid wallet action."
                );
            }


            /*
             * Redirect back to dashboard.
             */
            response.sendRedirect(
                    request.getContextPath() + "/wallet"
            );


        } catch (Exception e) {

            e.printStackTrace();


            session.setAttribute(
                    "message",
                    "Database error: " + e.getMessage()
            );


            response.sendRedirect(
                    request.getContextPath() + "/wallet"
            );
        }
    }
}