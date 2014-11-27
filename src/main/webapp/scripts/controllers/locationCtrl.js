app.controller('locCtrl', function($rootScope ,$scope, user)
{
	
    $rootScope.loc = user.loc;
    $rootScope.loc.sliderRadius = user.loc.radius;

    $scope.$watch('loc.radius', function() {
        if(user.loc.set) {
            user.loc.radius = $scope.loc.radius;
            user.updateUserRadius();
        }
    });
    
    $scope.$watch('loc', function() {
        if(user.loc.set)
        {
            if(!user.loc.inDB) {
                $scope.getLoc();
                $scope.loc.set = true;
                $scope.loc.inDB = true;
            }

            user.loc = $scope.loc;
            user.loc.set = true;
            $scope.loc.radius = 1000*$scope.loc.sliderRadius;


            switch($scope.loc.sliderRadius) {
                case 5:
                    $scope.loc.zoom = 11;
                    break;
                case 10:
                    $scope.loc.zoom = 10;
                    break;
                case 15:
                    $scope.loc.zoom = 9;
                    break;
                case 20:
                    $scope.loc.zoom = 9;
                    break;
                case 25:
                    $scope.loc.zoom = 8;
                    break;
                case 30:
                    $scope.loc.zoom = 8;
                    break;
            }
        }

    }, true); 

    $rootScope.setPosition = function (position) {
        user.loc.lat = position.coords.latitude;
        user.loc.lon = position.coords.longitude;
        user.updateUserloc();
    }

    $rootScope.showError = function (error) {
        switch (error.code) {
            case error.PERMISSION_DENIED:
                $scope.error = "User denied the request for Geoloc."
                break;
            case error.POSITION_UNAVAILABLE:
                $scope.error = "loc information is unavailable."
                break;
            case error.TIMEOUT:
                $scope.error = "The request to get user loc timed out."
                break;
            case error.UNKNOWN_ERROR:
                $scope.error = "An unknown error occurred."
                break;
        }
        $scope.$apply();
    }

    $rootScope.getLoc = function () {
        if (navigator.geolocation) {
        	var options = {timeout:60000};
            navigator.geolocation.getCurrentPosition($scope.setPosition, $scope.showError, options);
        }
        else {
            $scope.error = "Geoloc is not supported by this browser.";
        }
    }
    
  /*  $rootScope.geocoder = new google.maps.Geocoder();
    $rootScope.address = 'Victoria';
	$rootScope.codeAddress = function() {
		  $rootScope.geocoder.geocode( { 'address': $rootScope.address}, function(results, status) {
		    if (status == google.maps.GeocoderStatus.OK) {
		      $scope.loc.lat = (results[0].geometry.location.k);
		      $scope.loc.lon = (results[0].geometry.location.B);
		      $rootScope.setPosition({coords:{latitude:user.loc.lat , longitude: user.loc.lon}});
              $scope.loc.set = true;
              $scope.loc.inDB = true;
		    } else {
		      alert('Geocode was not successful for the following reason: ' + status);
		    }
		  });
		}*/
});