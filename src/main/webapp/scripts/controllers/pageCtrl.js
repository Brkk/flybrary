
app.controller('pageCtrl', function($scope, $rootScope, $location, $timeout, googleService, user) {

	$rootScope.login = function () {
		googleService.login().then(
		function(data) {
			$location.path('/main/offers').replace();
		},
		function(err) {

		})
	};
    	
	$rootScope.$watch('googleOnLoadCallback', function() {
    	googleService.handleClientLoad()
      });
    
    $rootScope.logout = function () {
    	googleService.logout();
    	$location.path('/login').replace();
    };
   
  });