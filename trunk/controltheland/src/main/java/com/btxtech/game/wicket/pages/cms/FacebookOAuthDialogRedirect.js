  var oauth_url = 'https://www.facebook.com/dialog/oauth/';
  oauth_url += '?client_id=${FACEBOOK_APP_ID}';
  oauth_url += '&redirect_uri=' + encodeURIComponent('https://apps.facebook.com/${FACEBOOK_APP_NAMESPACE}/');
  oauth_url += '&scope=${FACEBOOK_PERMISSIONS}';
  window.top.location = oauth_url;
