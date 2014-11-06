app.controller('mapCtrl', function($scope, $rootScope, user, $location)
{
	
	
	
	
});
/*
  // todo: add the api calls to update the loc => needs more from berk

  $scope.loc.latlng = new google.maps.LatLng(user.loc.lat, user.loc.lon);
  $scope.$watch('loc.radius', function() {
    user.loc.radius = $scope.loc.radius;
    $scope.circle.setRadius(user.loc.radius);
  });
  $scope.$watch('loc.latlng', function() {
      user.loc.lat = $scope.loc.latlng.lat();
      user.loc.lon = $scope.loc.latlng.lon();
      
      $scope.marker.setPosition($scope.loc.latlng);
      $scope.circle.setCenter($scope.loc.latlng);
      $scope.map.pan($scope.loc.latlng); 
  }, true);

  $scope.map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
  $scope.geocoder = new google.maps.Geocoder();
  $scope.marker = new google.maps.Marker({
    map: $scope.map,
    position: new google.maps.LatLng( $scope.loc.latlng)
    });

    var mapOptions = {
      zoom: 8,
      center: latlng
    }
    var 
    var circle = new google.maps.Circle({
    	map:map,
    	strokeColor: '#FF0000',
    	strokeOpacity: 0.8,
    	strokeWeight: 2,
    	fillColor: '#FF0000',
    	fillOpacity: 0.35,
    	center: new google.maps.LatLng(-34.397, 150.644)
    	radius: 15
    })
    //map = 
  }

  function codeAddress() {
    var address = document.getElementById('address').value;
    geocoder.geocode( { 'address': address}, function(results, status) {
      if (status == google.maps.GeocoderStatus.OK) {
        map.setCenter(results[0].geometry.loc);
        marker.setPosition(results[0].geometry.loc);
        circle.setCenter(results[0].geometry.loc);
      } else {
        alert('Geocode was not successful for the following reason: ' + status); // fill the bar with loc not found/ add a toast or something about it
      }
    });
  }
}*/