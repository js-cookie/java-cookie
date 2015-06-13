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
- [NodeJS](http://nodejs.org/download/) required to run grunt.
- [Grunt](http://gruntjs.com/getting-started) for JavaScript task management.

### Getting started

Install [NodeJS](http://nodejs.org/).  
Install [Maven](https://maven.apache.org/download.cgi) and add `mvn` as a global alias to run the `/bin/mvn` command inside Maven folder.

Browse to the project root directory and run the build:

    $ mvn install

After the build completes, you should see the following message in the console:

    ----------------------------------------------------------------------------
    BUILD SUCCESS
    ----------------------------------------------------------------------------
