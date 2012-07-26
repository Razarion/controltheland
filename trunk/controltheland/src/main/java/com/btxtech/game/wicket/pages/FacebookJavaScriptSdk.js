/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 25.07.12
 * Time: 14:58
 * To change this template use File | Settings | File Templates.
 */
window.fbAsyncInit = function () {
    FB.init({
        appId:'${FACEBOOK_APP_ID}', // App ID
        channelUrl:'${CHANNEL_URL}', // Channel File
        status:true, // check login status
        cookie:true, // enable cookies to allow the server to access the session
        xfbml:true  // parse XFBML
    });

    // Additional initialization code here
};

// Load the SDK Asynchronously
(function (d) {
    var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
    if (d.getElementById(id)) {
        return;
    }
    js = d.createElement('script');
    js.id = id;
    js.async = true;
    js.src = "//connect.facebook.net/en_US/all.js";
    ref.parentNode.insertBefore(js, ref);
}(document));