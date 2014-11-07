'use strict';


var app = angular.module( 'flybraryApp', [ 'services', 'ngAnimate', 'ngMaterial','ngRoute', 'ui.bootstrap', 'ngMap' ]);

app.config( function($routeProvider) {

    $routeProvider
      .when('/login', {
        //templateUrl: 'views/login.html',
        //controller: '',
        action: 'login'
      })
      .when('/main/:currentTab', {
        //templateUrl: 'views/main.html',
        //controller: 'mainCtrl',
        action: 'main'
      })
      .when('/main/matches/:matchKey', {
        action: 'main.matches.book'
      })
      .when('/main/offers/add/:step', {
        action: 'main.offers.add'
      })
      .when('/main/requests/add/:step', {
        action: 'main.requests.add'
      })
      .when('/feedback', {
        //templateUrl: 'views/feedback.html',
        //controller: ''
        action: 'feedback'
      })
      .when('/useragreement', {
        //templateUrl: 'views/useragreement.html',
        //controller: ''
        action: 'useragreement'
      })
      .when('/faq', {
        //templateUrl: 'views/faq.html',
        //controller: ''
        action: 'faq'
      })
      .when('/loading', {
        //templateUrl: 'views/loading.html',
        //controller: ''
          action: 'loading'
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