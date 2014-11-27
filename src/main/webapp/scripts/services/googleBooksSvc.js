
var googleBooksSvc = angular.module( 'googleBooksSvc', [])

.service('gBooks', function ($http, $q) {

    var deferred_ISBN = "";
    var deferred_titleAuthor = "";

    this.title = "";
    this.author = "";
    this.isbn = "";


    function parseBook(book){
        var ret = {};
        
        ret.title = book.volumeInfo.title || "No title available";
        ret.desc = book.volumeInfo.description || "No description available";
        if(book.volumeInfo.imageLinks) {
            ret.image = book.volumeInfo.imageLinks.smallThumbnail;
            ret.bigImage = book.volumeInfo.imageLinks.thumbnail;
        }
        else {
            ret.image =  '/images/placeholder_small.gif';
            ret.bigImage = '/images/placeholder_big.gif';
        }
        if(book.volumeInfo.authors) {
            ret.author = book.volumeInfo.authors[0];
        }
        else {
            ret.author = "No author available";
        }

        if(book.volumeInfo.industryIdentifiers) {
            ret.ISBN = book.volumeInfo.industryIdentifiers[0].identifier;
        }
        else {
            ret.ISBN = '1';
        }

        ret.key = ret.ISBN;


        return ret;
    };

    // makes the http call
    this.doSearchISBN = function (){
      deferred_ISBN = $q.defer();

      $http({
        method: 'GET',
        url: 'https://www.googleapis.com/books/v1/volumes?q=isbn:'+this.isbn+'&maxResults=10&key=AIzaSyA1duUBotiNnlcHHqnH2oIWwM4JhyozhoQ'
      }).then(function ($response)
      {
        if($response.data.totalItems)
            deferred_ISBN.resolve(($response.data.items).map(parseBook));
        else
            deferred_ISBN.reject("no books found");
      });
      return deferred_ISBN.promise;

    };

    // makes the http call
    this.doSearchTitleAuthor = function (){
        deferred_titleAuthor = $q.defer();

        if(this.author) {

            $http({
                method: 'GET',
                url: 'https://www.googleapis.com/books/v1/volumes?q='+this.title+'+inauthor:'+this.author+'&maxResults=10&key=AIzaSyA1duUBotiNnlcHHqnH2oIWwM4JhyozhoQ'
            }).then(function ($response) {
                if($response.data.totalItems)
                    deferred_titleAuthor.resolve(($response.data.items).map(parseBook));
                else
                deferred_titleAuthor.reject("no books found");
        });
            
        }
        else {

            $http({
                method: 'GET',
                url: 'https://www.googleapis.com/books/v1/volumes?q='+this.title+'&maxResults=10&key=AIzaSyA1duUBotiNnlcHHqnH2oIWwM4JhyozhoQ'
              }).then(function ($response) {
                if($response.data.totalItems)
                    deferred_titleAuthor.resolve(($response.data.items).map(parseBook));
                else
                    deferred_titleAuthor.reject("no books found");
            });
        }
        return deferred_titleAuthor.promise;
    };

        

});