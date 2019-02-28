## Issues

- Report issues or feature requests on [GitHub Issues](https://github.com/js-cookie/java-cookie/issues).
- If reporting a bug, please add a [simplified example](http://sscce.org/).

## Pull requests

- Create a new topic branch for every separate change you make.
- Create a test case if you are fixing a bug or implementing an important feature.
- Make sure the build runs successfully [(see below)](#development).

## Development

### Tools

We use the following tools for development:

- [Maven](https://maven.apache.org/) for Java Build.
- [NodeJS](https://nodejs.org/en/download/) used for NPM (installed by Maven automatically).
- [NPM](https://www.npmjs.com) used to install Bower (installed by Maven automatically).
- [Bower](https://bower.io) used to get [js-cookie](https://github.com/js-cookie/js-cookie/) for Integration tests (installed by NPM automatically).


### Getting started

Install [Maven](https://maven.apache.org/download.cgi) and add `mvn` as a global alias to run the `/bin/mvn` command inside Maven folder.

Browse to the project root directory and run the build:

    $ mvn install

After the build completes, you should see the following message in the console:

    ----------------------------------------------------------------------------
    BUILD SUCCESS
    ----------------------------------------------------------------------------

### Unit tests

To run the unit tests, execute the following command:

    $ mvn test

### Integration tests

If you want to debug the integration tests in the browser, switch `Debug.FALSE` to `Debug.TRUE` in `CookiesEncodingIT.java` and run the build:

    $ mvn verify

[Arquillian](http://arquillian.org/) will start the server, [Selenium](http://www.seleniumhq.org/) will run the tests in Firefox, but the build will hang to allow debugging in the browser.

It uses the [integration hook](https://github.com/js-cookie/js-cookie/blob/master/CONTRIBUTING.md#integration-with-server-side) provided by the project [js-cookie](https://github.com/js-cookie/js-cookie).
