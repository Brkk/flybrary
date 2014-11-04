
app.controller('pageCtrl', function($scope, $rootScope, $timeout, $location, googleService, user) {
    
	$rootScope.login = function () {
		googleService.login();
	};

    googleService.handleClientLoad();
    
    $rootScope.checkGoogleAuth = function () {
      googleService.checkAuth();
    };

    $scope.checkGoogleAuth();    
   
  });