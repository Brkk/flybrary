
app.controller('pageCtrl', function($scope, $rootScope, $timeout, $location, googleService, user) {

	$rootScope.login = function () {
		googleService.login();
	};

    //googleService.handleClientLoad();
    
    $rootScope.logout = function () {
    	googleService.logout();
    };
   
  });