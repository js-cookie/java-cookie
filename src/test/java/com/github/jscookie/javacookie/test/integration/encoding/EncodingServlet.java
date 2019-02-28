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
import com.github.jscookie.javacookie.test.integration.test.utils.IntegrationTestUtils;
import com.github.jscookie.javacookie.test.integration.test.utils.Result;

public class EncodingServlet extends HttpServlet {
	private static final long serialVersionUID = 1;
	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response )
	throws ServletException, IOException {
		String name = IntegrationTestUtils.getUTF8Param( "name", request );

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

}