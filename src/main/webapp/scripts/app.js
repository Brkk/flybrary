'use strict';

/**
 * @ngdoc overview
 * @name textShareApp
 * @description
 * # textShareApp
 *
 * Main module of the application.
 */




var app = angular.module( 'textChangrApp', [ 'ngAnimate', 'ngMaterial','directive.g+signin','ngRoute', 'ui.bootstrap' ]);

app.config( function($routeProvider) {
    $routeProvider
      .when('/login', {
        templateUrl: 'views/login.html',
        controller: ''
      })
      .when('/main', {
        templateUrl: 'views/main.html',
        controller: 'mainCtrl',
        redirectTo: '/main/offers'
      })
      .when('/main/:currentTab', {
        templateUrl: 'views/main.html',
        controller: 'mainCtrl'
      })
      .when('/feedback', {
        templateUrl: 'views/feedback.html',
        controller: ''
      })
      .when('/useragreement', {
        templateUrl: 'views/useragreement.html',
        controller: ''
      })
      .when('/faq', {
        templateUrl: 'views/faq.html',
        controller: ''
      })
      .otherwise({
        redirectTo: '/login'
      });
  })

/* Controllers  Start */

app.controller('mainCtrl', function($scope, $rootScope, $location, $routeParams)
  {
      $rootScope.tabs = {
        maxIndex : 1,
        locked : true,
      };
      $scope.currentTab = $routeParams.currentTab;
      switch($routeParams.currentTab) 
      {
        case "offers":
          $rootScope.tabs.selectedIndex = 0;
          break;
        case "requests":
          $rootScope.tabs.selectedIndex = 1;
          break;
        case "textchanges":
          $rootScope.tabs.selectedIndex = 2;
          break;
      }
      $scope.$watch('tabs.selectedIndex', function() {
        switch($scope.tabs.selectedIndex)
        {
          case 0:
            $location.path('/main/offers').replace();
            break;
          case 1:
            $location.path('/main/requests').replace();
            break;
          case 2:
            $location.path('/main/textchanges').replace();
            break;
        };
    });

  })

app.controller('pageCtrl', function($scope, $rootScope, $timeout, $location, googleService) {
    

    $rootScope.loggedIn = 0;
    $rootScope.books = [];

    /*googleService.handleClientLoad();
    
    $scope.checkGoogleAuth = function () {
      googleService.checkAuth().then(function (data) {
          console.log(data);
          console.log(userInfo);

          $rootScope.book = {
            type: 'offer',
            isbn: '978-0-595-66825-1',
            title: '',
            topic: '',
            author: '',
            edition: '',
            condition: '',
            uid: data.id,
            name: data.displayName,
            date: '1412810947',
            lat: '48.462927',
            lon: '-123.311534',
            image: '../images/finite_elements.jpg',
            email: data.email
          };
          $location.path('/main/offers').replace();
          $scope.$apply();
      }, function (err) {
          console.log('Failed: ' + err);
      });
    };

    $scope.checkGoogleAuth();*/


  
    $rootScope.logout = function() {   
        $location.path('/login').replace();
        gapi.auth.signOut();
        $scope.$apply();
      };

    $rootScope.userInfoCallback = function(userInfo) {
      console.log(userInfo);

      $rootScope.book = {
       type: 'offer',
       isbn: '978-0-595-66825-1',
       title: '',
       topic: '',
       author: '',
       edition: '',
       condition: '',
       uid: userInfo.id,
       name: userInfo.displayName,
       date: '1412810947',
       lat: '48.462927',
       lon: '-123.311534',
       image: '../images/finite_elements.jpg',
       email: userInfo.emails[0].value
      };
      $location.path('/main/offers').replace();
      $scope.$apply();
    };

    $scope.$on('event:google-plus-signin-success', function (event,authResult) {

      gapi.client.request(
        {
            'path':'/plus/v1/people/me',
            'method':'GET',
            'callback': $scope.userInfoCallback
        }
      );

      
    });
    $rootScope.$on('event:google-plus-signin-failure', function (event,authResult) {
      // Auth failure or signout detected
    });
  })

app.controller('BookListCtrl', function($scope, $http, $timeout, jsonFilter) {
      
      $scope.$watch('book.uid', function() {
        var req = {'uid':$scope.book.uid};
        $http.post("resources/retrieve", req, null)
            .success(function (data, status, headers, config)
            {
              $timeout(function() {
                $scope.books = data;
              });
            })
            .error(function (data, status, headers, config)
            {

            });
        });

      $scope.orderProp = 'title';
  })

app.controller('SidebarController', function($scope, $mdSidenav) {
    
    $scope.toggleLeft = function() {
      $mdSidenav('left').toggle();
    };

  })

