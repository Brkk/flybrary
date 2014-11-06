var login = angular.module('loginSvc', ['userSvc'])

.service('googleService', function ($http, $q, user, $location) {
    var clientId = '642821490386-5e5tfhghkcvsmjauaeu0mbnlrnjnl30n.apps.googleusercontent.com',
        apiKey = 'AIzaSyA1duUBotiNnlcHHqnH2oIWwM4JhyozhoQ',
        scopes = 'https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/userinfo.email',
        domain = '',//'flybrary.ca',
        cookies = 'single_host_origin',
        deferred = $q.defer();

    this.loggedIn = false;


    this.login = function () {
        gapi.auth.authorize({ 
            client_id: clientId, 
            scope: scopes, 
            immediate: false, 
            cookie_policy: cookies,
            hd: domain 
        }, this.handleAuthResult);

        return deferred.promise;
    };

    this.logout = function () {
        gapi.auth.signOut();
    };

    this.handleClientLoad = function () {
        gapi.client.setApiKey(apiKey);
        gapi.auth.init(function () { });
        this.checkAuth();
        return deferred.promise;
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
                    this.loggedIn = true;              
                    deferred.resolve(data); 
                   /* user.getUser().then(function(data){
                        //add user location to location
                    	location.lat = data.lat;
                    	location.lon = data.lon;
                    	location.address = data.location;
                        // start loading the book list
                        deferred.resolve(data);
                    }, function(err){

                    });  */
                });
            });
        } else {
            this.loggedIn = false;
            deferred.reject('error');
        }
    };
  });

