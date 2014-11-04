
app.controller('pageCtrl', function($scope, $rootScope, $location, $timeout, googleService, user) {

	$rootScope.login = function () {
		googleService.login().then(
		function(data) {
			$location.path('/main').replace();
		},
		function(err) {

		})
	};

    //googleService.handleClientLoad();
    
    $rootScope.logout = function () {
    	googleService.logout();
    	$location.path('/login').replace();
    };
   
  });