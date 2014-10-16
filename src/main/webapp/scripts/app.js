'use strict';

/**
 * @ngdoc overview
 * @name textShareApp
 * @description
 * # textShareApp
 *
 * Main module of the application.
 */

function DialogController($scope, $rootScope, $materialDialog) {
    $scope.hide = function() {
      $materialDialog.hide();
    };
    $scope.addBook = function(addBook) {
      $materialDialog.hide($scope.book);
    };
    $rootScope.book = {
       type: 'offer',
       isbn: '978-0-595-66825-1',
       title: '',
       topic: '',
       author: '',
       version: '',
       condition: '',
       user: 1,
       date: 1412810947,
       lat: 48.462927,
       lon: -123.311534,
       image: 'images/finite_elements.jpg'
    };
}



//angular
  //.module('textShareApp', []);
angular
  .module( 'textShareApp', [ 'ngAnimate', 'ngMaterial' ])
  .controller('BookListCtrl', function($scope, $rootScope) {
      $rootScope.books = [
          {
              'type': 'request',
              'isbn': '978-0-595-66825-1',
              'title': 'A - A First Course in the Finite Element Method',
              'topic': 'mechanical engineering',
              'author': 'Sergway',
              'version': '5',
              'condition': 'good',
              'user': 1,
              'date': 14128109451,
              'lat': 48.462927,
              'lon': -123.311534,
              'image': 'images/finite_elements.jpg'
          },
          {
              'type': 'offer',
              'isbn': '978-0-595-66825-1',
              'title': 'B - Physics for Scientists and Engineers',
              'topic': 'mechanical engineering',
              'author': 'Logan',
              'version': '5',
              'condition': 'good',
              'user': 1,
              'date': 1412810950,
              'lat': 48.462927,
              'lon': -123.311534,
              'image': 'images/finite_elements.jpg'
          },
          {
              'type': 'request',
              'isbn': '978-0-595-66825-1',
              'title': 'C - A First Course in the Finite Element Method',
              'topic': 'mechanical engineering',
              'author': 'Logan',
              'version': '5',
              'condition': 'good',
              'user': 1,
              'date': 1412810949,
              'lat': 48.462927,
              'lon': -123.311534,
              'image': 'images/finite_elements.jpg'
          },
          {
              'type': 'offer',
              'isbn': '978-0-595-66825-1',
              'title': 'D - First Course in the Finite Element Method',
              'topic': 'mechanical engineering',
              'author': 'Sergway',
              'version': '5',
              'condition': 'good',
              'user': 1,
              'date': 1412810948,
              'lat': 48.462927,
              'lon': -123.311534,
              'image': 'images/finite_elements.jpg'
          }
      ];
      $scope.orderProp = 'title';
  })
  .controller('SidebarController', function($scope, $rootScope, $materialSidenav) {
      $scope.openLeftMenu = function() {
        $materialSidenav('left').toggle();
      };
  })
  .controller('addBookCtrl', function($scope, $rootScope, $materialDialog, $timeout) {
    $scope.dialog = function(ev) {
      $materialDialog.show({
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
        $timeout(function() {$scope.books.push(addBook)});
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
        '<material-input-group ng-disabled="isDisabled">' +
          '<label for="{{fid}}">{{label}}</label>' +
          '<material-input id="{{fid}}" ng-model="value">' +
        '</material-input-group>'
    };
});



