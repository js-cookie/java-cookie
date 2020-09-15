package com.github.jscookie.javacookie.test.unit;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.jscookie.javacookie.Cookies;
import com.github.jscookie.javacookie.Expiration;
import com.github.jscookie.javacookie.Cookies.Attributes;
import com.github.jscookie.javacookie.test.unit.utils.BaseTest;

@RunWith( MockitoJUnitRunner.class )
public class CookiesWriteTest extends BaseTest {
	private Cookies cookies;

	@Before
	public void before() {
		cookies = Cookies.initFromServlet( request, response );
	}

	@Test
	public void simple_write() {
		cookies.set( "c", "v" );
		Mockito.verify( response ).addHeader( "Set-Cookie", "c=v; Path=/" );
	}

	@Test
	public void simple_write_with_default_attributes() {
		cookies.defaults()
			.path( "/" )
			.domain( "site.com" )
			.secure( true );
		cookies.set( "c", "v" );

		Mockito.verify( response ).addHeader( "Set-Cookie", "c=v; Path=/; Domain=site.com; Secure" );
	}

	@Test
	public void simple_write_with_attributes() {
		cookies.set( "c", "v", Cookies.Attributes.empty()
			.path( "/" )
			.domain( "example.com" )
			.secure( true )
		);

		Mockito.verify( response ).addHeader( "Set-Cookie", "c=v; Path=/; Domain=example.com; Secure" );
	}

	@Test
	public void simple_write_overriding_default_attributes() {
		cookies.defaults()
			.path( "/path/" )
			.secure( true );
		cookies.set( "c", "v", Cookies.Attributes.empty()
			.path( "/" )
		);

		// Should consider default secure if not overriden
		Mockito.verify( response ).addHeader( "Set-Cookie", "c=v; Path=/; Secure" );
	}

	@Test
	public void removing_default_path_should_fallback_to_whole_site() {
		cookies.defaults()
			.path( null );
		cookies.set( "c", "v" );

		Mockito.verify( response ).addHeader( "Set-Cookie", "c=v; Path=/" );
	}

	@Test
	public void should_not_write_the_path_attribute_if_set_as_an_empty_string() {
		cookies.defaults()
			.path( "" );
		cookies.set( "c", "v" );

		Mockito.verify( response ).addHeader( "Set-Cookie", "c=v" );
	}

	@Test
	public void expires_attribute() {
		DateTime date_2015_06_07_23h38m46s = new DateTime( 2015, 6, 7, 23, 38, 46 );
		cookies.set( "c", "v", Attributes.empty()
			.expires( Expiration.date( date_2015_06_07_23h38m46s ) )
		);
		Mockito.verify( response ).addHeader( "Set-Cookie", "c=v; Path=/; Expires=Sun, 07 Jun 2015 23:38:46 GMT" );
	}

	@Test
	public void httponly_attribute() {
		cookies.set( "c", "v", Attributes.empty()
			.httpOnly( true )
		);
		Mockito.verify( response ).addHeader( "Set-Cookie", "c=v; Path=/; HttpOnly" );
	}

	@Test
	public void sameSite_attribute() {
		cookies.set( "c", "v", Attributes.empty()
			.sameSite( "Lax" )
		);
		Mockito.verify( response ).addHeader( "Set-Cookie", "c=v; Path=/; SameSite=Lax" );
	}
}
