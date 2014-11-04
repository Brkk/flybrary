app.service('googleService', function ($http, $q, user) {
    var clientId = '642821490386-5e5tfhghkcvsmjauaeu0mbnlrnjnl30n.apps.googleusercontent.com',
        apiKey = 'AIzaSyA1duUBotiNnlcHHqnH2oIWwM4JhyozhoQ',
        scopes = 'https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/userinfo.email',
        domain = 'flybrary.ca',
        deferred = $q.defer();

    this.login = function () {
        gapi.auth.authorize({ 
            client_id: clientId, 
            scope: scopes, 
            immediate: false, 
            hd: domain 
        }, this.handleAuthResult);

        return deferred.promise;
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
            hd: domain 
        }, this.handleAuthResult);
    };

    this.handleAuthResult = function(authResult) {
        if (authResult && !authResult.error) {
            var data = {};
            gapi.client.load('oauth2', 'v2', function () {
                var request = gapi.client.oauth2.userinfo.get();
                request.execute(function (resp) {
                    user.email = data.emails[0].value;
                    user.uid = resp.id;
                    user.name = resp.displayName;
                    
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
            hd: domain 
        }, this.handleAuthResult);
        return false;
    };

    })