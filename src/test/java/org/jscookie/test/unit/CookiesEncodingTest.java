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
		cookies = Cookies.initFromServlet( request, response );
	}

	@Test
	public void character_not_allowed_in_name_and_value() {
		cookies.set( ";,\\\" ", ";,\\\" " );

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );
		
		Cookie actual = argument.getValue();
		Assert.assertEquals( "%3B%2C%5C%22%20", actual.getName() );
		Assert.assertEquals( "%3B%2C%5C%22%20", actual.getValue() );
	}

	@Test
	public void characters_allowed_in_name_and_value() {
		cookies.set(
			"!#$&'*+-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ^_`abcdefghijklmnopqrstuvwxyz|~",
			"!#$&'*+-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ^_`abcdefghijklmnopqrstuvwxyz|~"
		);

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );
		
		Cookie actual = argument.getValue();
		Assert.assertEquals(
			"!#$&'*+-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ^_`abcdefghijklmnopqrstuvwxyz|~",
			actual.getValue()
		);
		Assert.assertEquals(
			"!#$&'*+-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ^_`abcdefghijklmnopqrstuvwxyz|~",
			actual.getName()
		);
	}

	@Test
	public void character_with_3_bytes() {
		cookies.set( "c", "京" );

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "%E4%BA%AC", actual.getValue() );
	}
}
