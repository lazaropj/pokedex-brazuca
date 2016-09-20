(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('PokemonDetailController', PokemonDetailController);

    PokemonDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Pokemon'];

    function PokemonDetailController($scope, $rootScope, $stateParams, previousState, entity, Pokemon) {
        var vm = this;

        vm.pokemon = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gatewayApp:pokemonUpdate', function(event, result) {
            vm.pokemon = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
