module.config(['$provide', function ($provide) {

    $provide.decorator('$controller', ['$delegate', 'Auth', 'Current', function ($delegate, Auth, Current) {

        function getAccess(role) {

            if (!Current.realm) {
                return false;
            }

            var realmAccess = Auth.user && Auth.user['realm_access'];
            if (realmAccess) {
                realmAccess = realmAccess[Current.realm.realm];
                if (realmAccess) {
                    return realmAccess.indexOf(role) >= 0;
                }
            }
            return false;
        }

        return function (constructor, locals) {

            var controller = $delegate.apply(null, arguments);

            return angular.extend(function() {

                var obj = controller();

                if (constructor === 'GlobalCtrl') {

                    Object.defineProperty(locals.$scope.access, 'viewHello', {
                        get: function() {
                            return getAccess('view-hello');
                        }
                    });

                    Object.defineProperty(locals.$scope.access, 'manageHello', {
                        get: function() {
                            return getAccess('manage-hello');
                        }
                    });
                }

                return obj;

            }, controller);

        };

    }]);

}]);

module.config([ '$routeProvider', function($routeProvider) {
    $routeProvider
        .when('/realms/:realm/hello', {
            templateUrl : resourceUrl + '/partials/hello.html',
            resolve : {
                realm : function(RealmLoader) {
                    return RealmLoader();
                },
                hello : function(HelloLoader) {
                    return HelloLoader();
                }
            },
            controller : 'HelloCtrl'
        });
} ]);

module.controller('HelloCtrl', function($scope, realm, hello, $location) {

    $scope.realm = realm;
    $scope.hello = hello;

});

module.factory('Hello', function($resource) {
    return $resource(authUrl + '/realms/:realm/hello', {
        realm : '@realm'
    },  {
        update : {
            method : 'PUT'
        }
    });
});

module.factory('HelloLoader', function(Loader, Hello, $route, $q) {
    return Loader.get(Hello, function() {
        return {
            realm : $route.current.params.realm
        };
    });
});
