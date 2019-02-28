package com.github.jscookie.javacookie.test.integration.test.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class IntegrationTestUtils {
  /**
   * Retrieves the parameter using UTF-8 charset since the server default is ISO-8859-1
   */
  public static String getUTF8Param( String name, HttpServletRequest request ) throws UnsupportedEncodingException {
    String query = request.getQueryString();
    for ( String pair : query.split( "&" ) ) {
      if ( name.equals( pair.split( "=" )[ 0 ] ) ) {
        return URLDecoder.decode( pair.split( "=" )[ 1 ], "UTF-8" );
      }
    }
    return null;
  }
}
