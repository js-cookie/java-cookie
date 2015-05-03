package org.jscookie.test.integration;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet( urlPatterns = "/" )
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1;
	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) {
		// TODO
	}
}
