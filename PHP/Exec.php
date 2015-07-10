<?php
     include_once('./DB.php');
     
     $host = 'mysql.hostinger.es';
     
     $user = 'u454686435_pro';
     
     $password = 'm46422618';
     
     $database = 'u454686435_pro';
     
     $db = new DB($host,$user,$password,$database);
     
     $connection = $db->connect();
     if(isset($_REQUEST['ccuser']))
     {
         $db->checkCommUser($connection,$_REQUEST['ccuser']);
     }
     else if(isset($_REQUEST['complace']))
     {
	 $db->checkComPlace($connection, $_REQUEST['complace']);
     }
     else if(isset($_REQUEST['user']) && isset($_REQUEST['password']))
     {
         $db->login($connection,$_REQUEST['user'],$_REQUEST['password']);
     }
     else if(isset($_REQUEST['suuser']) && isset($_REQUEST['rpassword']))
     {
         $db->userSignUp($connection,$_REQUEST['suuser'],$_REQUEST['supassword'],$_REQUEST['sumail'], $_REQUEST['usertype'], $_REQUEST['avatar']);
     }
     else if(isset($_REQUEST['cuser']) && isset($_REQUEST['cpassword']) && isset($_REQUEST['cpassword2']))
     {
         $db->changeUserPassword($connection,$_REQUEST['cuser'],$_REQUEST['cpassword'],$_REQUEST['cpassword2']);
     }
     else if(isset($_REQUEST['suser']) && isset($_REQUEST['sname']) && isset($_REQUEST['stown']) && isset($_REQUEST['sstreet']))
     {
         $db->createPlace($connection, $_REQUEST['suser'], $_REQUEST['sname'], $_REQUEST['stown'], $_REQUEST['scalle'], $_REQUEST['latitude'],$_REQUEST['longitude']);
     }
     else if(isset($_REQUEST['cuser']))
     {
	 $db->checkUser($connection,$_REQUEST['cuser']);
     }
     else if(isset($_REQUEST['cplaces']))
     {
	 $db->checkPlaces($connection);
     }
     else if(isset($_REQUEST['acuser']) && isset($_REQUEST['com']) && isset($_REQUEST['place']))
     {
	 $db->createComment($connection, $_REQUEST['acuser'], $_REQUEST['com'], $_REQUEST['place']);
     }
     else
     {
         echo "Faltan los paramentros de entrada user y password, como por ejemplo:
         guia-alemania.esy.es/test.php?user=pp&password=123";
     }
     
 ?>