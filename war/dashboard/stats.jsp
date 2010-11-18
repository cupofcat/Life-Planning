<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.appspot.iclifeplanning.authentication.CalendarUtils" %>
<%@ page import="com.appspot.iclifeplanning.authentication.TokenException" %>

<%@ page import="java.lang.NullPointerException" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.net.URL" %>

<%
  UserService userService = UserServiceFactory.getUserService();
  //TODO: check if the token is there first:
  String setTokenDiv = "<h1>Setting ok</h1>";
  try {
    CalendarUtils.getCalendarUtils().setTokenFromReply(request.getQueryString());
  } catch (NullPointerException e) {
    setTokenDiv = "e.toString()";
  }
  String noTokenDiv = "<br />";
  String calendarsDiv = "<hr />";

  try {
    Set<URL> feeds = CalendarUtils.getCalendarUtils().getCalendarURLs();
    calendarsDiv = "<div><ul>";
    for(URL url : feeds) {
      calendarsDiv += "<li>" + url.toString() + "</li>";
    }
    calendarsDiv +="</ul></div>";
  } catch (TokenException e) {
    noTokenDiv = "<div>No token, go to <a href=\"" +
    CalendarUtils.getCalendarUtils().getCalendarAccessUrl(request.getRequestURL().toString()) +
    "\">link</a>!</div>";
  } catch (IOException e) {
    
  }
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
	
	<!--  Highcharts libraries: -->
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
	<script type="text/javascript" src="js/highcharts.js"></script>
	<script type="text/javascript" src="js/modules/exporting.js"></script>

    <script type="text/javascript">
      //jQuery.noConflict();
      jQuery(function() {
        jQuery("#lavaLampMenu").lavaLamp({fx: "swing", speed: 200});
      });
    </script>
	
	<script type="text/javascript" src="js/highcharts-options.js"></script>
	
	
	<!-- Loads historic chart -->
	<script type="text/javascript" src="js/chosen-priorities.js"></script>
	<script type="text/javascript" src="js/spheres-history.js"></script>
	<script type="text/javascript">
	var sphereOfInterest = {"sphere": "Russia"};
	</script>
	<script type="text/javascript" src="js/plan-achievement.js"></script>
	
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
	  
       <div id="priorities-chart" style="width: 800px; height: 400px; margin: 0 auto"></div>
	   <div id="historicChart" style="width: 800px; height: 400px; margin: 0 auto"></div>
	   <div id="plan-achievement" style="width: 800px; height: 400px; margin: 0 auto"></div>
	   
      </div>
      <div id="footer">
      </div>
    </div>
  </body> 
</html>