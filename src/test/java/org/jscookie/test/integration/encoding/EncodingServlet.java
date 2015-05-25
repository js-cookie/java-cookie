package org.jscookie.test.integration.encoding;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jscookie.Cookies;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet( "/encoding" )
public class EncodingServlet extends HttpServlet {
	private static final long serialVersionUID = 1;
	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response )
	throws ServletException, IOException {
		String name = request.getParameter( "name" );

		System.out.println( "--------------------" );
		System.out.println( "Testing: " + name );

		Cookies cookies = new Cookies( request, response );
		String value = cookies.get( name );

		if ( value == null ) {
			throw new NullPointerException( "Cookie not found with name: " + name );
		}

		System.out.println( "Value: " + value );
		System.out.println( "--------------------" );

		cookies.set( name, value );

		response.setContentType( "application/json" );
		new ObjectMapper()
			.writeValue( response.getOutputStream(), new Result( name, value ) );
	}
}

class Result {
	private String name;
	private String value;
	Result( String name, String value ) {
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
}