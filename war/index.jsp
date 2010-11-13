<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%
  UserService userService = UserServiceFactory.getUserService();
  String signupUrl = "";

  if (userService.isUserLoggedIn()) {
    response.sendRedirect("/dashboard");
  } else {
    signupUrl = userService.createLoginURL("/dashboard");
  }
%>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

    <link rel="stylesheet" href="css/main.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/lavalamp3.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/coda-slider.css" type="text/css" media="screen" title="no title" charset="utf-8">
    
    <script src="js/jquery-1.2.6.min.js" type="text/javascript"></script>
    
    <script type="text/javascript" src="js/jquery.easing.1.3.js"></script>
    <script type="text/javascript" src="js/jquery.lavalamp-1.3.4b2.js"></script>
    
    <script src="js/jquery.scrollTo-1.4.2-min.js" type="text/javascript"></script>
    <script src="js/jquery.localscroll-1.2.7-min.js" type="text/javascript" charset="utf-8"></script>
    <script src="js/jquery.serialScroll-1.2.2-min.js" type="text/javascript" charset="utf-8"></script>
    <script src="js/coda-slider.js" type="text/javascript" charset="utf-8"></script>

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
            <div id="logo"><div id="bg"></div><h1>Life Planning.</h1></div>
            <div id="menu">
              <ul class="lamp" id="lavaLampMenu">
                  <li><a href="#home">Home</a></li>
                  <li><a href="#">Help</a></li>
                  <li><a href="#step1">Tour</a></li>
                  <li><a href="<%= signupUrl %>">Login</a></li>
              </ul>
            </div>
          </div>
          <div id="header-bottom">
            <div id="header-text">
              The advent of mobile broadband and the "always connected" experience encourages the development of services offering persistent storage of personal data. Services could then use this data to provide intelligent, personalised and contextual support for all activities of an individual’s life. Such a service would combine aspects of planning, scheduling, calendar maintenance and consumer advice. The service would mediate between the user and all the complex information sources available on the Internet (e.g. transport feeds, product descriptions, third-party services) to provide contextualised and personalised decision support (c.f. price comparison sites such as confused.com). The business model is that the Life Planning service would always operate in the user’s best interest and have no other obligations.
            </div>
            <div id="header-box">
              <div id="slogan">
                We <span class="i">plan</span>. You <span class="i">live</span>.
              </div>
              <div class="definition">
                <h1>life <span class="trans">\`līf\</span></h1>
                <p>a mode of manner of existence, as in the world of affairs of society.</p>
              </div>
              <div class="definition">
                <h1>planning <span class="trans">\`planning\</span></h1>
                <p>the act or process of making a plan or plans.</p>
              </div>
            </div>
          </div>  
        </div>
      </div>
      <div id="body">

        <div id="wrapper">
          <div id="slider">
            <!-- This is hidden in coda-slider.css -->
            <ul class="navigation">
              <li><a href="#home">Home</a></li>
              <li><a href="#step1">Step 1</a></li>
              <li><a href="#step2">Step 2</a></li>
            </ul>

            <div class="scroll">
              <div class="scrollContainer">
                
                <!-- Pane that is shown first when the page loads -->
                <div class="panel" id="home">
                  <div id="signup-bar">
                    <div id="signup-text">
                      The Life Planning service would counteract this by allowing individuals to specify the goals they would like to achieve across all aspects of their life and then combine the planning system with stochastic optimisation to help users plan their choices to ensure, over time, that their life goals would be met.
                    </div>
                    <div id="signup">
                      <a href="<%= signupUrl %>">Sign up with your Google Account</a>
                    </div>
                  </div>
                </div>
                
                <!-- Steps of the Tour -->
                <div class="panel" id="step1">Step 1 of the tour.<br /><a href="#step2">Go to step2</a></div>
                
                <div class="panel" id="step2">Step 2 of the tour.<br /><a href="#step1">Go to step1</a></div>
              </div>
            </div>
  
          </div>
        </div>

      </div>
      <div id="footer">
      </div>
    </div>
  </body> 
</html>