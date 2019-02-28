package com.github.jscookie.javacookie.test.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jscookie.javacookie.Cookies;
import com.github.jscookie.javacookie.test.integration.test.utils.IntegrationTestUtils;
import com.github.jscookie.javacookie.test.integration.test.utils.Result;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WriteCookieServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Cookies cookies = Cookies.initFromServlet(request, response);

    String name = IntegrationTestUtils.getUTF8Param("name", request);
    String value = IntegrationTestUtils.getUTF8Param("value", request);

    cookies.set(name, value);

    response.setContentType("application/json");
    new ObjectMapper()
      .writeValue(response.getOutputStream(), new Result(name, value));
  }

}
