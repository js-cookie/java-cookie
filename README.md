# Java Cookie

A simple Java API for handling cookies

## Basic Usage

Create a cookie, valid across the entire site

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", "value" );
```

Create a cookie that expires 7 days from now, valid across the entire site:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", "value", Attributes.empty()
  .expires( Expiration.days( 7 ) )
);
```

Create an expiring cookie, valid to the path of the current page:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", "value", Attributes.empty()
  .expires( Expiration.days( 7 ) )
  .path( "" )
);
```

Read cookie:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.get( "name" ); // => "value"
cookies.get( "nothing" ); // => null
```

Read all available cookies:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
Map<String, String> all = cookies.get(); // => {name=value}
```

Delete cookie:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.remove( "name" );
```

Delete a cookie valid to the path of the current page:

```java
Cookies cookies = Cookies.initFromServlet( request, response );
cookies.set( "name", "value", Attributes.empty()
  .path( "" )
);
cookies.remove( "name" ); // fail!
cookies.remove( "name", Attributes.empty().path( "path" ) ); // removed!
```

*IMPORTANT! when deleting a cookie, you must pass the exact same path, domain and secure attributes that were used to set the cookie, unless you're relying on the [default attributes](#cookie-attributes).*
