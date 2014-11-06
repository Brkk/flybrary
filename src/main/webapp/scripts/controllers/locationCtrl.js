app.controller('locationCtrl', function($rootScope ,$scope, user)
{
	
    $rootScope.location = user.location;
    
    $scope.$watch('location', function() {
      user.location = $scope.location;
    }, true); 

    $scope.setPosition = function (position) {
        user.location.lat = position.coords.latitude;
        user.location.lon = position.coords.longitude;
        user.updateUserLocation();
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
            navigator.geolocation.getCurrentPosition($scope.setPosition, $scope.showError, options);
        }
        else {
            $scope.error = "Geolocation is not supported by this browser.";
        }
    }
   
});