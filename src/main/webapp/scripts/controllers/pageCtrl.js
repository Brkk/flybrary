
app.controller('pageCtrl', function($window, $scope, $rootScope, $location, $timeout, googleService, user, $route, $routeParams) {

	$window.addEventListener('resize', function() {

		$rootScope.size = 'xs';

		if($window.innerWidth >= 768) {
			$rootScope.size = 'sm';
		}
		if($window.innerWidth >= 992) {
			$rootScope.size = 'md';
		}
		if($window.innerWidth >= 1200) {
			$rootScope.size = 'lg';
		}

		$rootScope.$apply();

	});

	$rootScope.$watch('size', function(newVal,oldVal){
		if(newVal != oldVal) {
			render();
		}
	});

	if(googleService.loggedIn) {
		$location.path('/main/matches').replace();
	}
	else {
		$location.path('/loading').replace();
	}

	$rootScope.login = function () {
		googleService.login().then(
		function(data) {
			if(user.loc.lat == 0 && user.loc.lon == 0)
			{
				if(!/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
					$location.path('/setLocation').replace();
				}
				else
				{
					$location.path('/setLocationMobile').replace();
				}
			}
			else
			{
				$location.path('/main/matches').replace();
			}
		},
		function(err) {
    		$location.path('/login').replace();
		})
	};

	$rootScope.gapi = gapi;
    	
	$scope.$watch('gapi.client', function(newVal, oldVal) {
		if(newVal) {
	    	googleService.handleClientLoad().then(
			function(data) {
				if(user.loc.lat == 0 && user.loc.lon == 0)
				{
					if( !/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
						$location.path('/setLocation').replace();
					}
					else
					{
						$location.path('/setLocationMobile').replace();
					}
				}
				else
				{
					$location.path('/main/matches').replace();
				}
			},
			function(err) {
				googleService.logout();
	    		$location.path('/login').replace();
			})
    	}
      });
    
    $rootScope.logout = function () {
    	googleService.logout();
    	$location.path('/login').replace();
    };
    
    $rootScope.locSet = function () {
    	$location.path('/main/matches').replace();
    };

    $rootScope.renderAction = '';
    $rootScope.renderPath = [''];
    $rootScope.currentTab = 'offers';
    $rootScope.addStep = '';
    $rootScope.matchKey = '';
    $rootScope.addbooktype = "Offer a Book";
    $rootScope.is_or_should = "is";
    $rootScope.showAdd =false;

    render = function() {

		$rootScope.renderAction = $route.current.action;
		$rootScope.renderPath = $rootScope.renderAction.split( "." );

		// try to fill out the various parameters

		$rootScope.currentTab = ($routeParams.currentTab || '')
		$rootScope.addStep = ($routeParams.step || '');
		$rootScope.matchKey = ($routeParams.matchKey || '');
		$rootScope.selectedKey = ($routeParams.selectedKey || '');

		if($rootScope.renderPath.length > 1) {
			$rootScope.currentTab = $rootScope.renderPath[1];
		}
		if($rootScope.currentTab == '') {
			$rootScope.currentTab = 'offers';
		}

		switch($rootScope.currentTab) {
			case 'matches':
				$rootScope.showAdd = false;
				if($rootScope.tabs)
					$rootScope.tabs.selectedIndex = 0;
				break;
			case 'offers':
				$rootScope.addbooktype = "Offer a Book";
				$rootScope.is_or_should = "is";
				$rootScope.showAdd = true;
				if($rootScope.tabs)
					$rootScope.tabs.selectedIndex = 1;
				break;
			case 'requests':
				$rootScope.addbooktype = "Request a Book";
				$rootScope.is_or_should = "should";
				$rootScope.showAdd = true;
				if($rootScope.tabs)
					$rootScope.tabs.selectedIndex = 2;
				break;
		}
    };

    $scope.$on(
	"$routeChangeSuccess",function( $currentRoute, $previousRoute ) {
		render();
	});  
  });

window.googleOnLoadCallback = function() {

}