
app.controller('pageCtrl', function($scope, $rootScope, $timeout, $location, googleService, user) {
    
  $scope.login = function () {
    googleService.login();
  };

    googleService.handleClientLoad();
    
    $scope.checkGoogleAuth = function () {
      googleService.checkAuth();
    };

    $scope.checkGoogleAuth();    
   
  });