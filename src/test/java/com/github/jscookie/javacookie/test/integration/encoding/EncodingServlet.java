package com.github.jscookie.javacookie.test.integration.encoding;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jscookie.javacookie.Cookies;

public class EncodingServlet extends HttpServlet {
	private static final long serialVersionUID = 1;
	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response )
	throws ServletException, IOException {
		String name = getUTF8Param( "name", request );

		System.out.println( "Testing: " + name );

		Cookies cookies = Cookies.initFromServlet( request, response );
		String value = cookies.get( name );

		if ( value == null ) {
			throw new NullPointerException( "Cookie not found with name: " + name );
		}

		cookies.set( name, value );

		response.setContentType( "application/json" );
		new ObjectMapper()
			.writeValue( response.getOutputStream(), new Result( name, value ) );
	}

	/**
	 * Retrieves the parameter using UTF-8 charset since the server default is ISO-8859-1
	 */
	private String getUTF8Param( String name, HttpServletRequest request ) throws UnsupportedEncodingException {
		String query = request.getQueryString();
		for ( String pair : query.split( "&" ) ) {
			if ( name.equals( pair.split( "=" )[ 0 ] ) ) {
				return URLDecoder.decode( pair.split( "=" )[ 1 ], "UTF-8" );
			}
		}
		return null;
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