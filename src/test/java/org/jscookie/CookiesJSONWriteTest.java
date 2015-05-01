package org.jscookie;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;

import org.jscookie.testutils.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class CookiesJSONWriteTest extends BaseTest {
	private Cookies cookies;

	@Before
	public void before() {
		cookies = new Cookies( request, response );
	}

	@Test
	public void write_int_type() throws UnsupportedEncodingException {
		cookies.set( "c", 1 );

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
		Mockito.verify( response ).addCookie( argument.capture() );

		Cookie actual = argument.getValue();
		Assert.assertEquals( "1", actual.getValue() );
	}

//	@Test
//	public void write_boolean_type() {
//		cookies.set( "c", true );
//
//		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
//		Mockito.verify( response ).addCookie( argument.capture() );
//
//		Cookie actual = argument.getValue();
//		Assert.assertEquals( "true", actual.getValue() );
//	}
//
//	@Test
//	public void write_JSON_array_with_string() {
//		cookies.set( "c", Arrays.asList( "v" ) );
//
//		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
//		Mockito.verify( response ).addCookie( argument.capture() );
//
//		Cookie actual = argument.getValue();
//		Assert.assertEquals( "v", actual.getValue() );
//	}
//
//	@Test
//	public void write_custom_type_with_string_prop() {
//		cookies.set( "c", new CustomTypeString( "v" ) );
//
//		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
//		Mockito.verify( response ).addCookie( argument.capture() );
//
//		Cookie actual = argument.getValue();
//		Assert.assertEquals( "v", actual.getValue() );
//	}
//
//	@Test
//	public void write_custom_type_with_boolean_prop() {
//		cookies.set( "c", new CustomTypeBoolean( true ) );
//
//		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
//		Mockito.verify( response ).addCookie( argument.capture() );
//
//		Cookie actual = argument.getValue();
//		Assert.assertEquals( "true", actual.getValue() );
//	}
//
//	@Test
//	public void write_custom_type_with_number_prop() {
//		cookies.set( "c", new CustomTypeInteger( 1 ) );
//
//		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass( Cookie.class );
//		Mockito.verify( response ).addCookie( argument.capture() );
//
//		Cookie actual = argument.getValue();
//		Assert.assertEquals( "1", actual.getValue() );
//	}
//
//	private class CustomTypeString {
//		private String property;
//		private CustomTypeString( String property ) {
//			this.property = property;
//		}
//	}
//
//	private class CustomTypeBoolean {
//		private Boolean property;
//		private CustomTypeBoolean( Boolean property ) {
//			this.property = property;
//		}
//	}
//
//	private class CustomTypeInteger {
//		private Integer property;
//		private CustomTypeInteger( Integer property ) {
//			this.property = property;
//		}
//	}
}
