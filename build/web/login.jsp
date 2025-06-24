<%-- 
    Document   : login
    Created on : Jun 4, 2025, 10:38:28 AM
    Author     : Warudo
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Login - PT. MUHAMAD SALWARUD</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5f5;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .login-container {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            width: 320px;
        }
        h2 {
            color: #006400;
            margin-bottom: 1.5rem;
            text-align: center;
        }
        label {
            display: block;
            margin-bottom: 0.5rem;
            color: #228B22;
        }
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 0.6rem;
            margin-bottom: 1rem;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        button {
            width: 100%;
            background-color: #228B22;
            color: white;
            padding: 0.7rem;
            border: none;
            border-radius: 5px;
            font-weight: bold;
            cursor: pointer;
        }
        button:hover {
            background-color: #006400;
        }
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 0.8rem;
            border-radius: 5px;
            margin-bottom: 1rem;
            text-align: center;
        }
    </style>
</head>
<body>

<div class="login-container">
    <h2>LOGIN <br> PT MUHAMAD SALWARUD</h2>

<!-- ... -->

    <c:if test="${errorMessage != null and not empty errorMessage}">
    <div class="error-message">${errorMessage}</div>
    </c:if>


    <form method="post" action="LoginController">
        <label for="user">Username</label>
        <input type="text" id="user" name="user" required autofocus />

        <label for="pass">Password</label>
        <input type="password" id="pass" name="pass" required />

        <button type="submit">Login</button>
    </form>
</div>

</body>
</html>