function DialogController($scope, $rootScope, $mdDialog) {
    $scope.hide = function() {
      $mdDialog.hide();
    };
    $scope.addBook = function(addBook) {
      $mdDialog.hide($scope.book);
    };
    if(!$scope.book)
    {
      $rootScope.book = {
         type: 'offer',
         isbn: '',
         title: '',
         topic: '',
         author: '',
         edition: '',
         condition: '',
         uid: '',
         name: ''
      }
    };
}


app.controller('typeAheadCtrl', function($scope, $http) {
      // $scope.selected = '';
      
          $scope.updateList = function(title){
              return $http({
                method: 'GET',
                url: 'https://www.googleapis.com/books/v1/volumes?q='+title+'&maxResults=5'
              }).then(function ($response)
              {
                // var result = [];
                // $scope.booksList = $response.data['items'];
                // angular.forEach($scope.booksList, function(obj){
                      // result.push(obj['volumeInfo']['title']);
                // })
                return $response.data['items'];
              });
        }

          $scope.onSelect = function (item) {
              console.log($scope.asyncSelected);
              $scope.book.author = $scope.asyncSelected['volumeInfo']['authors'][0];
              $scope.book.isbn = $scope.asyncSelected['volumeInfo']['industryIdentifiers'][0]['identifier'];
          };
})

app.controller('addBookCtrl', function($scope, $mdDialog, $http, $timeout) {
    $scope.dialog = function(ev) {
      $mdDialog.show({
        templateUrl: 'views/addBook.html',
        targetEvent: ev,
        controller: DialogController
      }).then(function(addBook) {

        if($scope.tabs.selectedIndex == 0)
        {
          $scope.book.type = 'offer';
        }
        else
        {
          $scope.book.type = 'request';
        }

        console.log(addBook);

        $http.post("resources/add", addBook, null)
          .success(function (data, status, headers, config)
          {
            $http.post("resources/retrieve", {'uid':$scope.book.uid}, null)
              .success(function (data, status, headers, config)
              {
                  $timeout(function() {
                    $scope.books = data;
                  });
              })
              .error(function (data, status, headers, config)
              {

              });
            })
          .error(function (data, status, headers, config)
          {
          }
        );


      });
    };
  })





app.controller('tabCtrl', function($scope, $location, $timeout) {

    $scope.next = function() {
      $scope.tabs.selectedIndex = Math.min( $scope.tabs.maxIndex, $scope.tabs.selectedIndex + 1) ;
    };

    $scope.previous = function() {
      $scope.tabs.selectedIndex = Math.max(0, ($scope.tabs.selectedIndex - 1));
    };

  })


  /* Controllers  End */



/*  Services Start   */


app.service('googleService', function ($http, $q) {
    var clientId = '642821490386-5e5tfhghkcvsmjauaeu0mbnlrnjnl30n.apps.googleusercontent.com',
        apiKey = '',
        scopes = 'https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/userinfo.email',
        domain = '',
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
                    data.email = data.emails[0].value;
                    data.uid = resp.id;
                    data.name = resp.displayName;
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


/*  Services End   */
  

/*    Filters Start    */  
app.filter('isOffer', function () {
    return function (items) {
      var filtered = [];
      for (var i = 0; i < items.length; i++) {
        var item = items[i];
        if (item.propertyMap.type == 'offer') {
          filtered.push(item);
        }
      }
      return filtered;
    };
  })
app.filter('isRequest', function () {
    return function (items) {
      var filtered = [];
      for (var i = 0; i < items.length; i++) {
        var item = items[i];
        if (item.propertyMap.type == 'request') {
          filtered.push(item);
        }
      }
      return filtered;
    };
  })
  .filter('isMatch', function () {
    return function (items) {
      var filtered = [];
      for (var i = 0; i < items.length; i++) {
        var item = items[i];
        if (item.propertyMap.matched == 'yes') {
          filtered.push(item);
        }
      }
      return filtered;
    };
  })
  
/*    Filters End    */


  /**
   *  Simple directive used to quickly construct `Floating Label` text fields
   *  NOTE: the label field is considered a constant specified as an attribute
   */
app.directive('tfFloat', function() {
    return {
      restrict: 'E',
      replace: true,
      scope : {
        fid : '@?',
        value : '='
      },
      compile : function() {
        return {
          pre : function(scope, element, attrs) {
            // transpose `disabled` flag
            if ( angular.isDefined(attrs.disabled) ) {
              element.attr('disabled', true);
              scope.isDisabled = true;
            }

            // transpose the `label` value
            scope.label = attrs.label || '';
            scope.fid = scope.fid || scope.label;

            // transpose optional `type` and `class` settings
            element.attr('type', attrs.type || 'text');
            element.attr('class', attrs.class );
          }
        };
      },
      template:
        '<md-input-group ng-disabled="isDisabled">' +
          '<label for="{{fid}}">{{label}}</label>' +
          '<md-input id="{{fid}}" ng-model="value">' +
        '</md-input-group>'
    };
});