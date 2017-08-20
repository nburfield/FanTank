// // Plugin options and our code
// $("#loginRegisterModal").leanModal({
//         top: 100,
//         overlay: 0.6,
//         closeButton: ".modal_close"
// });

$(function() {
    // Calling Login Form
    $("#login").click(function() {
        $(".register").hide();
        $(".login").show();
        $(".header_title").text('Login');
        return false;
    });

    // Calling Register Form
    $("#register").click(function() {
        $(".login").hide();
        $(".register").show();
        $(".header_title").text('Register');
        return false;
    });

    // Going back to Social Forms
    $(".back_btn").click(function() {
        $(".user_login").hide();
        $(".user_register").hide();
        $(".social_login").show();
        $(".header_title").text('Login');
        return false;
    });

    // jQuery for page scrolling feature - requires jQuery Easing plugin
    $(document).on('click', 'a.page-scroll', function(event) {
        var $anchor = $(this);
        $('html, body').stop().animate({
            scrollTop: ($($anchor.attr('href')).offset().top - 60)
        }, 1250, 'easeInOutExpo');
        event.preventDefault();
    });

    // Highlight the top nav as scrolling occurs
    $('body').scrollspy({
        target: '.navbar-fixed-top-home',
        offset: 150
    });

    // Closes the Responsive Menu on Menu Item Click
    $('#top-navbar ul li a').click(function() {
        $('#top-navbar-button:visible').click();
    });

    // Closes the Responsive Menu on Menu Item Click
    $('#mobile-user ul li a').click(function() {
        $('#mobile-user-button:visible').click();
    });

    // Closes the Responsive Menu on Menu Item Click
    $('#top-navbar-button').click(function() {
        console.log($('#top-navbar-button'))
        //$('#top-navbar-button:focus').click();
    });

    // Offset for Main Navigation
    $('#mainNavHome').affix({
        offset: {
            top: 100
        }
    })

    // Offset for Main Navigation
    $('#mainNav').affix({
        offset: {
            top: -1
        }
    })

    // Offset for Artist Card
    $('#artistCardFloat').affix({
        offset: {
            top: 400
        }
    })

    // window.sr = ScrollReveal();
    // sr.reveal('.sr-icons', {
    //     duration: 600,
    //     scale: 0.3,
    //     distance: '0px',
    //     reset: true
    // }, 200);
});
