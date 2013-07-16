try {
    var value = '/spring/statJS?${HTML5_KEY}=';
    if (window.HTMLCanvasElement) {
        value += '${HTML5_KEY_Y}';
    } else {
        value += '${HTML5_KEY_N}';
    }
    var f = document.createElement('img');
    f.setAttribute('src', value);
    f.style.position = 'absolute';
    f.style.top = '0';
    f.style.left = '0';
    document.body.appendChild(f);
} catch (e) {
    errorMessage = encodeURI('JSDetection exception:' + e);
    pathname = encodeURI(window.location.pathname);
    var img = document.createElement('img');
    img.src = '/spring/lsc?e=' + errorMessage + '&t=' + new Date().getTime() + '&p=' + pathname;
    document.body.appendChild(img);
}