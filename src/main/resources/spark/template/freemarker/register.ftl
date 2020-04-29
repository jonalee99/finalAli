<#assign content>
<!DOCTYPE html>
<html lang="en">

<head>
  <title>Demeter: Register</title>
  <link rel="stylesheet" href="../css/register.css">
</head>

<body>

  <div id='cssmenu'>
    <ul>
      <li><a href='/demeter/home'>Home</a></li>
      <li><a href='/demeter/inventory'>Inventory</a></li>
      <li><a href='/demeter/recipe'>Recipes</a></li>
      <li><a href='/demeter/login'>Log In</a></li>
      <li class='active'><a href='/demeter/register'>Register</a></li>
    </ul>
  </div>

  <div class="user">
    <header class="user__header">
      <img src="../photo/cook.svg" alt="" />
      <h1 class="user__title">Welcome to Demeter</h1>
    </header>

    <form class="form">
      <div class="form__group">
        <input id="name" type="text" placeholder="Full Name" class="form__input" />
      </div>
      <div class="form__group">
        <input id="username" type="text" placeholder="Username" class="form__input" />
      </div>
      <div class="form__group">
        <input id="password" type="password" placeholder="Password" class="form__input" />
      </div>

      <button id="registerButton" class="btn" type="button">Register</button>
    </form>
    <br />
    <header class="message__header">
      <h1 id="messageText"class="message"></h1>
    </header>

  </div>
  <script src="../js/jquery-2.1.1.js"></script>
  <script src="../js/register.js"></script>
</body>

</html>
</#assign>
<#include "main.ftl">