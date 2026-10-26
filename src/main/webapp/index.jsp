<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <title>Digital Wallet</title>

    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        rel="stylesheet">
</head>

<body class="bg-light">

<div class="container mt-5">

    <div class="card shadow-sm mx-auto text-center"
         style="max-width: 700px;">

        <div class="card-body p-5">

            <h1 class="mb-3">
                Digital Wallet Demonstration Platform
            </h1>

            <p class="text-muted mb-4">
                Secure and simple digital wallet management
            </p>

            <a href="${pageContext.request.contextPath}/wallet?start=true"
               class="btn btn-primary btn-lg">

                Open Wallet

            </a>

        </div>

    </div>

</div>

</body>
</html>