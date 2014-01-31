window.fbAsyncInit = function () {
    FB.init({
        appId: '${FACEBOOK_APP_ID}', // App ID
        channelUrl: '${CHANNEL_URL}', // Channel File
        status: true, // check login status
        cookie: true, // enable cookies to allow the server to access the session
        xfbml: true  // parse XFBML
    });

    FB.login(function (loginResponse) {
        if (loginResponse.authResponse) {
            FB.api('/me', function (apiResponse) {
                try {
                    var form = document.createElement('form');
                    form.setAttribute('method', 'post');
                    form.setAttribute('action', '${OAUTH_SUCCESS_PAGE_URL}');
                    var signedRequestField = document.createElement('input');
                    signedRequestField.setAttribute('type', 'hidden');
                    signedRequestField.setAttribute('name', 'signed_request');
                    signedRequestField.setAttribute("value", loginResponse.authResponse.signedRequest);
                    form.appendChild(signedRequestField);
                    var linkField = document.createElement('input');
                    linkField.setAttribute('type', 'hidden');
                    linkField.setAttribute('name', 'link');
                    linkField.setAttribute("value", apiResponse.link);
                    form.appendChild(linkField);
                    var firstNameField = document.createElement('input');
                    firstNameField.setAttribute('type', 'hidden');
                    firstNameField.setAttribute('name', 'firstName');
                    firstNameField.setAttribute("value", apiResponse.first_name);
                    form.appendChild(firstNameField);
                    var lastNameField = document.createElement('input');
                    lastNameField.setAttribute('type', 'hidden');
                    lastNameField.setAttribute('name', 'lastName');
                    lastNameField.setAttribute("value", apiResponse.last_name);
                    form.appendChild(lastNameField);
                    var emailField = document.createElement('input');
                    emailField.setAttribute('type', 'hidden');
                    emailField.setAttribute('name', 'email');
                    emailField.setAttribute("value", apiResponse.email);
                    form.appendChild(emailField);
                    document.body.appendChild(form);
                    form.submit();
                } catch (e) {
                    errorMessage = encodeURI('FacbookOAuthDialog.js exception:' + e);
                    pathname = encodeURI(window.location.pathname);
                    var img = document.createElement('img');
                    img.src = '/spring/lsc?e=' + errorMessage + '&t=' + new Date().getTime() + '&p=' + pathname;
                    document.body.appendChild(img);
                }
            });
        } else {
            window.location.href = '${OAUTH_SUCCESS_PAGE_URL}?error=access_denied';
        }
    }, {scope: 'email'});
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