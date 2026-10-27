<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">

    <title>Digital Wallet - Register</title>

    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: linear-gradient(135deg, #667eea, #764ba2);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            width: 380px;
            background: white;
            padding: 35px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
        }

        h1 {
            text-align: center;
            margin-bottom: 25px;
        }

        label {
            display: block;
            margin-top: 12px;
            font-weight: bold;
        }

        input {
            width: 100%;
            box-sizing: border-box;
            padding: 13px;
            margin-top: 7px;
            border: 1px solid #ddd;
            border-radius: 8px;
        }

        button {
            width: 100%;
            padding: 14px;
            margin-top: 20px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 16px;
        }

        button:hover {
            background: #5568d8;
        }

        .login {
            text-align: center;
            margin-top: 20px;
        }

        a {
            color: #667eea;
            text-decoration: none;
        }

        .error {
            background: #f8d7da;
            color: #842029;
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
        }
    </style>

</head>

<body>

<div class="container">

    <h1>Create Account</h1>

    <% if (request.getAttribute("error") != null) { %>

        <div class="error">
            <%= request.getAttribute("error") %>
        </div>

    <% } %>

    <form action="${pageContext.request.contextPath}/register"
          method="post">

        <label for="name">Full Name</label>

        <input type="text"
               id="name"
               name="name"
               placeholder="Enter your full name"
               maxlength="100"
               required>


        <label for="email">Email</label>

        <input type="email"
               id="email"
               name="email"
               placeholder="Enter your email"
               required>


        <label for="mobile">Mobile Number</label>

        <input type="text"
               id="mobile"
               name="mobile"
               placeholder="Enter your mobile number"
               maxlength="15"
               required>


        <label for="password">Password</label>

        <input type="password"
               id="password"
               name="password"
               placeholder="Create a password"
               minlength="4"
               required>


        <button type="submit">
            Register
        </button>

    </form>


    <div class="login">

        Already have an account?

        <a href="${pageContext.request.contextPath}/login">
            Sign In
        </a>

    </div>

</div>

</body>

</html>