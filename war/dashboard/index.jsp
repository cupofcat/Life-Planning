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
  try {
    CalendarUtils.getCalendarUtils().setTokenFromReply(request.getQueryString());
  } catch (NullPointerException e) {
    //This is expected in most cases
  }

  String noTokenDiv = "<div id=\"calendar_div_toggle\"><a href=\"#\">Show calendar</a></div>";
  String calendars = "";

  try {
    Set<String> feeds = CalendarUtils.getCalendarUtils().getCalendarURLs();
    for(String url : feeds) {
      calendars += "&amp;src=" + url;
    }
  } catch (TokenException e) {
    noTokenDiv = "<div id=\"no_token\"><a href=\"" +
    CalendarUtils.getCalendarUtils().getCalendarAccessUrl(request.getRequestURL().toString()) +
    "\">Calendar not authorised! Please, follow this link.</a></div>";
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
    <link rel="stylesheet" href="css/suggestions.css" type="text/css" media="screen">
    
    <script src="../js/jquery-1.2.6.min.js" type="text/javascript"></script>
    
    <script type="text/javascript" src="../js/jquery.easing.1.3.js"></script>
    <script type="text/javascript" src="../js/jquery.lavalamp-1.3.4b2.js"></script>

    <script type="text/javascript">
      jQuery.noConflict();
      
      jQuery(document).ready(function($) {
        //Hide and slide calendar_div
        $("#calendar_div").hide();
        $("#calendar_div_toggle a").click(function(e){
          e.preventDefault();
          if ($("#calendar_div").is(":hidden")) {
            $("#calendar_div_toggle a").html("Hide calendar");
            $("#calendar_div").slideDown(500);
          }
          else {
            $("#calendar_div_toggle a").html("Show calendar");
            $("#calendar_div").slideUp(500);
          }
        });
        
        //Build suggestions
        $("#optimize_button a").click(function(e){
          e.preventDefault();
          $("#calendar_div_toggle a").click();
          $("#calendar_suggestions").html('<img src="css/ajax-loader.gif" />');
          $.getJSON("../suggestions", function(sugs){
            $container = $("#calendar_suggestions");
            $con_html = '<ul>';
            $.each(sugs, function(i, s){
              $con_html += '<li class="' + s.type + '"><div class="in_li">';
              $con_html += '<div class="left">';
              $con_html += '<div class=\"title\">' + s.title + '</div>';
              $con_html += '<div class=\"datetimes\">' + s.startDateTime + ' - ' + s.endDateTime + '</div>';
              $con_html += '<div class=\"description\">' + s.description + "Lorem ipsum dolor sit amet, consectetur adipisicing elit." + '</div>';
              //$con_html += '<div class=\"type\">' + s.type + '</div>';
              $con_html += '<div class=\"id\">' + s.id + '</div>';
              $con_html += '<div class=\"userId\">' + s.userID + '</div>';
              $con_html += '</div>';
              $con_html += '<div class="right"><ul class=\"spheres\">';
              $.each(s.spheres, function(j, sphere){
                $con_html += '<li class="' + sphere.toLowerCase() + '"><span>' + sphere + '</span></li>';
              })
              $con_html += "</ul></div></div></li>";
            })
            $con_html += "</ul>";
            $container.hide();
            $container.html($con_html);
            $container.slideDown(500);

            $('#calendar_suggestions > ul > li').addClass('checked').click(function(e){
              if ($(this).hasClass('checked')) {
                $(this).removeClass('checked');
                $(this).addClass('unchecked');
              } else {
                $(this).removeClass('unchecked');
                $(this).addClass('checked');
              }
            });

            $('<button id="send_suggestions">Apply suggestions</button>').click(function(){
              $user_id = "";
              $ids = [];
              $('#calendar_suggestions ul').children(".checked").each(function(i, suggestion){
                $user_id = $('div.userId', this).text();
                $ids.push($('div.id', this).text());
              });
              $answer = '{"userID:"' + $user_id + ', "suggestions":[' + $ids + ']}';
              alert($answer);
              //$.post("../suggestions", $answer);
            }).appendTo($container);
          });
        });

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
                  <li><a href="">Dashboard</a></li>
                  <li><a href="">Settings</a></li>
                  <li><a href="stats.jsp">Stats</a></li>
                  <li><a href="<%= userService.createLogoutURL("/") %>">Logout</a></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div id="body">
        <%= noTokenDiv %>
        <div id="calendar_div" class="sample">
        	<iframe class="calendar" src="https://www.google.com/calendar/embed?showTitle=0&amp;showPrint=0&amp;height=500&amp;wkst=2&amp;bgcolor=%23ffffff<%= calendars %>" style=" border-width:0 "></iframe>
          <div id="optimize_button">
            <a href="#">Optimise my life!</a>
          </div>
        </div>
        <br /><br />
      <div id="calendar_suggestions">
      </div>
      <br /><br />
      </div>
      <div id="footer">
      </div>
    </div>
  </body> 
</html>