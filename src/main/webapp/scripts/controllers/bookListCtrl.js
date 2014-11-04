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

  $scope.orderProp = 'title';
  });


/*    Filters Start    */  
app.filter('isOffer', function () {
    return function (items) {
      var filtered = [];

      if(!items)
        return [];

      for (var i = 0; i < items.length; i++) {
        var item = items[i];
        if (item.activeType == 'offer') {
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
        if (item.activeType == 'request') {
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
