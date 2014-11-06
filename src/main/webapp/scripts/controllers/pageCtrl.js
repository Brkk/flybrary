
app.controller('pageCtrl', function($window, $scope, $rootScope, $location, $timeout, googleService, user) {

	/*if(googleService.loggedIn) {
		$location.path('/main/offers').replace();
	}
	else {
		$location.path('/login').replace();
	}*/

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
   
  });

window.googleOnLoadCallback = function() {

	console.log(gapi);
}