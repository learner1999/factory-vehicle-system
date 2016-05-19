var factoryVehicle = angular.module('factoryVehicle',['ui.router']);

factoryVehicle.config(function($stateProvider,$urlRouterProvider) {
	$urlRouterProvider.otherwise('/login');
	$stateProvider
		.state('login',{
			url: '/login',
			templateUrl: 'tpls/loginForm.html'
		})
		.state('admin',{
			url: '/admin',
			views: {
				'': {
					templateUrl: 'tpls/admin.html'
				},
				'sideBar@admin': {
					templateUrl: 'tpls/sidebar.html'
				},
				'topBar@admin': {
					templateUrl: 'tpls/topbar.html'
				}
			}
		})
		.state('affairs',{
			url: '/affairs',
			views: {
				'': {
					templateUrl: 'tpls/affairs.html'
				},
				'sideBar@affairs': {
					templateUrl: 'tpls/sidebarAffairs.html'
				},
				'topBar@affairs': {
					templateUrl: 'tpls/topbar.html'
				}
			}
		})
});