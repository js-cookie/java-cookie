package org.jscookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Cookies implements CookiesHandler {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Attributes defaults = new Attributes();

	public Cookies( HttpServletRequest request, HttpServletResponse response ) {
		this.request = request;
		this.response = response;
	}

	@Override
	public synchronized Cookie get( String name ) {
		if ( name == null ) {
			throw new NullPointerException( "name is null" );
		}
		// TODO handle encoding
		Cookie[] cookies = request.getCookies();
		if ( cookies != null ) {
			for ( Cookie cookie : cookies ) {
				if ( name.equals( cookie.getName() ) ) {
					return cookie;
				}
			}
		}
		return null;
	}

	@Override
	public synchronized void set( String name, String value, Attributes attributes ) {
		// TODO handle encoding
		Cookie cookie = get( name );

		if ( cookie == null ) {
			// TODO check the behavior for "org.glassfish.web.rfc2109_cookie_names_enforced" is true
			cookie = new Cookie( name, value );
		}

		cookie.setValue( value );

		attributes = extend( defaults, attributes );

		if ( attributes.expires != null ) {
			cookie.setMaxAge( attributes.expires.toSecondsFromNow() );
		}

		if ( attributes.path != null ) {
			cookie.setPath( attributes.path );
		}

		if ( attributes.domain != null ) {
			cookie.setDomain( attributes.domain );
		}

		if ( attributes.secure != null ) {
			cookie.setSecure( attributes.secure );
		}

		response.addCookie( cookie );
	}

	@Override
	public synchronized void set( String name, String value ) {
		set( name, value, defaults );
	}

	@Override
	public synchronized void remove( String name, Attributes attributes ) {
		Cookie cookie = get( name );
		if ( cookie == null ) {
			return;
		}
		set( name, "", extend( attributes, new Attributes()
			.expires( Expiration.days( -1 ) ))
		);
	}

	@Override
	public synchronized void remove( String name ) {
		remove( name, new Attributes() );
	}

	@Override
	public void setDefaults( Attributes defaults ) {
		this.defaults = defaults;
	}

	private Attributes extend( Attributes a, Attributes b ) {
		return new Attributes().merge( a ).merge( b );
	}

	public static class Attributes {
		private Expiration expires;
		private String path;
		private String domain;
		private Boolean secure;
		public Attributes expires( Expiration expires ) {
			this.expires = expires;
			return this;
		}
		public Attributes path( String path ) {
			this.path = path;
			return this;
		}
		public Attributes domain( String domain ) {
			this.domain = domain;
			return this;
		}
		public Attributes secure( boolean secure ) {
			this.secure = secure;
			return this;
		}
		private Attributes merge( Attributes reference ) {
			if ( reference.path != null ) {
				path = reference.path;
			}
			if ( reference.domain != null ) {
				domain = reference.domain;
			}
			if ( reference.secure != null ) {
				secure = reference.secure;
			}
			if ( reference.expires != null ) {
				expires = reference.expires;
			}
			return this;
		}
	}
}
