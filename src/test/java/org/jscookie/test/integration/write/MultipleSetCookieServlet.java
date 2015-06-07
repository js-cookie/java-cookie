package org.jscookie.test.integration.write;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jscookie.Cookies;

@WebServlet( "/multiple-set-cookie" )
public class MultipleSetCookieServlet extends HttpServlet {
	private static final long serialVersionUID = 1;
	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) {
		Cookies cookies = Cookies.initFromServlet( request, response );
		cookies.set( "c", "v" );
		cookies.set( "c", "v" );
	}
}
