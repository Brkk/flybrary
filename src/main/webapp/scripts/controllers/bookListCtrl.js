app.controller('BookListCtrl', function($scope, $rootScope, $http, $timeout, jsonFilter, user) {
	
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
		  
	$rootScope.deleteBook = function(key){
		user.activeBookProperties.key = key;
		user.deleteBook();	
	};
	
	$rootScope.unmatchBook = function(key, matchDate, isbn, actionType, title){
		user.activeBookProperties.key = key;
		user.activeBookProperties.matchDate = matchDate;
		user.activeBookProperties.isbn = isbn;
		user.actionType = actionType;
		user.activeBookProperties.title = title;
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
        if (item.actionType == 'offer') {
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
        if (item.actionType == 'request') {
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
