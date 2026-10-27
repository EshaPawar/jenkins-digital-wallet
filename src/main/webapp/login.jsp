<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>

    <title>Digital Wallet - Sign In</title>

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
        }

        input {
            width: 100%;
            box-sizing: border-box;
            padding: 13px;
            margin: 8px 0 15px;
            border: 1px solid #ddd;
            border-radius: 8px;
        }

        button {
            width: 100%;
            padding: 14px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
        }

        .register {
            text-align: center;
            margin-top: 20px;
        }

        a {
            color: #667eea;
        }
    </style>

</head>

<body>

<div class="container">

    <h1>Sign In</h1>

    <form action="login" method="post">

        <label>Email</label>

        <input type="email"
               name="email"
               required>

        <label>Password</label>

        <input type="password"
               name="password"
               required>

        <button type="submit">
            Sign In
        </button>

    </form>

    <div class="register">
        Don't have an account?
        <a href="index.jsp">Register</a>
    </div>

</div>

</body>
</html>