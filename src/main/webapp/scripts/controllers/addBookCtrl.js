app.controller('addBookCtrl', function ($scope, $rootScope, $mdDialog, user) {
    
    $rootScope.newBookProperties = user.activeBookProperties;
    $scope.$watch('newbookProperties', function() {
      user.activeBookProperties = $scope.newBookProperties;
    }, true);
    $scope.hide = function() {
      $mdDialog.hide(false);
    };
    $scope.addBookClicked = function(addBook) {
      $mdDialog.hide(true);
    };
});


app.controller('dialogCtrl', function($scope, $mdDialog, $http, $timeout, user) {
    $scope.dialog = function(ev) {
      $mdDialog.show({
        templateUrl: 'views/addBook.html',
        targetEvent: ev,
        controller: 'addBookCtrl'
      }).then(function(addBookConfirmed) {
          if (addBookConfirmed){
            user.addBook().then(
              function(data)
              {

              },
              function(err)
              {
                  console.log('Failed: ' + err);
              });
          }
      
      });
    };
  });

