cordova.define("cordova-plugin-talkplus.talkplus", function(require, exports, module) {
var exec = require('cordova/exec');

exports.showToast = function (arg0, success, error) {
    exec(success, error, 'talkplus', 'showToast', [arg0]);
};

exports.joinRoom = function (arg0, success, error) {
    exec(success, error, 'talkplus', 'joinRoom', [arg0]);
};

});
