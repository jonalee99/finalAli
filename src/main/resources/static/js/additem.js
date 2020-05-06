
(function ($) {
  "use strict";
  $('.column100').on('mouseover', function () {
    var table1 = $(this).parent().parent().parent();
    var table2 = $(this).parent().parent();
    var verTable = $(table1).data('vertable') + "";
    var column = $(this).data('column') + "";

    $(table2).find("." + column).addClass('hov-column-' + verTable);
    $(table1).find(".row100.head ." + column).addClass('hov-column-head-' + verTable);
  });

  $('.column100').on('mouseout', function () {
    var table1 = $(this).parent().parent().parent();
    var table2 = $(this).parent().parent();
    var verTable = $(table1).data('vertable') + "";
    var column = $(this).data('column') + "";

    $(table2).find("." + column).removeClass('hov-column-' + verTable);
    $(table1).find(".row100.head ." + column).removeClass('hov-column-head-' + verTable);
  });


})(jQuery);

// This function is run on the load of the webpage
window.onload = function () {

  // Gets the first column
  const dropdowns = document.getElementById("myDropdown");
  const dropdowns1 = document.getElementById("myDropdown1");

  $.post("/demeter/foodCategory", null, response => {
    const parsed = JSON.parse(response);
    this.console.log(parsed);

    for (i = 0; i < parsed.length; i++) {

      // Input the recipe name
      const link = this.document.createElement("a");
      link.innerHTML = parsed[i];
      this.console.log(parsed[i]);
      link.addEventListener("click", function () {

        $.post("/demeter/food/" + link.innerHTML, null, response => {

          const parsed1 = JSON.parse(response);
          console.log(parsed1);
          dropdowns1.innerHTML = "";
          document.getElementById("cat").innerHTML = this.innerHTML;
          document.getElementById("item").innerHTML = "Item";
          document.getElementById("quantity").placeholder = "Quantity";

          for (i = 0; i < parsed1.length; i++) {

            const link1 = document.createElement("a");
            link1.innerHTML = parsed1[i];
            console.log(parsed1[i]);

            link1.addEventListener("click", function () {
              $.post("/demeter/units/" + link1.innerHTML, null, response => {

                const parsed1 = JSON.parse(response);
                console.log(parsed1);// Close the dropdown if the user clicks outside of it
                window.onclick = function (event) {
                  if (!event.target.matches('.dropbtn')) {
                    var dropdowns = document.getElementsByClassName("dropdown-content");
                    var i;
                    for (i = 0; i < dropdowns.length; i++) {
                      var openDropdown = dropdowns[i];
                      if (openDropdown.classList.contains('show')) {
                        openDropdown.classList.remove('show');
                      }
                    }
                  }
                }
                document.getElementById("quantity").placeholder = parsed1;
                document.getElementById("item").innerHTML = this.innerHTML;
              });
            });
            dropdowns1.appendChild(link1);
          };
        });
      });
      dropdowns.appendChild(link);
    }
  });
};

/* When the user clicks on the button, 
toggle between hiding and showing the dropdown content */
function myFunction() {
  document.getElementById("myDropdown").classList.toggle("show");
}

/* When the user clicks on the button,
toggle between hiding and showing the dropdown content */
function myFunction1() {
  document.getElementById("myDropdown1").classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
window.onclick = function (event) {
  if (!event.target.matches('.dropbtn')) {
    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');
      }
    }
  }
}

document.getElementById("additemButton").addEventListener("click", function () {

  // Get the parameters
  const getParameters = {
    name: document.getElementById("item").innerHTML,
    quantity: document.getElementById("quantity").value,
    units: document.getElementById("quantity").placeholder,
    expiration: document.getElementById("expiration").value,
    type: document.getElementById("cat").innerHTML
  };
  console.log(getParameters);

  $.post("/demeter/add", getParameters, response => {

    const parsed1 = JSON.parse(response);
    console.log(parsed1);// Close the dropdown if the user clicks outside of it
    document.getElementById("messageText").innerHTML = parsed1;
  });
});

$(document).ready(() => {
  
  // This defines the necessary variables
  const inputButton = $("#additemButton");
  const username = $("#quantity");
  const password = $("#expiration");

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
});
