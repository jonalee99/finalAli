
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

  $.post("/demeter/inventory", null, response => {
    const parsed = JSON.parse(response);
    
    for (i = 0; i < parsed.length; i++) {
      const name = parsed[i][0];
      const type = parsed[i][1];
      const quant = parsed[i][2];
      const units = parsed[i][3];
      const daystill = parsed[i][4]; 

      // Input the food name
      const td = this.document.createElement("td");
      td.className = "cell100 column1";
      td.innerHTML = name;

      const tree = this.document.createElement("tr");
      tree.className = "row100 body";
      tree.appendChild(td);

      firstColumn.appendChild(tree);

      // Input the other columns
      const typeTd = this.document.createElement("td");
      typeTd.className = "cell100 column2";
      typeTd.innerHTML = type;

      const quantTd = this.document.createElement("td");
      quantTd.className = "cell100 column3";
      quantTd.innerHTML = quant;
      
      const unitsTd = this.document.createElement("td");
      unitsTd.className = "cell100 column4";
      unitsTd.innerHTML = units;

      const daysTd = this.document.createElement("td");
      daysTd.className = "cell100 column5";
      daysTd.innerHTML = daystill;

      const restTr = this.document.createElement("tr");
      restTr.className = "row100 body";
      restTr.appendChild(typeTd);
      restTr.appendChild(quantTd);
      restTr.appendChild(unitsTd);
      restTr.appendChild(daysTd);

      restColumn.appendChild(restTr);
    }

    this.console.log(parsed);
  });
};