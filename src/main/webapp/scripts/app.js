'use strict';

/**
 * @ngdoc overview
 * @name textShareApp
 * @description
 * # textShareApp
 *
 * Main module of the application.
 */

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
         isbn: '978-0-595-66825-1',
         title: '',
         topic: '',
         author: '',
         edition: '',
         condition: '',
         uid: '2',
         user: '2',
         name: '',
         date: '1412810947',
         lat: '48.462927',
         lon: '-123.311534',
         image: 'finite_elements.jpg',
         email: 'jhedin10@gmail.com'
      }
    };
}



//angular
  //.module('textShareApp', []);
angular
  .module( 'textChangrApp', [ 'ngAnimate', 'ngMaterial','directive.g+signin' ])
  .controller('BookListCtrl', function($scope, $rootScope, $http, $timeout, jsonFilter) {
      var logResult = function (str, data, status, headers)
      {
        console.log(data);
        return str + "\n\n" +
          "data: " + data + "\n\n" +
          "status: " + status + "\n\n" +
          "headers: " + jsonFilter(headers()) + "\n\n";
      };

      $scope.getBooksLength = function () {
        return $rootScope.books.length;
      };

      $rootScope.books = [];
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

      $scope.orderProp = 'title';
  })
  .filter('isOffer', function () {
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
  .filter('isRequest', function () {
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
  .controller('SidebarController', function($scope, $rootScope, $mdSidenav) {

    $scope.toggleLeft = function() {
      $mdSidenav('left').toggle();
    };

  })
  .controller('addBookCtrl', function($scope, $rootScope, $mdDialog, $http, $timeout) {
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
  .controller('tabCtrl', function($scope, $rootScope) {
    $rootScope.tabs = {
      maxIndex : 1,
      selectedIndex : 0,
      locked : true,
    };

    $rootScope.next = function() {
      $scope.tabs.selectedIndex = Math.min( $scope.tabs.maxIndex, $scope.tabs.selectedIndex + 1) ;
    };

    $rootScope.previous = function() {
      $scope.tabs.selectedIndex = Math.max(0, ($scope.tabs.selectedIndex - 1));
    };

  })
  .controller('pageCtrl', function($scope, $rootScope, $timeout) {
    $rootScope.loggedIn = 0;

    $rootScope.logout = function() {
        gapi.auth.signOut();
        $rootScope.loggedIn = 0;
        $timeout(function() {
          $scope.$apply();
        });
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
      }
      $scope.loggedIn = 1;
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


  /**
   *  Simple directive used to quickly construct `Floating Label` text fields
   *  NOTE: the label field is considered a constant specified as an attribute
   */
  .directive('tfFloat', function() {
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
