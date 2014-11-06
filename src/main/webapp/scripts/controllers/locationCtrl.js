app.controller('locationCtrl', function($scope)
{
	
   /* $rootScope.location = user.location;
    $scope.$watch('location', function() {
      user.location = $scope.location;
    }, true); */
    
	$scope.lat = "0";
    $scope.lng = "0";
    $scope.error = "";

    $scope.showPosition = function (position) {
        $scope.lat = position.coords.latitude;
        $scope.lng = position.coords.longitude;
        $scope.$apply();
    }

    $scope.showError = function (error) {
        switch (error.code) {
            case error.PERMISSION_DENIED:
                $scope.error = "User denied the request for Geolocation."
                break;
            case error.POSITION_UNAVAILABLE:
                $scope.error = "Location information is unavailable."
                break;
            case error.TIMEOUT:
                $scope.error = "The request to get user location timed out."
                break;
            case error.UNKNOWN_ERROR:
                $scope.error = "An unknown error occurred."
                break;
        }
        $scope.$apply();
    }

    $scope.getLocation = function () {
        if (navigator.geolocation) {
        	var options = {timeout:60000};
            navigator.geolocation.getCurrentPosition($scope.showPosition, $scope.showError, options);
        }
        else {
            $scope.error = "Geolocation is not supported by this browser.";
        }
    }
   
});