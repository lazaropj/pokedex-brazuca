(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('pokemon', {
            parent: 'entity',
            url: '/pokemon',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gatewayApp.pokemon.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/pokemon/pokemons.html',
                    controller: 'PokemonController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('pokemon');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('pokemon-detail', {
            parent: 'entity',
            url: '/pokemon/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gatewayApp.pokemon.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/pokemon/pokemon-detail.html',
                    controller: 'PokemonDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('pokemon');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Pokemon', function($stateParams, Pokemon) {
                    return Pokemon.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'pokemon',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('pokemon-detail.edit', {
            parent: 'pokemon-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/pokemon/pokemon-dialog.html',
                    controller: 'PokemonDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Pokemon', function(Pokemon) {
                            return Pokemon.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('pokemon.new', {
            parent: 'pokemon',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/pokemon/pokemon-dialog.html',
                    controller: 'PokemonDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nome: null,
                                imagemURL: null,
                                tipo: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('pokemon', null, { reload: 'pokemon' });
                }, function() {
                    $state.go('pokemon');
                });
            }]
        })
        .state('pokemon.edit', {
            parent: 'pokemon',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/pokemon/pokemon-dialog.html',
                    controller: 'PokemonDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Pokemon', function(Pokemon) {
                            return Pokemon.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('pokemon', null, { reload: 'pokemon' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('pokemon.delete', {
            parent: 'pokemon',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/pokemon/pokemon-delete-dialog.html',
                    controller: 'PokemonDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Pokemon', function(Pokemon) {
                            return Pokemon.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('pokemon', null, { reload: 'pokemon' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
