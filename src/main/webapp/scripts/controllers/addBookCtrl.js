app.controller('addBookCtrl', function ($scope, $rootScope, $mdDialog, user, gBooks, $location) {
  
  $rootScope.search = {
    title: "",
    author: "",
    ISBN: ""
  };

  $rootScope.selected = {
    title: "sjdkfjw",
    author: "asdfaf",
    ISBN: "092292",
    desc: "sdsdhj'fsbcjhsdcsidcsdicswe",
    key: "",
    image: "http://books.google.com/books/content?id=g_ybia0hGw8C&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
    bigImage: "http://books.google.com/books/content?id=g_ybia0hGw8C&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
    condition: "1"
  };

  $rootScope.searchList = [];

  $rootScope.hoveredKey = "";

  $scope.hide = function() {
    $mdDialog.hide(false);
    $location.path('/main/' + $rootScope.currentTab).replace();
  };
  $rootScope.addBookClicked = function() {
    
    user.activeBookProperties.title = $rootScope.selected.title;
    user.activeBookProperties.author = $rootScope.selected.author;
    user.activeBookProperties.isbn = $rootScope.selected.ISBN;
    user.activeBookProperties.image = $rootScope.selected.image;
    user.activeBookProperties.condition = $rootScope.selected.condition;
    user.activeBookProperties.edition = '1';

    $mdDialog.hide(true);

    $location.path('/main/' + $rootScope.currentTab).replace();
  };


// fills the list/changes page/selects the first book
$rootScope.searchISBN = function() {

  gBooks.isbn = $scope.search.ISBN.replace(/\D/g,'');
  gBooks.doSearchISBN().then(
    function(data){
      console.log(data);
      $rootScope.searchList = data;
      $rootScope.selectKey(data[0]);
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
      console.log(data);
      $rootScope.searchList = data;
      $rootScope.selectKey(data[0]);
      $location.path('/main/' + $rootScope.currentTab + '/add/select').replace();
    }, 
    function (err) {
      console.log('Failed: ' + err);
    });

};

$rootScope.backToSearch = function() {
  $location.path('/main/' + $rootScope.currentTab + '/add/search').replace();
}


$rootScope.selectKey = function(theBook) {
  // lower the old selected element if it exists

  // raise on of the list elements, and fill the other side's elements

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
  // if the old one exists, lower it



  // raise it a little bit



  $rootScope.hoveredKey = theBook;
};



});
