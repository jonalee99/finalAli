<#assign content>
<!DOCTYPE html>
<html lang="en">

<head>
  <title>Demeter: Register</title>
  <link rel="stylesheet" href="../css/register.css">
</head>

<body>
<header class="user__header">

  <div id='cssmenu'>
    <ul id='bar'>      <li><a href='/demeter/home'>Expiry Sort</a></li>
      <li><a href='/demeter/recipe'>Category Sort</a></li>
      <li><a href='/demeter/inventory'>Inventory</a></li>
      <li><a href='/demeter/additem'>Add Item</a></li>
      <li><a href='/demeter/login'>Log In</a></li>
      <li class='active'><a href='/demeter/register'>Register</a></li>
    </ul>
  </div>


  <h1 class="user__title">Demeter</h1>
  <h2 class="caption">The Pantry Management App</h2>
</header>

  <div class="user">

    <h1 class="sign_in">Register</h1>

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