package com.appspot.iclifeplanning.charts;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Notification servlet. Responsible for checking for new events in
 * the users calendar and sending notifications about the changes.
 * 
 * @author Agnieszka Magda Madurska (amm208@doc.ic.ac.uk)
 *
 */
@SuppressWarnings("serial")
public class ChartDataServlet extends HttpServlet {
	private static long timer = 0;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

	}
}
