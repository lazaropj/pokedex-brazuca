(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .factory('PokemonSearch', PokemonSearch);

    PokemonSearch.$inject = ['$resource'];

    function PokemonSearch($resource) {
        var resourceUrl =  'primeirageracaomicroservice/' + 'api/_search/pokemons/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
