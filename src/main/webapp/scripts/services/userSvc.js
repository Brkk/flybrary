
var userSvc = angular.module( 'userSvc', [])

.service('user', function ($http, $q) {
    this.uid = '';
    this.email ='';
    this.name = '';
    this.loc = {
            lat: 0,
            lon: 0,
            zoom: 12,
            radius: 15000,
            slocet: false
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
            lat: +(this.loc.lat),
            lon: +(this.loc.lon)
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
            lat : +(this.loc.lat),
            lon : +(this.loc.lon),
            radius : +(this.loc.radius),
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
            radius: +(this.loc.radius)
        };
    };

    this.generateUser = function(){
        return {
            uid: this.uid
        };
    };

    this.generateloc = function(){
        return {
            uid: this.uid,
            lon: +(this.loc.lon),
            lat: +(this.loc.lat)
        };
    };

    this.generateUnmatch = function(){
        return {
            id: this.activeBookProperties.key,
            uid: this.uid,
            type: this.actionType,
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
            matchDate: book.propertyMap.matchDate.value
        };
    }

    this.getBooks = function (){
        $http.post("resources/retrieve", this.generateRetrieve(), null)
        .success(function (data, status, headers, config)
        {
            deferred_getBooks.resolve(data.map(parseBook));
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
            deferred_addBook.resolve(parseBook(data));
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
    
    this.updateUserloc = function (){
        $http.post("resources/updateUserloc", this.generateloc(), null)
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
            if(data.propertyMap){
                var loc = {
                    lat: data.propertyMap.lat.value,
                    lon: data.propertyMap.lon.value,
                    radius: data.propertyMap.radius.value
                };
                
                deferred_getUser.resolve(loc);
            }
            else {
            	deferred_getUser.reject('No User');
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