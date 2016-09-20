(function() {
    'use strict';
    angular
        .module('gatewayApp')
        .factory('Pokemon', Pokemon);

    Pokemon.$inject = ['$resource'];

    function Pokemon ($resource) {
        var resourceUrl =  'primeirageracaomicroservice/' + 'api/pokemons/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
