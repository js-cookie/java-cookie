package com.github.jscookie.javacookie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public final class Cookies implements CookiesDefinition {
	private static String UTF_8 = "UTF-8";
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AttributesDefinition defaults = Attributes.empty();
	private ConverterStrategy converter;
	private ObjectMapper mapper = new ObjectMapper();

	private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
	private static ResourceBundle lStrings = ResourceBundle.getBundle( LSTRING_FILE );

	private Cookies( HttpServletRequest request, HttpServletResponse response, ConverterStrategy converter ) {
		this.request = request;
		this.response = response;
		this.converter = converter;
	}

	public static Cookies initFromServlet( HttpServletRequest request, HttpServletResponse response ) {
		return new Cookies( request, response, null );
	}

	@Override
	public synchronized String get( String name ) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}

		String cookieHeader = request.getHeader( "cookie" );
		if ( cookieHeader == null ) {
			return null;
		}

		Map<String, String> cookies = getCookies( cookieHeader );
		for ( String decodedName : cookies.keySet() ) {
			if ( !name.equals( decodedName ) ) {
				continue;
			}
			return cookies.get( decodedName );
		}

		return null;
	}

	@Override
	public <T> T get( String name, Class<T> dataType ) throws CookieParseException {
		String value = get( name );
		if ( value == null ) {
			return null;
		}
		try {
			return mapper.readValue( value, dataType );
		} catch ( IOException e ) {
			throw new CookieParseException( e );
		}
	}

	@Override
	public <T> T get( String name, TypeReference<T> typeRef ) throws CookieParseException {
		String value = get( name );
		if ( value == null ) {
			return null;
		}
		try {
			return mapper.readValue( value, typeRef );
		} catch ( IOException e ) {
			throw new CookieParseException( e );
		}
	}

	@Override
	public Map<String, String> get() {
		Map<String, String> result = new HashMap<String, String>();

		String cookieHeader = request.getHeader( "cookie" );
		if ( cookieHeader == null ) {
			return result;
		}

		return getCookies( cookieHeader );
	}

	@Override
	public synchronized void set( String name, String value, AttributesDefinition attributes ) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}
		if ( value == null ) {
			throw new IllegalArgumentException();
		}
		if ( attributes == null ) {
			throw new IllegalArgumentException();
		}

		String encodedName = encode( name );
		String encodedValue = encodeValue( value );

		StringBuilder header = new StringBuilder();
		header.append( encodedName );
		header.append( '=' );
		header.append( encodedValue );

		attributes = extend( Attributes.empty().path( "/" ), defaults, attributes );

		String path = attributes.path();
		if ( path != null && !path.isEmpty() ) {
			header.append( "; Path=" + path );
		}

		Expiration expires = attributes.expires();
		if ( expires != null ) {
			header.append( "; Expires=" + expires.toExpiresString() );
		}

		String domain = attributes.domain();
		if ( domain != null ) {
			header.append( "; Domain=" + domain );
		}

		Boolean secure = attributes.secure();
		if ( Boolean.TRUE.equals( secure ) ) {
			header.append( "; Secure" );
		}

		Boolean httpOnly = attributes.httpOnly();
		if ( Boolean.TRUE.equals( httpOnly ) ) {
			header.append( "; HttpOnly" );
		}

		String sameSite = attributes.sameSite();
		if ( sameSite != null ) {
			header.append( "; SameSite=" + sameSite );
		}

		if ( response.isCommitted() ) {
			return;
		}

		setCookie( header.toString(), response );
	}

	@Override
	public void set( String name, int value, AttributesDefinition attributes ) throws CookieSerializationException {
		set( name, String.valueOf( value ), attributes );
	}

	@Override
	public void set( String name, boolean value, AttributesDefinition attributes ) throws CookieSerializationException {
		set( name, String.valueOf( value ), attributes );
	}

	@Override
	public <T> void set( String name, List<T> value, AttributesDefinition attributes ) throws CookieSerializationException {
		try {
			set( name, mapper.writeValueAsString( value ), attributes );
		} catch ( JsonProcessingException e ) {
			throw new CookieSerializationException( e );
		}
	}

	@Override
	public void set( String name, CookieValue value, AttributesDefinition attributes ) throws CookieSerializationException {
		try {
			set( name, mapper.writeValueAsString( value ), attributes );
		} catch ( JsonProcessingException e ) {
			throw new CookieSerializationException( e );
		}
	}

	@Override
	public void set( String name, String value ) {
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
		set( name, value, Attributes.empty() );
	}

	@Override
	public void set( String name, boolean value ) {
		set( name, String.valueOf( value ) );
	}

	@Override
	public <T> void set( String name, List<T> value ) throws CookieSerializationException {
		set( name, value, Attributes.empty() );
	}

	@Override
	public void set( String name, CookieValue value ) throws CookieSerializationException {
		set( name, value, Attributes.empty() );
	}

	@Override
	public void remove( String name, AttributesDefinition attributes ) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}
		if ( attributes == null ) {
			throw new IllegalArgumentException();
		}

		set( name, "", extend( attributes, Attributes.empty()
			.expires( Expiration.days( -1 ) ))
		);
	}

	@Override
	public void remove( String name ) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( lStrings.getString( "err.cookie_name_blank" ) );
		}
		remove( name, Attributes.empty() );
	}

	@Override
	public AttributesDefinition defaults() {
		return this.defaults;
	}

	@Override
	public Cookies withConverter( ConverterStrategy converter ) {
		return new Cookies( request, response, converter );
	}

	private Attributes extend( AttributesDefinition... mergeables ) {
		Attributes result = Attributes.empty();
		for ( AttributesDefinition mergeable : mergeables ) {
			result.merge( mergeable );
		}
		return result;
	}

	private void setCookie( String cookieValue, HttpServletResponse response ) {
		response.addHeader( "Set-Cookie", cookieValue );
	}

	private String encode( String decoded ) {
		return encode( decoded, new HashSet<Integer>() );
	}

	private String encode( String decoded, Set<Integer> exceptions ) {
		String encoded = decoded;
		for ( int i = 0; i < decoded.length(); ) {
			int codePoint = decoded.codePointAt( i );
			i += Character.charCount( codePoint );

			boolean isDigit = codePoint >= codePoint( "0" ) && codePoint <= codePoint( "9" );
			if ( isDigit ) {
				continue;
			}

			boolean isAsciiUppercaseLetter = codePoint >= codePoint( "A" ) && codePoint <= codePoint( "Z" );
			if ( isAsciiUppercaseLetter ) {
				continue;
			}

			boolean isAsciiLowercaseLetter = codePoint >= codePoint( "a" ) && codePoint <= codePoint( "z" );
			if ( isAsciiLowercaseLetter ) {
				continue;
			}

			boolean isAllowed =
					codePoint == codePoint( "!" ) || codePoint == codePoint( "#" ) ||
					codePoint == codePoint( "$" ) || codePoint == codePoint( "&" ) ||
					codePoint == codePoint( "'" ) || codePoint == codePoint( "*" ) ||
					codePoint == codePoint( "+" ) || codePoint == codePoint( "-" ) ||
					codePoint == codePoint( "." ) || codePoint == codePoint( "^" ) ||
					codePoint == codePoint( "_" ) || codePoint == codePoint( "`" ) ||
					codePoint == codePoint( "|" ) || codePoint == codePoint( "~" );
			if ( isAllowed ) {
				continue;
			}

			if ( exceptions.contains( codePoint ) ) {
				continue;
			}

			try {
				String character = new String( Character.toChars( codePoint ) );
				CharArrayWriter hexSequence = new CharArrayWriter();
				byte[] bytes = character.getBytes( UTF_8 );
				for (byte aByte : bytes) {
					char left = Character.forDigit(aByte >> 4 & 0xF, 16);
					char right = Character.forDigit(aByte & 0xF, 16);
					hexSequence
							.append('%')
							.append(left)
							.append(right);
				}
				String sequence = hexSequence.toString().toUpperCase();
				encoded = encoded.replace(character, sequence );
			} catch ( UnsupportedEncodingException e ) {
				e.printStackTrace();
			}
		}
		return encoded;
	}

	private String decode(String encoded) {
		// Use URLDecoder to fix https://github.com/js-cookie/java-cookie/issues/14
		String decoded = encoded;
		try {
		  decoded = URLDecoder.decode(encoded, UTF_8);
		} catch ( UnsupportedEncodingException e) {
				e.printStackTrace();
		}
		return decoded;
	}

	private String encodeValue( String decodedValue ) {
		Set<Integer> exceptions = new HashSet<Integer>();
		for ( int i = 0; i < decodedValue.length(); ) {
			int codePoint = decodedValue.codePointAt( i );
			i += Character.charCount( codePoint );

			boolean isIgnorable = false;
			if ( codePoint == codePoint( "/" ) || codePoint == codePoint( ":") ) {
				isIgnorable = true;
			}

			if ( codePoint >= codePoint( "<" ) && codePoint <= codePoint( "@" ) ) {
				isIgnorable = true;
			}

			if ( codePoint == codePoint( "[" ) || codePoint == codePoint( "]" ) ) {
				isIgnorable = true;
			}

			if ( codePoint == codePoint( "{" ) || codePoint == codePoint( "}" ) ) {
				isIgnorable = true;
			}

			if ( isIgnorable ) {
				exceptions.add( codePoint );
			}
		}

		return encode( decodedValue, exceptions );
	}

	private int codePoint( String character ) {
		return character.codePointAt( 0 );
	}

	private String decodeValue( String encodedValue, String decodedName ) {
		String decodedValue = null;

		if ( converter != null ) {
			try {
				decodedValue = converter.convert( encodedValue, decodedName );
			} catch ( ConverterException e ) {
				e.printStackTrace();
			}
		}

		if ( decodedValue == null ) {
			decodedValue = decode( encodedValue );
		}

		return decodedValue;
	}

	private Map<String, String> getCookies( String cookieHeader ) {
		Map<String, String> result = new HashMap<String, String>();
		String[] cookies = cookieHeader.split( "; " );
		for (String cookie : cookies) {
			String encodedName = cookie.split("=")[0];
			String decodedName = decode(encodedName);

			String encodedValue = cookie.substring(cookie.indexOf('=') + 1);
			String decodedValue = decodeValue(encodedValue, decodedName);
			result.put(decodedName, decodedValue);
		}
		return result;
	}

	public static class Attributes extends AttributesDefinition {
		private Expiration expires;
		private String path;
		private String domain;
		private Boolean secure;
		private Boolean httpOnly;
		private String sameSite;

		private Attributes() {}

		public static Attributes empty() {
			return new Attributes();
		}

		@Override
		Expiration expires() {
			return expires;
		}
		@Override
		public Attributes expires( Expiration expires ) {
			this.expires = expires;
			return this;
		}

		@Override
		String path() {
			return path;
		}
		@Override
		public Attributes path( String path ) {
			this.path = path;
			return this;
		}

		@Override
		String domain() {
			return domain;
		}
		@Override
		public Attributes domain( String domain ) {
			this.domain = domain;
			return this;
		}

		@Override
		Boolean secure() {
			return secure;
		}
		@Override
		public Attributes secure( Boolean secure ) {
			this.secure = secure;
			return this;
		}

		@Override
		Boolean httpOnly() {
			return httpOnly;
		}
		@Override
		public Attributes httpOnly( Boolean httpOnly ) {
			this.httpOnly = httpOnly;
			return this;
		}

		@Override
		String sameSite() {
			return sameSite;
		}
		@Override
		public Attributes sameSite( String sameSite ) {
			this.sameSite = sameSite;
			return this;
		}

		private Attributes merge( AttributesDefinition reference ) {
			if ( reference.path() != null ) {
				path = reference.path();
			}
			if ( reference.domain() != null ) {
				domain = reference.domain();
			}
			if ( reference.expires() != null ) {
				expires = reference.expires();
			}
			if ( reference.secure() != null ) {
				secure = reference.secure();
			}
			if ( reference.httpOnly() != null ) {
				httpOnly = reference.httpOnly();
			}
			if ( reference.sameSite() != null ) {
				sameSite = reference.sameSite();
			}
			return this;
		}
	}

	public static abstract class Converter implements ConverterStrategy {}
}
