'use strict';


var app = angular.module( 'flybraryApp', [ 'services', 'ngAnimate', 'ngMaterial','ngRoute', 'ui.bootstrap' ]);

app.config( function($routeProvider) {
    
    $routeProvider
      .when('/login', {
        templateUrl: 'views/login.html',
        controller: ''
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
  });

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