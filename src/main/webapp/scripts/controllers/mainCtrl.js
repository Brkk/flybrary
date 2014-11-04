app.controller('mainCtrl', function($scope, $rootScope, $location, $mdSidenav, $routeParams)
  {
     
      $rootScope.activeType = user.activeType;

      $rootScope.tabs = {
        maxIndex : 2,
        locked : true,
      };

      $scope.currentTab = $routeParams.currentTab;
      switch($routeParams.currentTab) 
      {
        case "offers":
          $rootScope.tabs.selectedIndex = 0;
          break;
        case "requests":
          $rootScope.tabs.selectedIndex = 1;
          break;
        case "matches":
          $rootScope.tabs.selectedIndex = 2;
          break;
      }
      $scope.$watch('tabs.selectedIndex', function() {
        switch($scope.tabs.selectedIndex)
        {
          case 0:
            $scope.activeType = 'offer';
            user.activeType = $scope.activeType;
            $location.path('/main/offers').replace();
            break;
          case 1:
            $scope.activeType = 'request';
            user.activeType = $scope.activeType;
            $location.path('/main/requests').replace();
            break;
          case 2:
            $scope.activeType = 'matches';
            user.activeType = $scope.activeType;
            $location.path('/main/matches').replace();
            break;
        };
    });

  $scope.next = function() {
      $scope.tabs.selectedIndex = Math.min( $scope.tabs.maxIndex, $scope.tabs.selectedIndex + 1) ;
  };

  $scope.previous = function() {
    $scope.tabs.selectedIndex = Math.max(0, ($scope.tabs.selectedIndex - 1));
  };

  $scope.toggleLeft = function() {
      $mdSidenav('left').toggle();
    };


  });

