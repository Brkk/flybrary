app.service('user', function ($http, $rootScope, $scope, $q) {
    var uid = '',
        email ='',
        name = '',
        location = {
            lat: '',
            lon: '',
            radius: '',
            address: ''
        },
        actionType = '',
        activeBookProperties = {
            isbn: '',
            title: '',
            key: '',
            author: '',
            edition: '',
            condition: '',
            image: ''
        },
        deferred = $q.defer(),
        bookList = [];


        this.generateRetrieve = function (){
            return {
                uid: uid,
                name: name,
                address: location.address,
                lat: +(location.lat),
                lon: +(location.lon)
            };
        };

        this.generateAdd = function (){
            return {
                uid: uid,
                type: actionType,
                title: activeBookProperties.title,
                author: activeBookProperties.author,
                edition: activeBookProperties.edition,
                condition: activeBookProperties.condition,
                isbn: activeBookProperties.isbn
            };
        };

        this.generateDelete = function (){
            return {
                key: activeBookProperties.key
            };
        };

        this.generateUpdate = function (){
            return {
                key: activeBookProperties.key,
                title: activeBookProperties.title,
                author: activeBookProperties.author,
                edition: activeBookProperties.edition,
                condition: activeBookProperties.condition,
                isbn: activeBookProperties.isbn
            };
        };

        this.generateRadius = function (){
            return {
                uid: uid,
                radius: +(location.radius)
            };
        };

        this.generateLocation = function (){
            return {
                uid: uid,
                lon: +(location.lon),
                lat: +(location.lat)
            };
        };

        this.generateUnmatch = function (){
            return {
                key: activeBookProperties.key,
                uid: uid,
                title: activeBookProperties.title,
                author: activeBookProperties.author
            };
        };

        this.parseBook = function (book){
            return 
            {   
                key: book.key.id,
                title: book.propertyMap.title,
                actionType: book.propertyMap.type,
                author: book.propertyMap.author,
                condition: book.propertyMap.condition,
                edition: book.propertyMap.edition,
                image: book.propertyMap.image,
                isbn: book.propertyMap.isbn,
                matched: book.propertyMap.matched
            };
        };

        this.getBooks = function (){
            $http.post("resources/retrieve", generateRetrieve(), null)
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
            $http.post("resources/add", generateAdd(), null)
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
            $http.post("resources/delete", generateDelete(), null)
        };

        this.updateBook = function (){
            $http.post("resources/update", generateUpdate(), null)
        };


};






