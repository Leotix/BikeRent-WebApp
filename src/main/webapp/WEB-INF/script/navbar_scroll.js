$(document).ready(function(){
    const num = $(".header-class").offset().top;
    $(window).bind('scroll', function() {
        if ($(window).scrollTop() > num) {
            $('.header-class').addClass('fixed');
        }
        else {
            $('.header-class').removeClass('fixed');
        }
    });
});