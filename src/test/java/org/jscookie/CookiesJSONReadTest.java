package org.jscookie;

import javax.servlet.http.Cookie;

import org.jscookie.testutils.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.annotation.JsonProperty;

@RunWith( MockitoJUnitRunner.class )
public class CookiesJSONReadTest extends BaseTest {
	private Cookies cookies;

	@Before
	public void before() {
		cookies = new Cookies( request, response );
	}

	@Test
	public void read_int_type() throws ParseException {
		Mockito.when( request.getCookies() ).thenReturn(new Cookie[] {
			new Cookie( "c", "1" )
		});

		String actual = cookies.get( "c" );
		String expected = "1";
		Assert.assertEquals( expected, actual );

		int actual2 = cookies.get( "c", Integer.class );
		int expected2 = 1;
		Assert.assertEquals( expected2, actual2 );
	}

	@Test
	public void read_boolean_type() throws ParseException {
		Mockito.when( request.getCookies() ).thenReturn(new Cookie[] {
			new Cookie( "c", "true" )
		});

		String actual = cookies.get( "c" );
		String expected = "true";
		Assert.assertEquals( expected, actual );

		boolean actual2 = cookies.get( "c", Boolean.class );
		boolean expected2 = true;
		Assert.assertEquals( expected2, actual2 );
	}

//	@Test
//	public void read_JSON_array_string() {
//		Mockito.when( request.getCookies() ).thenReturn(new Cookie[] {
//			new Cookie( "c", "[\"v\"]" )
//		});
//
//		String actual = cookies.get( "c" );
//		String expected = "[\"v\"]";
//		Assert.assertEquals( expected, actual );
//
//		String actual2 = cookies.get( "c", JSONArray.class ).getString( 0 );
//		String expected2 = "v";
//		Assert.assertEquals( expected2, actual2 );
//	}
//
	@Test
	public void read_custom_type() throws ParseException {
		Mockito.when( request.getCookies() ).thenReturn(new Cookie[] {
			new Cookie( "c", "{\"property\":\"v\"}" )
		});

		String actual = cookies.get( "c" );
		String expected = "{\"property\":\"v\"}";
		Assert.assertEquals( expected, actual );

		String actual2 = cookies.get( "c", CustomType.class ).getProperty();
		String expected2 = "v";
		Assert.assertEquals( expected2, actual2 );
	}

//	@Test
//	public call_to_read_all_cookies_with_mixed_types() {
//		Mockito.when( request.getCookies() ).thenReturn(new Cookie[] {
//			new Cookie( "c", "{\"property\":\"bar\"}" ),
//			new Cookie( "c2", "v" )
//		});
//
//		JSONObject actual = cookies.get();
//
//		Assert.assertEquals( "{\"property\":\"bar\"}", actual.getString( "c" ) );
//		Assert.assertEquals( "v", actual.get( "c2" ) );
//		Assert.assertEquals( "bar", actual.get( "c", CustomType.class ).getProperty() );
//		Assert.assertEquals( "v", actual.getString( "v" ) );
//	}

	private static class CustomType {
		private String property;
		@JsonProperty( "property" )
		private String getProperty() {
			return property;
		}
	}
}
