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
      .when('/main/requests/add/:step/:selectedKey', {
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