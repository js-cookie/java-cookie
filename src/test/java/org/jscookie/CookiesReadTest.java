package org.jscookie;

import javax.servlet.http.Cookie;

import org.jscookie.testutils.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class CookiesReadTest extends BaseTest {
	private Cookies cookies;

	@Before
	public void before() {
		cookies = new Cookies( request, response );
	}

	@Test
	public void simple_value() {
		Mockito.when( request.getCookies() ).thenReturn(new Cookie[] {
			new Cookie( "c", "v" )
		});
		String actual = cookies.get( "c" );
		String expected = "v";
		Assert.assertEquals( expected, actual );
	}
}
