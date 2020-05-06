<#assign content>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>Table V05</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
<!--===============================================================================================-->	
	<link rel="icon" type="image/png" href="../images/icons/favicon.ico"/>
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="../vendor/bootstrap/css/bootstrap.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="../fonts/font-awesome-4.7.0/css/font-awesome.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="../vendor/animate/animate.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="../vendor/select2/select2.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="../vendor/perfect-scrollbar/perfect-scrollbar.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="../css/util.css">
  <link rel="stylesheet" type="text/css" href="../css/inventory.css">
  <link rel="stylesheet" href="../css/register.css">
<!--===============================================================================================-->
</head>
<body>

  

	<div class="limiter">
		<header class="user__header">
      <div id='cssmenu'>
        <ul id='bar'>
          <li class='active'><a href='/demeter/home'>Expiry Sort</a></li>
          <li><a href='/demeter/recipe'>Category Sort</a></li>
          <li><a href='/demeter/inventory'>Inventory</a></li>
          <li><a href='/demeter/additem'>Add Item</a></li>
          <li><a href='/demeter/login'>Log In</a></li>
          <li><a href='/demeter/register'>Register</a></li>
        </ul>
      </div>


    <h1 class="user__title">Expiry Sort</h1>
    <br>
      <h2 class="caption">These recipes use foods that are expiring soon</h2>

		</header>


		<div class="container-table100">
			<div class="wrap-table100">
        <div class="user1">
        </div>
				<div class="table100 ver1">
					<div class="table100-firstcol">
						<table>
							<thead>
								<tr class="row100 head">
									<th class="cell100 column1">Recipe</th>
								</tr>
							</thead>
							<tbody id="firstColumn">
							</tbody>
						</table>
					</div>
					
					<div class="wrap-table100-nextcols js-pscroll">
						<div class="table100-nextcols">
							<table>
								<thead>
									<tr class="row100 head">
                    <th class="cell100 column1">Description</th>
									</tr>
								</thead>
								<tbody id="otherColumns">
								</tbody>
							</table>
						</div>
					</div>
        </div>
			</div>
		</div>
  </div>
  


<!--===============================================================================================-->	
	<script src="../vendor/jquery/jquery-3.2.1.min.js"></script>
<!--===============================================================================================-->
	<script src="../vendor/bootstrap/js/popper.js"></script>
	<script src="../vendor/bootstrap/js/bootstrap.min.js"></script>
<!--===============================================================================================-->
	<script src="../vendor/select2/select2.min.js"></script>
<!--===============================================================================================-->
	<script src="../vendor/perfect-scrollbar/perfect-scrollbar.min.js"></script>
	<script>
		$('.js-pscroll').each(function(){
			var ps = new PerfectScrollbar(this);

			$(window).on('resize', function(){
				ps.update();
			})

			$(this).on('ps-x-reach-start', function(){
				$(this).parent().find('.table100-firstcol').removeClass('shadow-table100-firstcol');
			});

			$(this).on('ps-scroll-x', function(){
				$(this).parent().find('.table100-firstcol').addClass('shadow-table100-firstcol');
			});

		});

		
		
		
	</script>
<!--===============================================================================================-->
	<script src="../js/home.js"></script>

</body>
</html>
</#assign>
<#include "main.ftl">