
var userSvc = angular.module( 'userSvc', [])

.service('user', function ($http, $q) {
    this.uid = '';
    this.email ='';
    this.name = '';
    this.location = {
            lat: 0,
            lon: 0,
            radius: 15000
        };
    this.actionType = '';
    this.activeBookProperties = {
            isbn: '',
            title: '',
            key: '',
            author: '',
            edition: '',
            condition: '',
            image: '',
            matchDate: '',
            matched: ''
        };
    this.bookList = [];

    var deferred_getBooks = $q.defer();
    var deferred_getUser = $q.defer();
    var deferred_addBook = $q.defer();

    this.generateRetrieve = function (){
        return {
            uid: this.uid,
            name: this.name,
            email: this.email,
            lat: +(this.location.lat),
            lon: +(this.location.lon)
        };
    };

    this.generateAdd = function(){
        return {
            uid: this.uid,
            type: this.actionType,
            title: this.activeBookProperties.title,
            author: this.activeBookProperties.author,
            edition: this.activeBookProperties.edition,
            condition: this.activeBookProperties.condition,
            isbn: this.activeBookProperties.isbn,
            lat : +(this.location.lat),
            lon : +(this.location.lon),
            radius : +(this.location.radius),
            image: this.activeBookProperties.image
        };
    };

    this.generateDelete = function(){
        return {
            id: this.activeBookProperties.key
        };
    };

    this.generateUpdate = function(){
        return {
            id: this.activeBookProperties.key,
            title: this.activeBookProperties.title,
            author: this.activeBookProperties.author,
            edition: this.activeBookProperties.edition,
            condition: this.activeBookProperties.condition,
            isbn: this.activeBookProperties.isbn
        };
    };

    this.generateRadius = function(){
        return {
            uid: this.uid,
            radius: +(this.location.radius)
        };
    };

    this.generateUser = function(){
        return {
            uid: this.uid
        };
    };

    this.generateLocation = function(){
        return {
            uid: this.uid,
            lon: +(this.location.lon),
            lat: +(this.location.lat)
        };
    };

    this.generateUnmatch = function(){
        return {
            id: this.activeBookProperties.key,
            type: this.activeBookProperties.actionType,
            title: this.activeBookProperties.title,
            author: this.activeBookProperties.author,
            matchDate: this.activeBookProperties.matchDate,
            isbn: this.activeBookProperties.isbn
        };
    };

    function parseBook(book){
        console.log(book);
        return {   
            key: book.key.id,
            title: book.propertyMap.title.value,
            actionType: book.propertyMap.type,
            author: book.propertyMap.author.value,
            condition: book.propertyMap.condition,
            edition: book.propertyMap.edition,
            image: book.propertyMap.image.value,
            isbn: book.propertyMap.isbn,
            matched: book.propertyMap.matched,
            matchDate: book.propertyMap.matchDate
        };
    }

    this.getBooks = function (){
        $http.post("resources/retrieve", this.generateRetrieve(), null)
        .success(function (data, status, headers, config)
        {
          bookList = data.map(parseBook);
          deferred_getBooks.resolve(bookList);
        })
        .error(function (data, status, headers, config)
        {
            deferred_getBooks.reject('error');
        });
        deferred_getBooks = $q.defer();
        return deferred_getBooks.promise;
    };

    this.addBook = function (){
        $http.post("resources/add", this.generateAdd(), null)
        .success(function (data, status, headers, config)
        {
            bookList.push(parseBook(data));
            deferred_addBook.resolve(bookList);
        })
        .error(function (data, status, headers, config)
        {
            deferred_addBook.reject('error');
        });
        deferred_addBook = $q.defer();
        return deferred_addBook.promise;
    };

    this.deleteBook = function (){
        $http.post("resources/delete", this.generateDelete(), null)
    };

    this.updateBook = function (){
        $http.post("resources/update", this.generateUpdate(), null)
    };
    
    this.updateUserLocation = function (){
        $http.post("resources/updateUserLocation", this.generateLocation(), null)
    };
    
    this.updateUserRadius = function (){
        $http.post("resources/updateUserRadius", this.generateRadius(), null)
    };
    
    this.unmatchTextbook = function (){
        $http.post("resources/unmatchTextbook", this.generateUnmatch(), null)
    };

    this.getUser = function (){
        $http.post("resources/getUser", this.generateRetrieve(), null)
        .success(function (data, status, headers, config)
        {
            if(data=={}){
            	deferred.reject('No User');
            }
            else {
            	location.lat = data.lat,
                location.lon = data.lon,
                location.radius = data.radius,
                deferred_getUser.resolve(location);
            }
        })
        .error(function (data, status, headers, config)
        {
            deferred_getUser.reject('error');
        });
        deferred_getUser = $q.defer();
        return deferred_getUser.promise;
    };


});