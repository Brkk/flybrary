
var userSvc = angular.module( 'userSvc', [])

.service('user', function ($http, $q) {
    this.uid = '';
    this.email ='';
    this.name = '';
    this.location = {
            lat: '',
            lon: '',
            radius: '',
            address: ''
        };
    this.actionType = '';
    this.activeBookProperties = {
            isbn: '',
            title: '',
            key: '',
            author: '',
            edition: '',
            condition: '',
            image: ''
        };
    this.bookList = [];

    var deferred = $q.defer();

    this.generateRetrieve = function (){
        return {
            uid: this.uid,
            name: this.name,
            email: this.email,
            location: this.location.address,
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
            key: this.activeBookProperties.key
        };
    };

    this.generateUpdate = function(){
        return {
            key: this.activeBookProperties.key,
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
            key: this.activeBookProperties.key,
            uid: this.uid,
            title: this.activeBookProperties.title,
            author: this.activeBookProperties.author
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
            image: book.propertyMap.image,
            isbn: book.propertyMap.isbn,
            matched: book.propertyMap.matched
        };
    }

    this.getBooks = function (){
        $http.post("resources/retrieve", this.generateRetrieve(), null)
        .success(function (data, status, headers, config)
        {
          bookList = data.map(parseBook);
          deferred.resolve(bookList);
        })
        .error(function (data, status, headers, config)
        {
            deferred.reject('error');
        });

        return deferred.promise;
    };

    this.addBook = function (){
        $http.post("resources/add", this.generateAdd(), null)
        .success(function (data, status, headers, config)
        {
            bookList.push(parseBook(data));
            deferred.resolve(bookList);
        })
        .error(function (data, status, headers, config)
        {
            deferred.reject('error');
        });
        return deferred.promise;
    };

    this.deleteBook = function (){
        $http.post("resources/delete", this.generateDelete(), null)
    };

    this.updateBook = function (){
        $http.post("resources/update", this.generateUpdate(), null)
    };

    this.getUser = function (){
        $http.post("resources/getUser", this.generateRetrieve(), null)
        .success(function (data, status, headers, config)
        {
            location.lat = data.lat,
            location.lon = data.lon,
            location.radius = data.radius,
            location.address = data.address
            deferred.resolve(location);
        })
        .error(function (data, status, headers, config)
        {
            deferred.reject('error');
        });

        return deferred.promise;
    };


});