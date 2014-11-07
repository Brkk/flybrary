app.controller('BookListCtrl', function($scope, $rootScope, $http, $timeout, jsonFilter, user) {
	

  console.log("booklist ctrl loaded");
  $rootScope.bookList = [];
  $scope.$watch('bookList',function(){},true);
  
  user.getBooks().then(
    function(data){
      $rootScope.bookList = data;
      console.log($rootScope.bookList);
    }, 
    function (err) {
      console.log('Failed: ' + err);
    });
  
	  user.getUser().then(function(data){
		  $scope.location = data;
	  }, 
	  function (err) {
		  console.log('Failed: ' + err);
	  });

  $scope.orderProp = 'title';
		  
	$rootScope.deleteBook = function(book){
    var index = $scope.bookList.indexOf(book);

    if (index > -1) {
        $scope.bookList.splice(index, 1);
    }

    user.activeBookProperties.key = book.key;
		user.deleteBook();	
	};
	
	$rootScope.unmatchBook = function(book){

   var index = $scope.bookList.indexOf(book);

    if (index > -1) {
        $scope.bookList.splice(index, 1);
    }

		user.activeBookProperties.key = book.key;
		user.activeBookProperties.matchDate = book.matchDate;
		user.activeBookProperties.isbn = book.isbn;
		user.actionType = book.actionType;
		user.activeBookProperties.title = book.title;
		user.unmatchTextbook();	
	};
	
  });

/*    Filters Start    */  
app.filter('isOffer', function () {
    return function (items) {
      var filtered = [];

      if(!items)
        return [];

      for (var i = 0; i < items.length; i++) {
        var item = items[i];
        if (item.actionType == 'offer' && item.matched == 'no') {
          filtered.push(item);
        }
      }
    return filtered;
  };
});
app.filter('isRequest', function () {
    return function (items) {
      var filtered = [];

      if(!items)
        return [];

      for (var i = 0; i < items.length; i++) {
        var item = items[i];
        if (item.actionType == 'request' && item.matched == 'no') {
          filtered.push(item);
        }
      }
      return filtered;
    };
  });
app.filter('isMatch', function () {
    return function (items) {
      var filtered = [];

      if(!items)
        return [];

      for (var i = 0; i < items.length; i++) {
        var item = items[i];
        if (item.matched == 'yes') {
          filtered.push(item);
        }
      }
      return filtered;
    };
  });
  
/*    Filters End    */
