(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('PokemonDialogController', PokemonDialogController);

    PokemonDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Pokemon'];

    function PokemonDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Pokemon) {
        var vm = this;

        vm.pokemon = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.pokemon.id !== null) {
                Pokemon.update(vm.pokemon, onSaveSuccess, onSaveError);
            } else {
                Pokemon.save(vm.pokemon, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gatewayApp:pokemonUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
