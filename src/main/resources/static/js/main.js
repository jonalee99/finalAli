// This function is run on the load of the webpage
window.onload = function () {

  // Gets the first column
  const bar = document.getElementById("bar");

  $.post("/demeter/user", null, response => {
    const name = JSON.parse(response);

    const logout = this.document.createElement("button");
    logout.innerHTML = "Logout";
    logout.style = "outline: none !important; border: none; background: transparent; font-family: inherit"
    logout.addEventListener("click", function(){
      $.post("/demeter/logout");
      window.location.reload(true);
    });

    const div = this.document.createElement("div");
    div.innerHTML = "Welcome: " + name;
    div.id = "welcomeName";

    const welcome = this.document.createElement("li");
    welcome.style = "position: absolute; right: 0px"
    welcome.appendChild(div);
    welcome.appendChild(logout);

    bar.appendChild(welcome);
  });
  this.load();
};