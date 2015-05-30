package org.jscookie.test.unit;

import javax.servlet.http.Cookie;

import org.jscookie.Cookies;
import org.jscookie.test.unit.utils.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class CookiesEncodingTest extends BaseTest {
	private Cookies cookies;

	@Before
	public void before() {
		cookies = new Cookies( request, response );
	}

	@Test
	public void character_not_allowed_in_name_and_value() {
		cookies.set( ";", ";" );

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );
		
		Cookie actual = argument.getValue();
		Assert.assertEquals( "%3B", actual.getName() );
		Assert.assertEquals( "%3B", actual.getValue() );
	}
}
