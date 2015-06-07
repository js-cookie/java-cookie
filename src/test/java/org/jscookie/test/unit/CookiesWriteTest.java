package org.jscookie.test.unit;

import org.jscookie.Cookies;
import org.jscookie.Expiration;
import org.jscookie.test.unit.utils.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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
			.secure( true )
			.expires( Expiration.days( 1 ) );
		cookies.set( "c", "v" );

		// TODO Add expires
		Mockito.verify( response ).addHeader( "Set-Cookie", "c=v; Path=/; Domain=site.com; Secure" );
	}

	@Test
	public void simple_write_with_attributes() {
		cookies.set( "c", "v", Cookies.Attributes.empty()
			.path( "/" )
			.domain( "example.com" )
			.secure( true )
			.expires( Expiration.days( 1 ) )
		);

		// TODO expires
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
}
