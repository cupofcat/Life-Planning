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
  CalendarUtils.setUpIfNewUser();
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
    <link rel="stylesheet" href="css/co da-slider.css" type="text/css" media="screen" title="no title" charset="utf-8">
    <link rel="stylesheet" href="css/override.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/suggestions.css" type="text/css" media="screen">
    
    <script src="../js/jquery-1.2.6.min.js" type="text/javascript"></script>
    
    <script type="text/javascript" src="../js/jquery.easing.1.3.js"></script>
    <script type="text/javascript" src="../js/jquery.lavalamp-1.3.4b2.js"></script>

    <script src="../js/jquery.scrollTo-1.4.2-min.js" type="text/javascript"></script>
    <script src="../js/jquery.localscroll-1.2.7-min.js" type="text/javascript" charset="utf-8"></script>
    <script src="../js/jquery.serialScroll-1.2.2-min.js" type="text/javascript" charset="utf-8"></script>    
    <script src="../js/c oda-slider.js" type="text/javascript" charset="utf-8"></script>

    <script type="text/javascript">

      //jQuery.noConflict();
      
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
        });/**/
        
        //Build suggestions
        $("#optimize_button a").click(function(e){
          e.preventDefault();
          $("#calendar_div_toggle a").click();
          $("#calendar_suggestions").html('<img src="css/ajax-loader.gif" />');
          //$.getJSON("../dummy-suggestions-ok.json", function(optimisation){
          $.getJSON("../suggestions", function(optimisation){
            $container = $("#calendar_suggestions");
            $container.html('');
            $sugg_nav_div = $('<div id="sugg_nav"></div>').appendTo($container);
            
            $.each(optimisation.lists, function(sugg_set_id, sugg_set) {
              $sugg_set_div = $('<div class="sugg_set" id="sugg_set' + sugg_set_id + '"></div>').appendTo($container);
              $sugg_set_div.append('<div class="id">' + sugg_set_id + '</div>');
              $.each(sugg_set, function(alter_set_id, alter_set) {
                $alter_set_div = $('<div class="alter_set"></div>').appendTo($sugg_set_div);
                $alter_set_id_div = $('<div class="id">' + alter_set_id + '</div>').appendTo($alter_set_div);
                $.each(alter_set, function(alter_id, alter) {
                  $selection_div = $('<div></div>').appendTo($alter_set_div);
                  $type_div = $('<div class="' + alter.type + '"></div>').appendTo($selection_div);
                  $alter_div = $('<div class="alter"></div>').appendTo($type_div);
                  $alter_back_div = $('<div class="alter_back"></div>').appendTo($type_div);
                  $alter_back_strip_div = $('<div class="alter_back_strip"></div>').appendTo($type_div);
                  $w = $alter_set_div.width() / alter_set.length - 5;
                  $selection_div.width($w);
                  if (alter_id == 0) {
                    $selection_div.addClass('selected');
                  } else {
                    $selection_div.addClass('unselected');
                  }
                  $alter_back_div.width($w - 20);
                  $selection_div.click(function() {
                    if ($(this).hasClass('selected')) {
                      $(this).removeClass('selected');
                      $(this).addClass('unselected');
                    } else {
                      $('.selected', $(this).parent()).each(function(i) {
                        $(this).removeClass('selected');
                        $(this).addClass('unselected');
                      });
                      $(this).removeClass('unselected');
                      $(this).addClass('selected');
                    }
                  });
                  $alter_id_div = $('<div class="id">' + alter_id + '</div>').appendTo($alter_div);
                  $alter_div_left = $('<div class="left"></div>').appendTo($alter_div);
                  $alter_div_right = $('<div class="right"></div>').appendTo($alter_div);
                  $alter_div_left.append('<div class="title">' + alter.title + '</div>');
                  if (alter.type == 'Reschedule') {
                    $alter_div_left.append('<div class="datetimes">From: ' + alter.startDateTime + ' - ' + alter.endDateTime + '<br />To: ' + alter.newStartDateTime + ' - ' + alter.newEndDateTime +'</div>');
                  } else {
                    $alter_div_left.append('<div class="datetimes">' + alter.startDateTime + ' - ' + alter.endDateTime + '</div>');
                  }
                  $alter_div_left.append('<div class="description">' + alter.description + '</div>');
                  $spheres_ul = $('<ul class="spheres"></ul>').appendTo($type_div/*$alter_div_right*/);
                  $.each(alter.spheres, function(sphere_id, sphere) {
                    $spheres_ul.append('<li class="' + sphere.toLowerCase() + '"><span>' + sphere + '</span></li>');
                  }); //spheres
                }); //alter_set
              }); //sugg_set
              $apply_button = $('<button class="apply_suggs">Apply suggestions</button>').appendTo($sugg_set_div);
              $apply_button.click(function() {
                $choices = [];
                $('div.alter_set', $('div.sugg_set').get(sugg_set_id)).each(function(alter_set_id, alter_set) {
                  $choice = '[';
                  $('div.selected', this).each(function(alter_id, alter) {
                    $choice += alter_set_id + ', ';
                    $choice += $('.id', this).text();
                  });
                  $choice += ']';
                  if ($choice != '[]') {
                    $choices.push($choice);
                  }
                });
                $answer = '{"userID":"' + optimisation.userID + '", "setID":"' + sugg_set_id + '", "suggestions":[' + $choices + ']}';
                //alert($answer);
                $.post("../suggestions", $answer);
                $container.html('');
              }); //apply_button.click()
              
              //Add navigation tab
              $sugg_nav_a = $('<a href="#sugg_set' + sugg_set_id + '">Set ' + sugg_set_id + '</a>').appendTo($sugg_nav_div);
              if (sugg_set_id == 0) {
                $sugg_nav_a.addClass('selected');
              } else {
                $sugg_nav_a.addClass('unselected');
                $sugg_set_div.hide();
              }
              $sugg_nav_a.click(function() {
                $('#calendar_suggestions > div.sugg_set').hide();
                $('.selected', $(this).parent()).each(function(i) {
                  $(this).removeClass('selected');
                  $(this).addClass('unselected');
                })
                $(this).removeClass('unselected');
                $(this).addClass('selected');
                $($(this).attr('href')).slideDown(300);
              });
            }); //optimisation.lists
            
            $container.hide();
            $container.slideDown(500);
          });
        });/**/

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
                  <li><a href="settings.jsp">Settings</a></li>
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
      </div>
      <div id="footer">
      </div>
    </div>
  </body> 
</html>