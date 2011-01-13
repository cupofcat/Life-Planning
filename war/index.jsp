<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%
  UserService userService = UserServiceFactory.getUserService();
  String signupUrl = "";

  if (userService.isUserLoggedIn()) {
    response.sendRedirect("/dashboard/index.jsp");
  } else {
    signupUrl = userService.createLoginURL("/dashboard/index.jsp");
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
                  <li><a href="#help">Help</a></li>
                  <li><a href="#step1">Tour</a></li>
                  <li><a href="<%= signupUrl %>">Login</a></li>
              </ul>
            </div>
          </div>
          <div id="header-bottom">
            <div id="header-text">
The development of mobile devices and e-mail has spread work into areas of time and space it did not previously occupy, to the detriment of family, domestic and social life (“the Blackberry curse”). The Life Planning counteracts this by allowing you to specify the balance you would like to achieve between all aspects of you life!
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
              <li><a href="#step2">Step 3</a></li>
              <li><a href="#step2">Step 4</a></li>
              <li><a href="#step2">Step 5</a></li>
              <li><a href="#help">Help</a></li>
            </ul>

            <div class="scroll">
              <div class="scrollContainer">
                
                <!-- Pane that is shown first when the page loads -->
                <div class="panel" id="home">
                  <div id="signup-bar">
                    <div id="signup-text">
To start using the service all you need is a Google account with Google Calendar. Simply sign in using the button on the right and authorise our application to access your data. We do not store your password! If you need assistance, please check the <a href="#step1">tour</a> section of the website.
                    </div>
                    <div id="signup">
                      <a href="<%= signupUrl %>">Sign up with your Google Account</a>
                    </div>
                  </div>
                </div>
                
                <!-- Steps of the Tour -->
                <div class="panel" id="step1">
                  <span class="tour-step">1. Sign up with your Google Account</span>
                  <img src="img/tour/01-sign.png" />
                  <a href="#step2" class="tour-next">Next</a>
                  <a href="#home" class="tour-prev">Prev</a>
                </div>
                
                <div class="panel" id="step2">
                  <span class="tour-step">2. Authorise our application to access your calendar</span>
                  <img src="img/tour/02-authorize.png" />
                  <a href="#step3" class="tour-next">Next</a>
                  <a href="#step1" class="tour-prev">Prev</a>
                </div>
                
                <div class="panel" id="step3">
                  <span class="tour-step">3. Set the desired life balance</span>
                  <img src="img/tour/03-settings.png" />
                  <a href="#step4" class="tour-next">Next</a>
                  <a href="#step2" class="tour-prev">Prev</a>
                </div>
                
                <div class="panel" id="step4">
                  <span class="tour-step">4. Optimise your life</span>
                  <img src="img/tour/04-optimise.png" />
                  <a href="#step5" class="tour-next">Next</a>
                  <a href="#step3" class="tour-prev">Prev</a>
                </div>
                
                <div class="panel" id="step5">
                  <span class="tour-step">5. See the results!</span>
                  <img src="img/tour/05-stats.png" />
                  <a href="#home" class="tour-next">Sign up!</a>
                  <a href="#step4" class="tour-prev">Prev</a>
                </div>
                
                <div class="panel" id="help">
                  <ul>
                    <li>
                      <span class="question">Is my data safe?</span>
                      <span class="answer">Yes! We do not store your Google login nor your password and do not copy any data from your calendar. The only data we store is the data directly connected to using our service.</span>
                    </li>
                    <li>
                      <span class="question">How does it work?</span>
                      <span class="answer">By using some fancy AI, a little bit of magic and lots of stochastic analysis.</span>
                    </li>
                    <li>
                      <span class="question">Is it good?</span>
                      <span class="answer">Yes, very!</span>
                    </li>
                    <li>
                      <span class="question">Will it make me happy?</span>
                      <span class="answer">There is only one way to find out. But for the money we charge ($0), it is definitely worth a shot!</span>
                    </li>
                    <li>
                      <span class="question">Do I need to do everything you say?</span>
                      <span class="answer">No, you can reject any number of suggestions, but you need to understand that this will keep you further away from your desired life balance...</span>
                    </li>
                  </ul>
                </div>
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