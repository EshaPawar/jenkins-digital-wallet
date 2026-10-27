jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.digitalwallet.dao.Transaction" %>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>Digital Wallet Dashboard</title>

    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        rel="stylesheet">

</head>


<body class="bg-light">


<div class="container mt-5">


    <!-- Header -->

    <div class="text-center mb-4">

        <h1>
            Digital Wallet Demonstration Platform
        </h1>

        <p class="text-muted">
            Secure and simple digital wallet management
        </p>

    </div>


    <!-- Wallet Card -->

    <div class="card shadow mx-auto"
         style="max-width: 750px;">

        <div class="card-body p-4">


            <h3 class="card-title mb-4">
                Wallet Dashboard
            </h3>


            <!-- Logged-in User Information -->

            <div class="card border-primary mb-4">

                <div class="card-body">

                    <h5 class="text-primary mb-3">
                        Account Information
                    </h5>


                    <div class="row">

                        <div class="col-md-6">

                            <p>
                                <strong>Name:</strong><br>
                                ${accountHolder}
                            </p>


                            <p>
                                <strong>Email:</strong><br>
                                ${email}
                            </p>

                        </div>


                        <div class="col-md-6">

                            <p>
                                <strong>Mobile:</strong><br>
                                ${mobile}
                            </p>


                            <p>
                                <strong>Account ID:</strong><br>
                                ${userId}
                            </p>

                        </div>

                    </div>

                </div>

            </div>


            <!-- Wallet Balance -->

            <div class="card border-success mb-4">

                <div class="card-body text-center">

                    <h5 class="text-success">
                        Current Wallet Balance
                    </h5>


                    <h1 class="text-success">
                        ₹${balance}
                    </h1>


                    <p class="text-muted mb-0">
                        Available wallet balance
                    </p>

                </div>

            </div>


            <!-- Success / Error Message -->

            <%
                Object messageObj =
                        request.getAttribute("message");

                if (messageObj != null &&
                    !messageObj.toString().isEmpty()) {
            %>

                <div class="alert alert-info">

                    <strong>
                        ${message}
                    </strong>

                </div>

            <%
                }
            %>


            <!-- Add Money -->

            <div class="card bg-light border mb-4">

                <div class="card-body">

                    <h5 class="mb-3">
                        Add Money
                    </h5>


                    <form method="post"
                          action="${pageContext.request.contextPath}/wallet">


                        <input type="hidden"
                               name="action"
                               value="add">


                        <div class="mb-3">

                            <label for="amount"
                                   class="form-label">

                                Enter Amount

                            </label>


                            <div class="input-group">

                                <span class="input-group-text">
                                    ₹
                                </span>


                                <input type="number"
                                       class="form-control"
                                       id="amount"
                                       name="amount"
                                       min="1"
                                       max="50000"
                                       step="0.01"
                                       placeholder="Enter amount"
                                       required>

                            </div>


                            <small class="text-muted">

                                Maximum amount allowed: ₹50,000

                            </small>

                        </div>


                        <button type="submit"
                                class="btn btn-success w-100">

                            + Add Money

                        </button>


                    </form>

                </div>

            </div>


            <!-- Refresh Wallet -->

            <div class="card border-primary mb-4">

                <div class="card-body text-center">

                    <h5 class="text-primary">
                        Refresh Wallet
                    </h5>


                    <p class="text-muted">

                        Refresh your wallet and receive
                        a ₹100 demo credit.

                    </p>


                    <form method="post"
                          action="${pageContext.request.contextPath}/wallet">

                        <input type="hidden"
                               name="action"
                               value="refresh">


                        <button type="submit"
                                class="btn btn-primary">

                            🔄 Refresh Wallet

                        </button>

                    </form>

                </div>

            </div>


            <!-- Last Added Amount -->

            <%
                Object lastAddedObj =
                        request.getAttribute("lastAddedAmount");

                if (lastAddedObj != null) {

                    double lastAdded =
                            ((Number) lastAddedObj).doubleValue();

                    if (lastAdded > 0) {
            %>

                <div class="alert alert-warning">

                    <strong>
                        Last Amount Added:
                    </strong>

                    ₹${lastAddedAmount}

                </div>

            <%
                    }
                }
            %>


            <!-- Latest Wallet Activity -->

            <div class="alert alert-primary">

                <strong>
                    Latest Wallet Activity
                </strong>

                <br>

                ${latestActivity}

            </div>


            <!-- Transaction History -->

            <div class="card border mb-4">

                <div class="card-body">

                    <h5 class="mb-3">
                        Transaction History
                    </h5>


                    <%

                        Object transactionsObj =
                                request.getAttribute("transactions");

                        if (transactionsObj != null) {

                            List<Transaction> transactions =
                                    (List<Transaction>) transactionsObj;


                            if (!transactions.isEmpty()) {

                    %>

                        <div class="list-group">

                            <%

                                for (Transaction transaction :
                                        transactions) {

                            %>

                                <div class="list-group-item">

                                    <div class="d-flex justify-content-between">

                                        <strong>
                                            <%= transaction.getType() %>
                                        </strong>


                                        <span class="text-success">

                                            ₹<%= String.format(
                                                    "%.2f",
                                                    transaction.getAmount()
                                               ) %>

                                        </span>

                                    </div>


                                    <small class="text-muted">

                                        Recipient:
                                        <%= transaction.getRecipient() %>

                                        <br>

                                        Status:
                                        <%= transaction.getStatus() %>

                                        <br>

                                        Date:
                                        <%= transaction.getTransactionDate() %>

                                    </small>

                                </div>


                            <%

                                }

                            %>

                        </div>


                    <%

                            } else {

                    %>

                        <p class="text-muted mb-0">
                            No transactions yet.
                        </p>


                    <%

                            }

                        } else {

                    %>

                        <p class="text-muted mb-0">
                            No transactions yet.
                        </p>


                    <%

                        }

                    %>


                </div>

            </div>


            <!-- Wallet Status / Receipt -->

            <div class="card border-success mb-4">

                <div class="card-body text-center">

                    <h5 class="text-success mb-2">

                        Wallet Status / Receipt

                    </h5>


                    <p class="text-muted mb-3">

                        Download your current wallet status,
                        account details, balance and
                        transaction history.

                    </p>


                    <a href="${pageContext.request.contextPath}/wallet?action=downloadReceipt"
                       class="btn btn-success btn-lg">

                        Download Wallet Receipt

                    </a>

                </div>

            </div>


            <!-- Bottom Buttons -->

            <div class="d-grid gap-2">


                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-outline-secondary">

                    ← Back to Home

                </a>


            </div>


        </div>

    </div>


</div>


</body>

</html>
