
app.controller('pageCtrl', function($window, $scope, $rootScope, $location, $timeout, googleService, user, $route, $routeParams) {

	console.log("page ctrl loaded");

	if(googleService.loggedIn) {
		$location.path('/main/offers').replace();
	}
	else {
		$location.path('/login').replace();
	}

	$rootScope.login = function () {
		googleService.login().then(
		function(data) {
			$location.path('/main/offers').replace();
		},
		function(err) {
    		$location.path('/login').replace();
		})
	};

	$rootScope.gapi = gapi;
    	
	$scope.$watch('gapi.client', function(newVal, oldVal) {
		if(newVal) {
	    	googleService.handleClientLoad().then(
			function(data) {
				$location.path('/main/offers').replace();
			},
			function(err) {
				googleService.logout();
	    		$location.path('/login').replace();
			})
    	}
      });
    
    $rootScope.logout = function () {
    	googleService.logout();
    	$location.path('/login').replace();
    };

    $rootScope.renderAction = '';
    $rootScope.renderPath = [''];
    $rootScope.currentTab = 'offers';
    $rootScope.addStep = '';
    $rootScope.matchKey = '';

    render = function() {

		$rootScope.renderAction = $route.current.action;
		$rootScope.renderPath = $rootScope.renderAction.split( "." );

		// try to fill out the various parameters

		$rootScope.currentTab = ($routeParams.currentTab || '')
		$rootScope.addStep = ($routeParams.step || '');
		$rootScope.matchKey = ($routeParams.matchKey || '');

		if($rootScope.renderPath.length > 1) {
			$rootScope.currentTab = $rootScope.renderPath[1];
		}
		if($rootScope.currentTab == '') {
			$rootScope.currentTab = 'offers';
		}
    };

    $scope.$on(
	"$routeChangeSuccess",function( $currentRoute, $previousRoute ) {
		render();
		console.log("rendered");
	});  
  });

window.googleOnLoadCallback = function() {

	console.log(gapi);
}