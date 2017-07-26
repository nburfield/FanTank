var gulp            = require('gulp');
var proxyMiddleware = require('http-proxy-middleware');
var browserSync     = require('browser-sync').create();
var less            = require('gulp-less');
var header          = require('gulp-header');
var cleanCSS        = require('gulp-clean-css');
var rename          = require("gulp-rename");
var uglify          = require('gulp-uglify');
var pkg             = require('./package.json');

// Set the banner content
var banner = ['/*!\n',
    ' * <%= pkg.author %> - v<%= pkg.version %>\n',
    ' * Copyright 2017-' + (new Date()).getFullYear(), ' <%= pkg.author %>\n',
    ' * Licensed under <%= pkg.license %>\n',
    ' */\n',
    ''
].join('');

// Compile LESS files from /less into /css
gulp.task('less', function() {
    return gulp.src('static/less/custom.less')
        .pipe(less())
        .pipe(header(banner, { pkg: pkg }))
        .pipe(gulp.dest('static/css'))
        .pipe(browserSync.reload({
            stream: true
        }))
});

// Minify compiled CSS
gulp.task('minify-css', ['less'], function() {
    return gulp.src('static/css/custom.css')
        .pipe(cleanCSS({ compatibility: 'ie8' }))
        .pipe(rename({ suffix: '.min' }))
        .pipe(gulp.dest('static/css'))
        .pipe(browserSync.reload({
            stream: true
        }))
});

// Minify JS
gulp.task('minify-js', function() {
    return gulp.src('static/js/custom.js')
        .pipe(uglify())
        .pipe(header(banner, { pkg: pkg }))
        .pipe(rename({ suffix: '.min' }))
        .pipe(gulp.dest('static/js'))
        .pipe(browserSync.reload({
            stream: true
        }))
});

// Copy vendor libraries from /node_modules into /vendor
gulp.task('copy', function() {
    gulp.src(['node_modules/bootstrap/dist/js/*.min.js', '!**/npm.js'])
        .pipe(gulp.dest('static/js'))

    gulp.src(['node_modules/bootstrap/dist/css/*.min.css', '!**/bootstrap-theme.*', '!**/*.map'])
        .pipe(gulp.dest('static/css'))

    gulp.src(['node_modules/jquery/dist/jquery.min.js'])
        .pipe(gulp.dest('static/js'))

    gulp.src(['node_modules/font-awesome/fonts/**'])
        .pipe(gulp.dest('static/fonts'))

    gulp.src(['node_modules/font-awesome/css/*.min.css', '!**/*.map'])
        .pipe(gulp.dest('static/css'))

    gulp.src(['node_modules/scrollreveal/dist/*.min.js'])
        .pipe(gulp.dest('static/js'))
})

// Dev task with browserSync
gulp.task('serve', ['less', 'minify-css', 'minify-js'], function() {
    var proxy = proxyMiddleware('/', {target: 'http://localhost:8080/'});
    browserSync.init({
        server: {
            baseDir: ".",
            routes: { "/": "templates" },
            middleware: [ proxy ]
        },
        startPath: '/'
    });

    gulp.watch('static/less/*.less', ['less']);
    gulp.watch('static/css/*.css', ['minify-css']);
    gulp.watch('static/js/*.js', ['minify-js']);
    // Reloads the browser whenever HTML or JS files change
    gulp.watch('templates/*.html', browserSync.reload);
    gulp.watch('templates/**/*.html', browserSync.reload);
    gulp.watch('static/js/**/*.js', browserSync.reload);
});
