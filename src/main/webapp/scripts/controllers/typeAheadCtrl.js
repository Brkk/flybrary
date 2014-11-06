
app.controller('typeAheadCtrl', function($scope, $http, user) {
      // $scope.selected = '';
      
          $scope.updateList = function(title){
              return $http({
                method: 'GET',
                url: 'https://www.googleapis.com/books/v1/volumes?q='+title+'&maxResults=5&key=AIzaSyA1duUBotiNnlcHHqnH2oIWwM4JhyozhoQ'
              }).then(function ($response)
              {
                // var result = [];
                // $scope.booksList = $response.data['items'];
                // angular.forEach($scope.booksList, function(obj){
                      // result.push(obj['volumeInfo']['title']);
                // })
                return $response.data['items'];
              });
        }

          $scope.onSelect = function (item) {
              console.log($scope.asyncSelected);
              $scope.newBookProperties.author = $scope.asyncSelected['volumeInfo']['authors'][0];
              $scope.newBookProperties.isbn = $scope.asyncSelected['volumeInfo']['industryIdentifiers'][0]['identifier'];
              $scope.newBookProperties.title = $scope.asyncSelected['volumeInfo']['title'];
              $scope.newBookProperties.image = $scope.asyncSelected['volumeInfo']['imageLinks']['thumbnail'];

          };
});