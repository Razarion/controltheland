window.fbAsyncInit = function () {
    FB.init({
        appId:'${FACEBOOK_APP_ID}', // App ID
        channelUrl:'${CHANNEL_URL}', // Channel File
        status:true, // check login status
        cookie:true, // enable cookies to allow the server to access the session
        xfbml:true  // parse XFBML
    });

    function login() {
        FB.login(function (response) {
            if (response.authResponse) {
                window.location.href = '${FACEBOOK_START}' + '?signed_request=' + response.authResponse.signedRequest;
            }
        });
    }

    FB.getLoginStatus(function (response) {
        if (response.status === 'connected') {
            window.onerror("User is already connected with facebook. This should not happen in the register process.", "FacebookRegister.js", "???");
        } else {
            var btn = document.getElementById('fbconnectbutton');
            btn.style.visibility = 'visible';
            btn.onclick = function () {
                login();
                return false;
            };
        }
    });
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