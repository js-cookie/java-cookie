package org.jscookie.test.unit;

import javax.servlet.http.Cookie;

import org.jscookie.Cookies;
import org.jscookie.Expiration;
import org.jscookie.test.unit.utils.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "c", actual.getName() );
		Assert.assertEquals( "v", actual.getValue() );
		Assert.assertEquals( "/", actual.getPath() );
		Assert.assertEquals( null, actual.getDomain() );
		Assert.assertEquals( false, actual.getSecure() );
		Assert.assertEquals( -1, actual.getMaxAge() );
	}

	@Test
	public void simple_write_with_default_attributes() {
		cookies.defaults()
			.path( "/" )
			.domain( "site.com" )
			.secure( true )
			.expires( Expiration.days( 1 ) );
		cookies.set( "c", "v" );

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "c", actual.getName() );
		Assert.assertEquals( "v", actual.getValue() );
		Assert.assertEquals( "/", actual.getPath() );
		Assert.assertEquals( "site.com", actual.getDomain() );
		Assert.assertEquals( true, actual.getSecure() );
		Assert.assertEquals( 86400, actual.getMaxAge() );
	}

	@Test
	public void simple_write_with_attributes() {
		cookies.set( "c", "v", Cookies.Attributes.empty()
			.path( "/" )
			.domain( "example.com" )
			.secure( true )
			.expires( Expiration.days( 1 ) )
		);

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "c", actual.getName() );
		Assert.assertEquals( "v", actual.getValue() );
		Assert.assertEquals( "/", actual.getPath() );
		Assert.assertEquals( "example.com", actual.getDomain() );
		Assert.assertEquals( true, actual.getSecure() );
		Assert.assertEquals( 86400, actual.getMaxAge() );
	}

	@Test
	public void simple_write_overriding_default_attributes() {
		cookies.defaults()
			.path( "/path/" )
			.secure( true );
		cookies.set( "c", "v", Cookies.Attributes.empty()
			.path( "/" )
		);

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "c", actual.getName() );
		Assert.assertEquals( "v", actual.getValue() );
		Assert.assertEquals( "/", actual.getPath() );
		Assert.assertEquals( "should consider default if not overriden", true, actual.getSecure() );
	}
}
