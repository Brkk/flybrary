
var userSvc = angular.module( 'userSvc', [])

.service('user', function ($http, $q) {
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


        function generateRetrieve(){
            return {
                uid: uid,
                name: name,
                address: location.address,
                lat: +(location.lat),
                lon: +(location.lon)
            };
        }

        function generateAdd(){
            return {
                uid: uid,
                type: actionType,
                title: activeBookProperties.title,
                author: activeBookProperties.author,
                edition: activeBookProperties.edition,
                condition: activeBookProperties.condition,
                isbn: activeBookProperties.isbn,
                lat : +(location.lat),
                lon : +(location.lon),
                radius : +(location.radius)
            };
        }

        function generateDelete(){
            return {
                key: activeBookProperties.key
            };
        }

        function generateUpdate(){
            return {
                key: activeBookProperties.key,
                title: activeBookProperties.title,
                author: activeBookProperties.author,
                edition: activeBookProperties.edition,
                condition: activeBookProperties.condition,
                isbn: activeBookProperties.isbn
            };
        }

        function generateRadius(){
            return {
                uid: uid,
                radius: +(location.radius)
            };
        }

        function generateUser(){
            return {
                uid: uid
            };
        }

        function generateLocation(){
            return {
                uid: uid,
                lon: +(location.lon),
                lat: +(location.lat)
            };
        }

        function generateUnmatch(){
            return {
                key: activeBookProperties.key,
                uid: uid,
                title: activeBookProperties.title,
                author: activeBookProperties.author
            };
        }

        function parseBook(book){
            return {   
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
        }

        this.getBooks = function (){
            $http.post("resources/retrieve", generateRetrieve(), null)
            .success(function (data, status, headers, config)
            {
              bookList = data.map(parseBook(book));
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

        this.getUser = function (){
            $http.post("resources/getUser", generateRetrieve(), null)
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