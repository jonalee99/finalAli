
(function ($) {
	"use strict";
	$('.column100').on('mouseover',function(){
		var table1 = $(this).parent().parent().parent();
		var table2 = $(this).parent().parent();
		var verTable = $(table1).data('vertable')+"";
		var column = $(this).data('column') + ""; 

		$(table2).find("."+column).addClass('hov-column-'+ verTable);
		$(table1).find(".row100.head ."+column).addClass('hov-column-head-'+ verTable);
	});

	$('.column100').on('mouseout',function(){
		var table1 = $(this).parent().parent().parent();
		var table2 = $(this).parent().parent();
		var verTable = $(table1).data('vertable')+"";
		var column = $(this).data('column') + ""; 

		$(table2).find("."+column).removeClass('hov-column-'+ verTable);
		$(table1).find(".row100.head ."+column).removeClass('hov-column-head-'+ verTable);
	});
    

})(jQuery);

// This function is run on the load of the webpage
window.onload = function() {

  // Gets the first column
  const firstColumn = document.getElementById("firstColumn");
  const restColumn = document.getElementById("otherColumns")

  $.post("/demeter/home", null, response => {
    const parsed = JSON.parse(response);
    
    for (i = 0; i < parsed.length; i++) {
      const name = parsed[i][0];
      const type = parsed[i][1];
      const web = parsed[i][2];


      // Input the recipe name
      const link = this.document.createElement("a");
      link.innerHTML = name;
      link.href = web;

      const td = this.document.createElement("td");
      td.className = "cell100 column1";
      td.appendChild(link);

      const tree = this.document.createElement("tr");
      tree.className = "row100 body";
      tree.appendChild(td);

      firstColumn.appendChild(tree);

      // Input the other columns
      const typeTd = this.document.createElement("td");
      typeTd.className = "cell100 column2";
      typeTd.innerHTML = type;

      const restTr = this.document.createElement("tr");
      restTr.className = "row100 body";
      restTr.appendChild(typeTd);

      restColumn.appendChild(restTr);
    }

    this.console.log(parsed);
  });
};

/* When the user clicks on the button, 
toggle between hiding and showing the dropdown content */
function myFunction() {
  document.getElementById("myDropdown").classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
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