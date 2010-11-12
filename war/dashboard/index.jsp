<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%
  UserService userService = UserServiceFactory.getUserService();
%>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

    <link rel="stylesheet" href="../css/main.css" type="text/css" media="screen">
    <link rel="stylesheet" href="../css/lavalamp3.css" type="text/css" media="screen">
    <link rel="stylesheet" href="../css/coda-slider.css" type="text/css" media="screen" title="no title" charset="utf-8">
    <link rel="stylesheet" href="css/override.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/drawer.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/screensmall.css" type="text/css" media="screen">
  
    <script type="text/javascript" src="js/mootools.js"></script>
    <script type="text/javascript" src="js/moocheck.js"></script>
    
    <script src="../js/jquery-1.2.6.min.js" type="text/javascript"></script>
    
    <script type="text/javascript" src="../js/jquery.easing.1.3.js"></script>
    <script type="text/javascript" src="../js/jquery.lavalamp-1.3.4b2.js"></script>
    <script type="text/javascript" src="js/drawer.js"></script>

    <script type="text/javascript">
      jQuery.noConflict();
      jQuery(function() {
        jQuery("#lavaLampMenu").lavaLamp({fx: "swing", speed: 200});
      });
    </script>
  </head>
  <body>
    <div id="main">
      <div id="header-long">
        <div id="strip"></div>
        <div id="header">
          <div id="header-top">
            <div id="logo"><div id="bg"></div><h1><%= request.getUserPrincipal().getName() %></h1></div>
            <div id="menu">
              <ul class="lamp" id="lavaLampMenu">
                  <li><a href="">Dashboard</a></li>
                  <li><a href="">Settings</a></li>
                  <li><a href="">Help</a></li>
                  <li><a href="<%= userService.createLogoutURL("/") %>">Logout</a></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div id="body">
        <ul id="drw_tabs">
        	<li><a href="#calendar_div" rel="drw">Calendar</a></li>
        	<!--<li><a href="#calendar_changes" rel="drw">Suggested changes</a></li>-->
        </ul>
        <div id="drw"></div>
        <div id="calendar_div" class="sample">
        	<iframe class="calendar" src="https://www.google.com/calendar/embed?showTitle=0&amp;showPrint=0&amp;height=500&amp;wkst=2&amp;bgcolor=%23ffffff" style=" border-width:0 "></iframe>
          <div id="optimize_button">
            <a href="#">Optimise my life!</a>
          </div>
        </div>
      <div id="calendar_suggestions">
        <div class='demo'>
          <form action='' method='post' class='centre'> 
          	<ul> 
            	<li class='tip' title='Checkbox :: Again, easily styled to behave like a normal checkbox, but prettily.'> 
            		<strong>Suggestions:</strong> 
            		<label class='f_checkbox'><input type='checkbox' name='s1' checked='checked'>
            		  <p>Suggestion 1</p><p> aslkja klasjf klsdfjalskjdf asdlfkjaldfjalkfj slSuggestion 1lSuggestion 1lSuggestionestion fkjd d d  d f Suggestion 1Suggestion 1Suggestion 1Suggestion asdSuggestion 1 dalkfjasfl sldkfjasldfkj sldkfjaslkfj woeirumn 92034 09asdf aslfkjasdf sadflkj</p>
            		</label> 
            		<label class='f_checkbox'><input type='checkbox' name='s2' checked='checked'>Suggestiosn 2</label> 
            		<label class='f_checkbox'><input type='checkbox' name='s3' checked='checked'>Suggestion 3</label> 
            	</li> 
            	<li class='centre' style='margin-top:4px'> 
            		<input type='reset' value='Reset'> <input type='submit' name='submit' value='Submit Form'> 
            	</li> 
          </ul> 
          </form>
        </div>
      </div>
      </div>
      <div id="footer">
      </div>
    </div>
  </body> 
</html>