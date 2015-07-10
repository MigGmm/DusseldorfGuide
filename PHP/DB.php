<?php
    class db
    {
        var $str_host;
        var $str_user;
        var $str_password;
        var $str_database;

        function db($str_host,$str_user, $str_password, $str_database)
        {
            $this->str_host = $str_host;
            $this->str_user = $str_user;
            $this->str_password = $str_password;
            $this->str_database = $str_database;
        }
        
        function connect()
        {
            if($connection = mysqli_connect($this->str_host,$this->str_user,$this->str_password,$this->str_database))
            {
                return $connection;
            }
            else
            {
                die('Error al conectar con la base de datos '.$this->str_database.' con el usuario '.$this->str_user.' y la contraseña '.$this->str_password.' en el host '.$this->str_host);
            }
            
        }
        
        function login($connection, $str_user, $str_password)
        {
            $str_query = "SELECT * FROM users WHERE name='$str_user' AND password=PASSWORD('$str_password');";
            $result = mysqli_query($connection,$str_query);
            if(($result->num_rows) == 1)
            {
                $log = array();
                $log[] = array('logstatus'=>1);
                echo json_encode($log);
                mysqli_close($connection);
            }
            else
            {
                $log = array();
                $log[] = array('logstatus'=>0);
                echo json_encode($log);
                mysqli_close($connection);
            }
        }
        
        function userSignUp($connection, $str_name, $str_password, $str_email, $usertype, $avatar)
        {
            if($str_name != null && $str_name != null && $str_name != '' && $str_name != '')
            {                            
                
                $str_query = "INSERT INTO users VALUES('$str_name','$str_email',PASSWORD('$str_password'), '$usertype', '$avatar');";
                $result = mysqli_query($connection,$str_query);

                if($result != null)
                {
                    $insert = array();
                    $insert[] = array('insertstatus' => 1);
                }
                else
                {
                    $insert = array();
                    $insert[] = array('insertstatus' => 0);
                }
                echo json_encode($insert);
                mysqli_close($connection);
            }
            else
            {
                echo 'Debes introducir al menos un nombre y una contraseña de usuario.';
            }   
        }
        
        function changeUserPassword($connection,$str_user,$str_password_old,$str_password_new)
        {
            if($str_user != '' && $str_user != null && $str_password_old != '' && $str_password_old != null && $str_password_new != '' && $str_password_new != null)
            {
                $str_query = "SELECT * FROM users WHERE name='$str_user' AND password=PASSWORD('$str_password_old');";
                $result = mysqli_query($connection,$str_query);
                if(($result->num_rows) == 1)
                {
                    $str_query = "UPDATE users SET password=PASSWORD('$str_password_new') WHERE name='$str_user'";
                    $result = mysqli_query($connection,$str_query);
                    if($result != null)
                    {
                        $log = array();
                        $log[] = array('cambiarPasswordStatus'=>1);
                        echo json_encode($log);
                        mysqli_close($connection);
                    }
                    else
                    {
                        $log = array();
                        $log[] = array('cambiarPasswordStatus'=>0);
                        echo json_encode($log);
                        mysqli_close($connection);
                    }
                    
                }
                else
                {
                    $log = array();
                    $log[] = array('cambiarPasswordStatus'=>'noexist');
                    echo json_encode($log);
                    mysqli_close($connection);
                }
            }
        }
        
        function createPlace($connection, $str_user, $str_place_name, $str_town, $str_street, $latitude, $longitude)
        {
            $str_query = "SELECT * FROM users WHERE name='$str_user' AND tipo_user='premium';";
            $result = mysqli_query($connection,$str_query);
            if(($result->num_rows) == 1)
            {
                $str_query = "SELECT * FROM places WHERE name='$str_place_name';";
                $result = mysqli_query($connection,$str_query);
                if($result->num_rows == 0)
                {
                    //$int_foto = $int_foto != null? $int_foto : 0;
                    $str_query = "INSERT INTO places(name,town,street,administrator,latitude, longitude) VALUES('$str_place_name','$str_town','$str_street','$str_user','$latitude','$longitude');";
                    $result = mysqli_query($connection,$str_query);
                    if($result != null)
                    {
                        $insert = array();
                        $insert[] = array('insertstatus' => 1);
                    }
                    else
                    {
                        $insert = array();
                        $insert[] = array('insertstatus' => 0);
                    }
                    echo json_encode($insert);
                    mysqli_close($connection);
                }
                else
                {
                    $insert = array();
                    $insert[] = array('insertstatus' => 'exist');
                    echo json_encode($insert);
                    mysqli_close($connection);
                }
            }
            else
            {
                $insert = array();
                $insert[] = array('insertstatus' => 'nopremium');
                echo json_encode($insert);
                mysqli_close($connection);
            }
        }
        
        function createComment($connection, $str_user, $str_message, $int_place)
        {
            $str_query = "SELECT * FROM places WHERE id=$int_place;";
            $result = mysqli_query($connection,$str_query);
            if(($result->num_rows) == 1)
            {
                $str_query = "INSERT INTO comments (user,message,place) VALUES ('$str_user','$str_message',$int_place)";
                $result = mysqli_query($connection,$str_query);
                if($result != null)
                {
                    $insert = array();
                    $insert[] = array('insertstatus' => 1);
                }
                else
                {
                    $insert = array();
                    $insert[] = array('insertstatus' => 0);
                }
                echo json_encode($insert);
                mysqli_close($connection);
            }
            else
            {
                $insert = array();
                $insert[] = array('insertstatus' => 'noexist');
                echo json_encode($insert);
                mysqli_close($connection);
            }
        }
        
        function checkUser($connection,$str_user_name)
        {
            $str_query = "SELECT * FROM users WHERE name = '$str_user_name';";
            $result = mysqli_query($connection,$str_query);
            if(($result->num_rows) == 1)
	    {       
                $consulta = array();
                while($fila = mysqli_fetch_row($result))
                {
                    $consulta[] = array(
                        'nmae' => $fila[0]
                        ,'email' => $fila[1]
                        ,'password' => $fila[2]
                        ,'usertype' => $fila[3]
			,'avatar' => $fila[4]
                    );
                }
                echo json_encode($consulta,JSON_PRETTY_PRINT);
                mysqli_close($connection);
            }
	    else 
	    {
		print "Error al realizar la consulta.";
            }
            
        }

	function checkPlaces($connection)
        {
            	$str_query = "SELECT * FROM places;";
            	$result = mysqli_query($connection,$str_query);
            	if(($result->num_rows) > 0)
	    	{       
                	$consulta = array();
                	while($fila = mysqli_fetch_row($result))
                	{
                    		$consulta[] = array(
				'id' => $fila[0]
                        	,'name' => $fila[1]
                        	,'town' => $fila[2]
                        	,'street' => $fila[3]
                        	,'administrator' => $fila[4]
				,'latitude' => $fila[5]
				,'longitude' => $fila[6]				
                    		);
                	}
                	echo json_encode($consulta,JSON_PRETTY_PRINT);
                	mysqli_close($connection);
            	}
	    	else 
	    	{
			print "Error al realizar la consulta.";
            	}            
        }
        
        function checkCommUser($connection,$str_user_name)
        {
            $str_query = "SELECT * FROM comments WHERE user = '$str_user_name';";
            $result = mysqli_query($connection,$str_query);
            if(($result->num_rows) != 0)
            {
                $consulta = array();
                while($fila = mysqli_fetch_row($result))
                {

                    $consulta[] = array(
                        'id' => $fila[0]
                        ,'user' => $fila[1]
                        ,'message' => $fila[2] 
                        ,'place' => $fila[3] 
                        ,'date' => $fila[4]
                    );
                }
                echo json_encode($consulta,JSON_PRETTY_PRINT);
                mysqli_close($connection);
            }
            else
            {
                print "Error al realizar la consulta.";
            }
        }
        
        function checkComPlace($connection,$int_place)
        {
            $str_query = "SELECT * FROM comments WHERE place = '$int_place';";
            $result = mysqli_query($connection,$str_query);
            if(($result->num_rows) != 0)
            {
                $consulta = array();
                while($fila = mysqli_fetch_row($result))
                {

                    $consulta[] = array(
                        'id' => $fila[0]
                        ,'user' => $fila[1]
                        ,'message' => $fila[2] 
                        ,'place' => $fila[3] 
                        ,'date' => $fila[4]
                    );
                }
                echo json_encode($consulta,JSON_PRETTY_PRINT);
                mysqli_close($connection);
            }
            else
            {
                print "Error al realizar la consulta.";
            }
        }
    }
?>