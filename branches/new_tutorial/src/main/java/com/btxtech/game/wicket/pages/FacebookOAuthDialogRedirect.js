  var oauth_url = 'https://www.facebook.com/dialog/oauth/';
  oauth_url += '?client_id=${FACEBOOK_APP_ID}';
  oauth_url += '&redirect_uri=' + encodeURIComponent('${FACEBOOK_REDIRECT_URI}');
  oauth_url += '&scope=' + encodeURIComponent('${FACEBOOK_PERMISSIONS}');
  window.top.location = oauth_url;