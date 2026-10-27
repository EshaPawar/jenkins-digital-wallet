<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
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
            width: 400px;
            background: white;
            padding: 35px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
        }

        h1 {
            text-align: center;
            color: #333;
        }

        .subtitle {
            text-align: center;
            color: #777;
            margin-bottom: 25px;
        }

        input {
            width: 100%;
            box-sizing: border-box;
            padding: 13px;
            margin: 8px 0 15px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 15px;
        }

        button {
            width: 100%;
            padding: 14px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            cursor: pointer;
        }

        button:hover {
            background: #5568d8;
        }

        .login {
            text-align: center;
            margin-top: 20px;
        }

        .login a {
            color: #667eea;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="container">

    <h1>Digital Wallet</h1>

    <div class="subtitle">
        Create your wallet account
    </div>

    <form action="register" method="post">

        <label>Name</label>
        <input type="text"
               name="name"
               placeholder="Enter your name"
               required>

        <label>Email</label>
        <input type="email"
               name="email"
               placeholder="Enter your email"
               required>

        <label>Mobile</label>
        <input type="tel"
               name="mobile"
               placeholder="Enter your mobile number"
               required>

        <label>Password</label>
        <input type="password"
               name="password"
               placeholder="Create a password"
               required>

        <button type="submit">
            Register
        </button>

    </form>

    <div class="login">
        Already have an account?
        <a href="login.jsp">Sign In</a>
    </div>

</div>

</body>
</html>