package com.github.jscookie.javacookie.test.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jscookie.javacookie.CookieSerializationException;
import com.github.jscookie.javacookie.Cookies;
import com.github.jscookie.javacookie.test.integration.test.utils.CustomJSONCookie;
import com.github.jscookie.javacookie.test.integration.test.utils.IntegrationTestUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WriteJSONCookieServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Cookies cookies = Cookies.initFromServlet(request, response);

    String name = IntegrationTestUtils.getUTF8Param("name", request);
    String value = IntegrationTestUtils.getUTF8Param("value", request);

    CustomJSONCookie customJSONCookie = new CustomJSONCookie(value, value);

    try {
      cookies.set(name, customJSONCookie);
    } catch (CookieSerializationException e) {
      e.printStackTrace();
    }

    response.setContentType("application/json");
    new ObjectMapper()
        .writeValue(response.getOutputStream(), customJSONCookie);
  }
}
