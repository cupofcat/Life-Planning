package com.appspot.iclifeplanning;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class LifePlanningServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Howdy, world");
	}
}
