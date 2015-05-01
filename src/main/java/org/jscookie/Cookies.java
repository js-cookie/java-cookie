package org.jscookie;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Cookies implements CookiesDefinition {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private CookiesDefinition.Attributes defaults = new Attributes();
	private CookiesDefinition.Converter converter;
	private ObjectMapper mapper = new ObjectMapper();

	private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
	private static ResourceBundle lStrings = ResourceBundle.getBundle( LSTRING_FILE );

	private Cookies( HttpServletRequest request, HttpServletResponse response, CookiesDefinition.Converter converter ) {
		this( request, response );
		this.converter = converter;
	}

	public Cookies( HttpServletRequest request, HttpServletResponse response ) {
		this.request = request;
		this.response = response;
	}

	@Override
	public synchronized String get( String name ) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}

		Cookie[] cookies = request.getCookies();
		if ( cookies == null ) {
			return null;
		}

		for ( Cookie cookie : cookies ) {
			String decodedName = decodeName( cookie );

			if ( !name.equals( decodedName ) ) {
				continue;
			}

			String decodedValue = decodeValue( cookie, decodedName );

			if ( decodedValue == null ) {
				continue;
			}

			return decodedValue;
		}

		return null;
	}

	@Override
	public <T> T get( String name, Class<T> dataType ) throws CookieParseException {
		String value = get( name );
		try {
			return mapper.readValue( value, dataType );
		} catch ( IOException e ) {
			throw new CookieParseException( e );
		}
	}

	@Override
	public <T> T get( String name, TypeReference<T> typeRef ) throws CookieParseException {
		String value = get( name );
		try {
			return mapper.readValue( value, typeRef );
		} catch ( IOException e ) {
			throw new CookieParseException( e );
		}
	}

	@Override
	public Map<String, String> get() {
		Map<String, String> result = new HashMap<String, String>();

		Cookie[] cookies = request.getCookies();
		if ( cookies == null ) {
			return null;
		}

		for ( Cookie cookie : cookies ) {
			String decodedName = decodeName( cookie );
			String decodedValue = decodeValue( cookie, decodedName );

			if ( decodedValue == null ) {
				continue;
			}

			result.put( decodedName, decodedValue );
		}

		return result;
	}

	@Override
	public synchronized void set( String name, String value, CookiesDefinition.Attributes attributes ) throws UnsupportedEncodingException {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}
		if ( value == null ) {
			throw new IllegalArgumentException();
		}
		if ( attributes == null ) {
			throw new IllegalArgumentException();
		}

		String encodedName = URLEncoder.encode( name, StandardCharsets.UTF_8.name() );
		String encodedValue = URLEncoder.encode( value, StandardCharsets.UTF_8.name() );

		Cookie cookie = new Cookie( encodedName, encodedValue );
		attributes = extend( defaults, attributes );

		Expiration expires = attributes.expires();
		if ( expires != null ) {
			cookie.setMaxAge( expires.toSecondsFromNow() );
		}

		String path = attributes.path();
		if ( path != null ) {
			cookie.setPath( path );
		}

		String domain = attributes.domain();
		if ( domain != null ) {
			cookie.setDomain( domain );
		}

		Boolean secure = attributes.secure();
		if ( secure != null ) {
			cookie.setSecure( secure );
		}

		response.addCookie( cookie );
	}

	@Override
	public void set( String name, int value, CookiesDefinition.Attributes attributes ) throws CookieSerializationException {
		try {
			set( name, String.valueOf( value ), attributes );
		} catch ( UnsupportedEncodingException e ) {
			throw new CookieSerializationException( e );
		}
	}

	@Override
	public void set( String name, boolean value, CookiesDefinition.Attributes attributes ) throws CookieSerializationException {
		try {
			set( name, String.valueOf( value ), attributes );
		} catch ( UnsupportedEncodingException e ) {
			throw new CookieSerializationException( e );
		}
	}

	@Override
	public <T> void set( String name, List<T> value, CookiesDefinition.Attributes attributes ) throws CookieSerializationException {
		try {
			set( name, mapper.writeValueAsString( value ), attributes );
		} catch ( UnsupportedEncodingException | JsonProcessingException e ) {
			throw new CookieSerializationException( e );
		}
	}

	@Override
	public synchronized void set( String name, String value ) throws UnsupportedEncodingException {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}
		if ( value == null ) {
			throw new IllegalArgumentException();
		}
		set( name, value, defaults );
	}

	@Override
	public void set( String name, int value ) throws CookieSerializationException {
		set( name, value, new Attributes() );
	}

	@Override
	public void set( String name, boolean value ) throws CookieSerializationException {
		try {
			set( name, String.valueOf( value ) );
		} catch ( UnsupportedEncodingException e ) {
			throw new CookieSerializationException( e );
		}
	}

	@Override
	public <T> void set( String name, List<T> value ) throws CookieSerializationException {
		set( name, value, new Attributes() );
	}

	@Override
	public synchronized void remove( String name, CookiesDefinition.Attributes attributes ) throws UnsupportedEncodingException {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}
		if ( attributes == null ) {
			throw new IllegalArgumentException();
		}

		set( name, "", extend( attributes, new Attributes()
			.expires( Expiration.days( -1 ) ))
		);
	}

	@Override
	public synchronized void remove( String name ) throws UnsupportedEncodingException {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}
		remove( name, new Attributes() );
	}

	@Override
	public void setDefaults( CookiesDefinition.Attributes defaults ) {
		if ( defaults == null ) {
			throw new IllegalArgumentException();
		}
		this.defaults = defaults;
	}

	@Override
	public Cookies withConverter( CookiesDefinition.Converter converter ) {
		return new Cookies( request, response, converter );
	}

	private Attributes extend( CookiesDefinition.Attributes a, CookiesDefinition.Attributes b ) {
		return new Attributes().merge( a ).merge( b );
	}

	private String decodeName( Cookie cookie ) {
		try {
			return URLDecoder.decode( cookie.getName(), StandardCharsets.UTF_8.name() );
		} catch ( UnsupportedEncodingException e ) {
			e.printStackTrace();
		}
		return null;
	}

	private @Nullable String decodeValue( Cookie cookie, String decodedName ) {
		String decodedValue = null;

		if ( converter != null ) {
			try {
				decodedValue = converter.convert( cookie.getValue(), decodedName );
			} catch ( ConverterException e ) {
				e.printStackTrace();
			}
		}

		if ( decodedValue == null ) {
			try {
				decodedValue = URLDecoder.decode( cookie.getValue(), StandardCharsets.UTF_8.name() );
			} catch ( UnsupportedEncodingException e ) {
				e.printStackTrace();
			}
		}

		return decodedValue;
	}

	public static class Attributes extends CookiesDefinition.Attributes {
		private Expiration expires;
		private String path;
		private String domain;
		private Boolean secure;

		@Override
		@Nullable
		Expiration expires() {
			return expires;
		}
		public Attributes expires( @Nullable Expiration expires ) {
			this.expires = expires;
			return this;
		}

		@Override
		@Nullable
		String path() {
			return path;
		}
		public Attributes path( @Nullable String path ) {
			this.path = path;
			return this;
		}

		@Override
		@Nullable
		String domain() {
			return domain;
		}
		public Attributes domain( @Nullable String domain ) {
			this.domain = domain;
			return this;
		}

		@Override
		@Nullable
		Boolean secure() {
			return secure;
		}
		public Attributes secure( @Nullable Boolean secure ) {
			this.secure = secure;
			return this;
		}

		private Attributes merge( CookiesDefinition.Attributes reference ) {
			if ( reference.path() != null ) {
				path = reference.path();
			}
			if ( reference.domain() != null ) {
				domain = reference.domain();
			}
			if ( reference.secure() != null ) {
				secure = reference.secure();
			}
			if ( reference.expires() != null ) {
				expires = reference.expires();
			}
			return this;
		}
	}

	public static abstract class Converter extends CookiesDefinition.Converter {}
}
