var login = angular.module('loginSvc', ['userSvc'])

.service('googleService', function ($http, $q, user, $location) {
    var clientId = '642821490386-5e5tfhghkcvsmjauaeu0mbnlrnjnl30n.apps.googleusercontent.com',
        apiKey = 'AIzaSyA1duUBotiNnlcHHqnH2oIWwM4JhyozhoQ',
        scopes = 'https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/userinfo.email',
        domain = '',//'flybrary.ca',
        cookies = 'single_host_origin',
        deferred = $q.defer();

    this.login = function () {
        gapi.auth.authorize({ 
            client_id: clientId, 
            scope: scopes, 
            immediate: false, 
            cookie_policy: cookies,
            hd: domain 
        }, this.handleAuthResult);

        return deferred.promise;
    }

    this.logout = function () {
        gapi.auth.signOut();
    }

    this.handleClientLoad = function () {
        gapi.client.setApiKey(apiKey);
        gapi.auth.init(function () { });
        window.setTimeout(checkAuth, 1);
    };

    this.checkAuth = function() {
        gapi.auth.authorize({ 
            client_id: clientId, 
            scope: scopes, 
            immediate: true, 
            cookie_policy: cookies,
            hd: domain 
        }, this.handleAuthResult);
    };

    this.handleAuthResult = function(authResult) {
        if (authResult && !authResult.error) {
            var data = {};
            gapi.client.load('oauth2', 'v2', function () {
                var request = gapi.client.oauth2.userinfo.get();
                request.execute(function (resp) {
                    user.email = resp.email;
                    user.uid = resp.id;
                    user.name = resp.name;   
                });
            });
            deferred.resolve(data);
        } else {
            deferred.reject('error');
        }
    };

    this.handleAuthClick = function(event) {
        gapi.auth.authorize({ 
            client_id: clientId, 
            scope: scopes, 
            immediate: false, 
            cookie_policy: cookies,
            hd: domain 
        }, this.handleAuthResult);
        return false;
    };

    });