module.exports = function (grunt) {

	grunt.initConfig({
		bower_postinst: {
			all: {
				options: {
					components: {
						'js-cookie': ['npm', 'grunt']
					}
				}
			}
		}
	});

	grunt.loadNpmTasks('grunt-bower-postinst');

	grunt.registerTask('default', ['bower_postinst']);
};
