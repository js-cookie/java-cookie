package org.jscookie.test.unit;

import org.jscookie.Cookies;
import org.jscookie.test.unit.utils.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
		Mockito.verify( response ).addHeader( "Set-Cookie", "%3B%2C%5C%22%20=%3B%2C%5C%22%20; Path=/" );
	}

	@Test
	public void characters_allowed_in_name_and_value() {
		cookies.set(
			"!#$&'*+-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ^_`abcdefghijklmnopqrstuvwxyz|~",
			"!#$&'*+-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ^_`abcdefghijklmnopqrstuvwxyz|~"
		);
		Mockito.verify( response ).addHeader(
			"Set-Cookie",
			"!#$&'*+-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ^_`abcdefghijklmnopqrstuvwxyz|~=!#$&'*+-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ^_`abcdefghijklmnopqrstuvwxyz|~; Path=/"
		);
	}

	@Test
	public void character_with_3_bytes_in_value() {
		cookies.set( "c", "京" );
		Mockito.verify( response ).addHeader( "Set-Cookie", "c=%E4%BA%AC; Path=/" );
	}

	@Test
	public void character_with_3_bytes_in_name() {
		cookies.set( "京", "v" );
		Mockito.verify( response ).addHeader( "Set-Cookie", "%E4%BA%AC=v; Path=/" );
	}

	@Test
	public void characters_allowed_in_cookie_value() {
		cookies.set( "c", "/:<=>?@[]{}" );
		Mockito.verify( response ).addHeader( "Set-Cookie", "c=/:<=>?@[]{}; Path=/" );
	}
}
