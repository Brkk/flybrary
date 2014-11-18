'use strict';


var app = angular.module( 'flybraryApp', [ 'services', 'ngAnimate', 'ngMaterial','ngRoute', 'ui.bootstrap', 'ngMap' ]);

app.config( function($routeProvider) {

    $routeProvider
      .when('/login', {
        action: 'login'
      })
      .when('/main/:currentTab', {
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
      .when('/main/requests/add/:step/:selectedKey', {
        action: 'main.requests.add'
      })
      .when('/feedback', {
        action: 'feedback'
      })
      .when('/useragreement', {
        action: 'useragreement'
      })
      .when('/faq', {
        action: 'faq'
      })
      .when('/loading', {
          action: 'loading'
      })
      .when('/setLocation', {
    	  action: 'setLocation'
      })
      .otherwise({
        redirectTo: '/login'
      });
  });