(function(angular) {
  'use strict';

  var app = angular.module('dashboard', ['ngRoute', 'ngAnimate']);

  app.config(['$routeProvider', '$locationProvider',
    function($routeProvider, $locationProvider) {
      $routeProvider
        .when('/dashboard', {
          templateUrl: 'account',
          controller: 'AccountCtrl',
        })
        .when('/dashboard/investments', {
          templateUrl: 'investments',
          controller: 'InvestmentsCtrl',
        })
        .otherwise({
          controller: 'ExternalCtrl',
        });

      $locationProvider.html5Mode(true);
    }
  ]);

  app.controller('Dashboard', ['$route', '$routeParams', '$location',
    function($route, $routeParams, $location) {
      this.$route = $route;
      this.$location = $location;
      this.$routeParams = $routeParams;
    }
  ]);

  app.controller('AccountCtrl', ['$routeParams', '$scope', '$http', function($routeParams, $scope, $http) {
    this.name = 'AccountCtrl';
    this.params = $routeParams;
    $http.get('/user/data').
    then(function(response) {
      $scope.user = response.data;
    });
  }])

  app.controller('InvestmentsCtrl', ['$routeParams', '$scope', '$http', function($routeParams, $scope, $http) {
    this.name = 'InvestmentsCtrl';
    this.params = $routeParams;
    $http.get('/user/funding').
      then(function(response) {
        $scope.investments = response.data;
      });
  }])

  app.controller('ExternalCtrl', ['$routeParams', function($routeParams) {
    console.log($routeParams)
    this.name = 'ExternalCtrl';
    this.params = $routeParams;
  }])

})(window.angular);
