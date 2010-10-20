package com.appspot.iclifeplanning;

import java.io.IOException;
import javax.servlet.http.*;

import com.google.gdata.client.http.AuthSubUtil;

@SuppressWarnings("serial")
public class TokenRequestServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String requestUrl =
			  AuthSubUtil.getRequestUrl("http://iclifeplanning.appspot.com/lifeplanning",
			                            "http://www.google.com/calendar/feeds/default/allcalendars/full",
			                            false,
			                            true);

		String suggestAuthorization = "<p>MyApp needs access to your" +
			  "Google Calendar account to read your Calendar feed. To authorize" +
			  "MyApp to access your account, <a href=\"" + requestUrl + "\">log in " +
			  "to your account</a>.</p>";
		resp.setContentType("text/html");    
		resp.getWriter().println(suggestAuthorization);
	}
}
