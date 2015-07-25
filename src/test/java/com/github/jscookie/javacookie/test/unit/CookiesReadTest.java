package com.github.jscookie.javacookie.test.unit;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.jscookie.javacookie.Cookies;
import com.github.jscookie.javacookie.test.unit.utils.BaseTest;

@RunWith( MockitoJUnitRunner.class )
public class CookiesReadTest extends BaseTest {
	private Cookies cookies;

	@Before
	public void before() {
		cookies = Cookies.initFromServlet( request, response );
	}

	@Test
	public void simple_value() {
		Mockito.when( request.getHeader( "cookie" ) ).thenReturn( "c=v" );
		String actual = cookies.get( "c" );
		String expected = "v";
		Assert.assertEquals( expected, actual );
	}

	@Test
	public void read_all() {
		Mockito.when( request.getHeader( "cookie" ) ).thenReturn( "c=v; foo=bar" );
		Map<String, String> result = cookies.get();

		String actual = result.get( "c" );
		String expected = "v";
		Assert.assertEquals( expected, actual );

		actual = result.get( "foo" );
		expected = "bar";
		Assert.assertEquals( expected, actual );
	}

	@Test
	public void equal_sign_in_cookie_name() {
		Mockito.when( request.getHeader( "cookie" ) ).thenReturn( "c=a=b" );

		String actual = cookies.get( "c" );
		String expected = "a=b";
		Assert.assertEquals( expected, actual );
	}
}
