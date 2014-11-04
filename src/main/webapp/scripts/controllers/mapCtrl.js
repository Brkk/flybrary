app.controller('mainCtrl', function($scope, $rootScope, user, $location)
{

  // todo: add the api calls to update the location => needs more from berk

  $scope.location.latlng = new google.maps.LatLng(user.location.lat, user.location.lon);
  $scope.$watch('location.radius', function() {
    user.location.radius = $scope.location.radius;
    $scope.circle.setRadius(user.location.radius);
  });
  $scope.$watch('location.latlng', function() {
      user.location.lat = $scope.location.latlng.lat();
      user.location.lon = $scope.location.latlng.lon();
      
      $scope.marker.setPosition($scope.location.latlng);
      $scope.circle.setCenter($scope.location.latlng);
      $scope.map.pan($scope.location.latlng); 
  }, true);

  $scope.map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
  $scope.geocoder = new google.maps.Geocoder();
  $scope.marker = new google.maps.Marker({
    map: $scope.map,
    position: new google.maps.LatLng( $scope.location.latlng)
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
    map = 
  }

  function codeAddress() {
    var address = document.getElementById('address').value;
    geocoder.geocode( { 'address': address}, function(results, status) {
      if (status == google.maps.GeocoderStatus.OK) {
        map.setCenter(results[0].geometry.location);
        marker.setPosition(results[0].geometry.location);
        circle.setCenter(results[0].geometry.location);
      } else {
        alert('Geocode was not successful for the following reason: ' + status); // fill the bar with location not found/ add a toast or something about it
      }
    });
  }
}