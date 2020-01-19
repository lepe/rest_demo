/**
 * Common XHR method
 * @param method: HTTP method (GET, POST, PUT, DELETE)
 * @param url: service URL
 * @param callback: Callback on Success (it will return data)
 * @param error_callback: Callback on Failure
 * @param data: Data object to send (in case of POST and PUT)
 *
 * @author A.Lepe (dev@alepe.com)
 */

// Public functions:
var $get, $post, $put, $delete;

(function() {
    // This function is private
    var XHR = function(method, url, callback, error_callback, data) {
        var request = new XMLHttpRequest();
        request.open(method, url, true);
        if(method == "POST" || method == "PUT") {
            request.setRequestHeader('Content-Type', 'application/json');
        }
        request.onload = function() {
          if (request.status >= 200 && request.status < 400) {
            var data = JSON.parse(request.responseText);
            if(callback !== undefined) {
                callback(data);
            }
          } else {
            if(error_callback !== undefined) {
                error_callback();
            }
          }
        };
        if(data !== undefined) {
            data = JSON.stringify(data);
        }
        request.send(data);
    };
    //------- PUBLIC GLOBAL FUNCTIONS --------------------
    $get = function(url, callback, error_callback) {
        XHR("GET", url, callback, error_callback);
    };
    $post = function(url, data, callback, error_callback) {
        XHR("POST", url, callback, error_callback, data);
    };
    $put = function(url, data, callback, error_callback) {
        XHR("PUT", url, callback, error_callback, data);
    };
    $delete = function(url, callback, error_callback) {
        XHR("DELETE", url, callback, error_callback);
    };
})();

