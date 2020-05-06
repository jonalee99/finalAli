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
      <li class='active'><a href='/demeter/additem'>Add Item</a></li>
      <li><a href='/demeter/login'>Log In</a></li>
      <li><a href='/demeter/register'>Register</a></li>
    </ul>
  </div>

  <h1 class="user__title">Demeter</h1>
  <h2 class="caption">The Pantry Management App</h2>
</header>

  <div class="user">
    <h1 class="sign_in">Add Item</h1>

    <div class="form">
      <div class="form__group">
        <div class="dropdown">
          <button onclick="myFunction()" id="cat" class="dropbtn">Item Category</button>
          <div id="myDropdown" class="dropdown-content">
          </div>
        </div>
      </div>
      <div class="form__group">
        <div class="dropdown">
          <button onclick="myFunction1()" id="item" class="dropbtn">Item</button>
          <div id="myDropdown1" class="dropdown-content">
          </div>
        </div>
      </div>

      <div class="form__group">
        <input id="quantity" type="text" placeholder="Quantity" class="form__input"/>
      </div>

      <div class="form__group">
        <input id="expiration" type="text" placeholder="expiration <dd/MM/yyyy>" class="form__input"/>
      </div>

      <button id="additemButton" class="btn" type="button">Add Item</button>
    </div>
    <br />
    <header class="message__header">
      <h1 id="messageText" class="message"></h1>
    </header>

  </div>
  <script src="../js/jquery-2.1.1.js"></script>
  <script src="../js/additem.js"></script>
</body>

</html>
</#assign>
<#include "main.ftl">
