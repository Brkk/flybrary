app.controller('mainCtrl', function($scope, $rootScope, user, $location, $mdSidenav)
  {
      console.log("main ctrl loaded");
      $rootScope.actionType = user.actionType;

      
      $rootScope.tabs = {
        maxIndex: 2,
        locked: true,
        selectedIndex: 0
      };

      switch($rootScope.currentTab) 
      {
        case "matches":
          $scope.tabs.selectedIndex = 0;
          break;
        case "offers":
          $scope.tabs.selectedIndex = 1;
          break;
        case "requests":
          $scope.tabs.selectedIndex = 2;
          break;
      }
      $scope.$watch('tabs.selectedIndex', function(newVal, oldVal) {
        if(newVal != oldVal) {
          switch($scope.tabs.selectedIndex)
          {
            case 0:
              $scope.actionType = 'matches';
              user.actionType = $scope.actionType;
              if($rootScope.currentTab != 'matches')
                $location.path('/main/matches').replace();
              break;
            case 1:
              $scope.actionType = 'offer';
              user.actionType = $scope.actionType;
              if($rootScope.currentTab != 'offers')
                $location.path('/main/offers').replace();
              break;
            case 2:
              $scope.actionType = 'request';
              user.actionType = $scope.actionType;
              if($rootScope.currentTab != 'requests')
                $location.path('/main/requests').replace();
              break;
          };
        }
    }, true);

  $rootScope.next = function() {
    $scope.tabs.selectedIndex = Math.min( $scope.tabs.maxIndex, $scope.tabs.selectedIndex + 1) ;
  };

  $rootScope.previous = function() {
    $scope.tabs.selectedIndex = Math.max(0, ($scope.tabs.selectedIndex - 1));
  };

  $rootScope.toggleLeft = function() {
      $mdSidenav('left').toggle();
    };
  });