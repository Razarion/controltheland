function isNotChromeCommand(toTest) {
    if (toTest instanceof String || typeof toTest == 'string') {
        return !(toTest.substring(0, 9) == 'chrome://');
    } else {
        return true;
    }
}

window.onerror = function (message, file, lineNumber) {
    if (message != 'Script error' && lineNumber != 0 && isNotChromeCommand(file)) {
        var errorMessage = encodeURI("${DISPLAY_ERROR_PREFIX}\nMessage: " + message + "\nFile: " + file + "\nLinenumber: " + lineNumber);
        var pathName = encodeURI(window.location.pathname);
        var img = document.createElement('img');
        img.src = '/spring/lsc?e=' + errorMessage + '&t=' + new Date().getTime() + '&p=' + pathName;
        document.body.appendChild(img);
    }
    return true;
};