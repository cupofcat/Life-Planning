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
    
    <script src="../js/jquery-1.2.6.min.js" type="text/javascript"></script>
    
    <script type="text/javascript" src="../js/jquery.easing.1.3.js"></script>
    <script type="text/javascript" src="../js/jquery.lavalamp-1.3.4b2.js"></script>

    <script type="text/javascript">
      $(function() {
        $("#lavaLampMenu").lavaLamp({fx: "swing", speed: 200});
      });
    </script>
  </head>
  <body>
    <div id="main">
      <div id="header-long">
        <div id="strip"></div>
        <div id="header">
          <div id="header-top">
            <div id="logo"><h1><%= request.getUserPrincipal().getName() %></h1></div>
            <div id="menu">
              <ul class="lamp" id="lavaLampMenu">
                  <li><a href="#home">Dashoard</a></li>
                  <li><a href="#">Settings</a></li>
                  <li><a href="#step1">Help</a></li>
                  <li><a href="<%= userService.createLogoutURL("/") %>">Logout</a></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div id="body">
      </div>
      <div id="footer">
      </div>
    </div>
  </body> 
</html>