package org.jscookie.test.integration.encoding;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet( "/encoding" )
public class EncodingServlet extends HttpServlet {
	private static final long serialVersionUID = 1;
	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response )
	throws ServletException, IOException {
		request.getRequestDispatcher( "/encoding.html" ).forward( request, response );
	}
}
