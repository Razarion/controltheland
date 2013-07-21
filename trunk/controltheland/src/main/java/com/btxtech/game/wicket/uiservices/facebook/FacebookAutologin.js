window.fbAsyncInit = function () {
    FB.init({
        appId:'${FACEBOOK_APP_ID}', // App ID
        channelUrl:'${CHANNEL_URL}', // Channel File
        status:true, // check login status
        cookie:true, // enable cookies to allow the server to access the session
        xfbml:true  // parse XFBML
    });

    FB.getLoginStatus(function (response) {
        if (response.status === 'connected') {
            var form = document.createElement('form');
            form.setAttribute('method', 'post');
            form.setAttribute('action', '${FACEBOOK_AUTO_LOGIN}');
            var hiddenField = document.createElement('input');
            hiddenField.setAttribute('type', 'hidden');
            hiddenField.setAttribute('name', 'signed_request');
            hiddenField.setAttribute("value", response.authResponse.signedRequest);
            form.appendChild(hiddenField);
            document.body.appendChild(form);
            form.submit();
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