module.exports = function(config) {
    config.set({
        basePath: '../../..',
        frameworks: ['jasmine'],
        files: [
            'libs/angular/angular.js',
            'libs/angular-route/angular-route.js',
            'libs/angular-mocks/angular-mocks.js',
            'src/test/javascript/*.js'
        ],
        exclude: ['src/test/javascript/karma.conf*.js'],
        reporters: ['progress'],
        port: 9876,
        logLevel: config.LOG_INFO,
        browsers: ['PhantomJS'],
        singleRun: false,
        autoWatch: true,
        plugins: [
            'karma-jasmine',
            'karma-phantomjs-launcher'
        ]
    });
};