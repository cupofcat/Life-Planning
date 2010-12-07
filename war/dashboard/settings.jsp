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
%>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

    <link rel="stylesheet" href="../css/main.css" type="text/css" media="screen">
    <link rel="stylesheet" href="../css/lavalamp3.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/override.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/suggestions.css" type="text/css" media="screen">
    
    <script src="../js/jquery-1.2.6.min.js" type="text/javascript"></script>
    
    <script type="text/javascript" src="../js/jquery.easing.1.3.js"></script>
    <script type="text/javascript" src="../js/jquery.lavalamp-1.3.4b2.js"></script> 

    <script type="text/javascript">
      jQuery(document).ready(function($) {
        $('button').click(function() {
          $answer = '{userID:"' + '<%= request.getUserPrincipal().getName() %>", ';
          $answer += 'fullOpt:"';
          if ($('input:checkbox').get(0).value == 'on') {
            $answer += 'TRUE';
          } else {
            $answer += 'FALSE';
          }/**/
          $answer += '", ';
          $answer += 'spheresSettings:[';
          $texts = $('input:text');
          $texts.each(function(i, inp){
            $answer += '{name:"' + inp.name + '", value:"' + $(this).val() + '"}';
            if (i != $texts.length - 1) {
              $answer += ',';
            }
          });
          $answer += ']}';
          alert($answer);
          $.post("settings", $answer);
        })
        //Lavalamp
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
                  <li><a href="index.jsp">Dashboard</a></li>
                  <li><a href="#">Settings</a></li>
                  <li><a href="stats.jsp">Stats</a></li>
                  <li><a href="<%= userService.createLogoutURL("/") %>">Logout</a></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div id="body">
        Work: <input type="text" name="work" size="2" maxlength="2" />%<br />
        Health: <input type="text" name="health" size="2" maxlength="2" />%<br />
        Family: <input type="text" name="family" size="2" maxlength="2" />%<br />
        Recreation: <input type="text" name="recreation" size="2" maxlength="2" />%<br />
        Full optimisation: <input type="checkbox" name="gull" checked="checked">
        <button>Submit</button>
      </div>
      <div id="footer">
      </div>
    </div>
  </body> 
</html>