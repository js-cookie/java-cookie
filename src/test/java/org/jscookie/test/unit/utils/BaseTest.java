package org.jscookie.test.unit.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;

public class BaseTest {
	protected @Mock HttpServletRequest request;
	protected @Mock HttpServletResponse response;

	@Before
	public void before() {
		Mockito.when( request.getCookies() ).thenReturn( new Cookie[]{} );
	}
}
