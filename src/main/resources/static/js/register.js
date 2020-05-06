/**
 * Front end logic for register page
 */

$(document).ready(() => {

  // This defines the necessary variables
  const inputButton = $("#registerButton");
  const username = $("#username");
  const password = $("#password");
  const name = $("#name");
  const message = $("#messageText");

  // Enters on enter keypress
  username.keypress(function (event) {
    if (event.which == 13) {
      event.preventDefault();
      inputButton.click();
    }
  });

  password.keypress(function (event) {
    if (event.which == 13) {
      event.preventDefault();
      inputButton.click();
    }
  });

  name.keypress(function (event) {
    if (event.which == 13) {
      event.preventDefault();
      inputButton.click();
    }
  });

  // When the user clicks the register button
  inputButton.click(function () {

    // Get the parameters
    const getParameters = {
      name: name.val(),
      username: username.val(),
      password: password.val()
    };

    $.post("/demeter/register", getParameters, response => {

      const parsed = JSON.parse(response).content;
      message.html(parsed);
      
    });
  });
});
