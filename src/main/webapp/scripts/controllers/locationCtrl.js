app.controller('locationCtrl', function($scope, $rootScope, user, $location)
{
	
    $rootScope.location = user.location;
    $scope.$watch('location', function() {
      user.location = $scope.location;
    }, true);
	
	
});