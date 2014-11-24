app.controller('addBookCtrl', function ($scope, $rootScope, $mdDialog,$mdToast, user, gBooks, $location) {
  
  $rootScope.search = {
    title: "",
    author: "",
    ISBN: ""
  };

  $rootScope.toasttype = {
		    typ: '',
		    msg: ''
		  };

  $rootScope.selected = {
    title: "sjdkfjw",
    author: "asdfaf",
    ISBN: "092292",
    desc: "sdsdhj'fsbcjhsdcsidcsdicswe",
    key: "",
    image: "http://books.google.com/books/content?id=g_ybia0hGw8C&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
    bigImage: "http://books.google.com/books/content?id=g_ybia0hGw8C&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
    condition: "1",
    edition: ""
  };

  $rootScope.searchList = [];

  $rootScope.hoveredKey = "";

  $scope.hide = function() {
    $mdDialog.hide(false);
    $location.path('/main/' + $rootScope.currentTab).replace();
  };
  

  $scope.toastPosition = {
    bottom: true,
    top: false,
    left: false,
    right: true
  };

  $scope.getToastPosition = function() {
    return Object.keys($scope.toastPosition)
      .filter(function(pos) { return $scope.toastPosition[pos]; })
      .join(' ');
  };
  
  $rootScope.addBookClicked = function() {
    
 	$scope.alert = '';
    user.activeBookProperties.title = $rootScope.selected.title;
    user.activeBookProperties.author = $rootScope.selected.author;
    user.activeBookProperties.isbn = $rootScope.selected.ISBN;
    user.activeBookProperties.image = $rootScope.selected.image;
    user.activeBookProperties.condition = $rootScope.selected.condition;
 	
    if($rootScope.selected.edition) {
    	if($rootScope.selected.edition[0] < '0' || $rootScope.selected.edition[0] > '9') {
            $rootScope.toasttype.msg = "Edition must be a number!";

            $mdToast.show({
              //controller: ToastCtrl,
              templateUrl: 'views/toast.html',
              hideDelay: 10000,
              position: $scope.getToastPosition()
            });
    	} else {
    		user.activeBookProperties.edition = '';
    		for (i = 0; i < $rootScope.selected.edition.length; i++) { 
    			if($rootScope.selected.edition[i] >= '0' && $rootScope.selected.edition[i] <= '9')
    				user.activeBookProperties.edition += $rootScope.selected.edition[i];
    			else
    				break;
    		}
    		
    		$mdDialog.hide(true);
    	    $location.path('/main/' + $rootScope.currentTab).replace();
    	}
    } else { 
            $rootScope.toasttype.msg = "Please enter an edition";

            $mdToast.show({
              //controller: ToastCtrl,
              templateUrl: 'views/toast.html',
              hideDelay: 10000,
              position: $scope.getToastPosition()
            });
    	
    }
  };

// fills the list/changes page/selects the first book
$rootScope.searchISBN = function() {

  gBooks.isbn = $scope.search.ISBN.replace(/\D/g,'');
  gBooks.doSearchISBN().then(
    function(data){
    	$rootScope.searchList = data;
    	if( !/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
    		$rootScope.selectKey(data[0]);
    	}
    	$location.path('/main/' + $rootScope.currentTab + '/add/select').replace();
    }, 
    function (err) {
      console.log('Failed: ' + err);
    });
  
};

$rootScope.searchTitleAuthor = function() {
  gBooks.author = $scope.search.author;
  gBooks.title = $scope.search.title;

  gBooks.doSearchTitleAuthor().then(
    function(data){
      $rootScope.searchList = data;
      if( !/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
    	  $rootScope.selectKey(data[0]);
      }
      $location.path('/main/' + $rootScope.currentTab + '/add/select').replace();
    }, 
    function (err) {
      console.log('Failed: ' + err);
    });

};

$rootScope.backToSearch = function() {
  $location.path('/main/' + $rootScope.currentTab + '/add/search').replace();
}

$rootScope.backToSelect = function() {
	  $location.path('/main/' + $rootScope.currentTab + '/add/select').replace();
}

$rootScope.backToMain = function() {
	  $mdDialog.hide(false);
	  $location.path('/main/' + $rootScope.currentTab).replace();
}

$rootScope.moveToSelected = function() {
	$location.path('/main/' + $rootScope.currentTab + '/add/selected').replace();
}


$rootScope.selectKey = function(theBook) {
  var i = $rootScope.searchList.indexOf(theBook);

  if(i < 0)
    return;
  
  $rootScope.selected.title = $rootScope.searchList[i].title;
  $rootScope.selected.author = $rootScope.searchList[i].author;
  $rootScope.selected.ISBN = $rootScope.searchList[i].ISBN;
  $rootScope.selected.desc = $rootScope.searchList[i].desc;
  $rootScope.selected.key = $rootScope.searchList[i].key;
  $rootScope.selected.image = $rootScope.searchList[i].image;
  $rootScope.selected.bigImage = $rootScope.searchList[i].bigImage;


};

$rootScope.searchMouseOver = function(theBook) {

  $rootScope.hoveredKey = theBook;
};

});

function ToastCtrlTwo($scope, $rootScope, $mdToast) {
};

