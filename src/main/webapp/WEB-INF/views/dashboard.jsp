<%@ page contentType="text/html;charset=UTF-8" %>

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


            <!-- Account Information -->

            <div class="row">

                <div class="col-md-6">

                    <p>
                        <strong>Account Holder:</strong><br>
                        Demo User
                    </p>

                    <p>
                        <strong>Account Number:</strong><br>
                        DW10001
                    </p>

                </div>


                <div class="col-md-6">

                    <p>
                        <strong>Wallet Balance:</strong>
                    </p>

                    <h2 class="text-success">
                        ₹${balance}
                    </h2>

                </div>

            </div>


            <hr>


            <!-- Wallet Status -->

            <div class="alert alert-success">

                <strong>Wallet Status:</strong>
                Active

                <br>

                <strong>Last Refreshed:</strong>
                ${refreshedAt}

                <br>

                <strong>Wallet Refresh Count:</strong>
                ${refreshCount}

            </div>


            <!-- Latest Wallet Activity -->

            <div class="alert alert-info">

                <strong>Latest Wallet Activity</strong>

                <br>

                ${latestActivity}

            </div>


            <!-- Success / Error Message -->

            <% if (message != null && !message.isEmpty()) { %>

                <div class="alert alert-warning">

                    ${message}

                </div>

            <% } %>


            <!-- ADD MONEY -->

            <div class="card bg-light border mb-4">

                <div class="card-body">

                    <h5 class="mb-3">
                        Add Money
                    </h5>

                    <form
                        method="get"
                        action="${pageContext.request.contextPath}/wallet">

                        <input
                            type="hidden"
                            name="action"
                            value="add">

                        <div class="mb-3">

                            <label
                                for="amount"
                                class="form-label">

                                Enter Amount

                            </label>

                            <div class="input-group">

                                <span class="input-group-text">
                                    ₹
                                </span>

                                <input
                                    type="number"
                                    class="form-control"
                                    id="amount"
                                    name="amount"
                                    min="1"
                                    step="0.01"
                                    placeholder="Enter amount"
                                    required>

                            </div>

                        </div>


                        <button
                            type="submit"
                            class="btn btn-success w-100">

                            + Add Money

                        </button>

                    </form>

                </div>

            </div>


            <!-- Last Added Amount -->

            <% if (lastAddedAmount > 0) { %>

                <div class="alert alert-warning">

                    <strong>Last Amount Added:</strong>

                    ₹${lastAddedAmount}

                </div>

            <% } %>


            <!-- Buttons -->

            <div class="d-grid gap-2">


                <!-- Refresh -->

                <a
                    href="${pageContext.request.contextPath}/wallet?refresh=true"
                    class="btn btn-primary">

                    🔄 Refresh Wallet

                </a>


                <!-- Back -->

                <a
                    href="${pageContext.request.contextPath}/"
                    class="btn btn-outline-secondary">

                    ← Back to Home

                </a>

            </div>


        </div>

    </div>


</div>


</body>

</html>