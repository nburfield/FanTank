/*!
 * BC Innovations, LLC - v1.0.0
 * Copyright 2017-2017 BC Innovations, LLC
 * Licensed under ISC
 */
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

  app.controller('Navbar', function($scope, $http) {
    $http.get('/user/data').
      then(function(response) {
        $scope.user = response.data;
      });
  });

  app.controller('Dashboard', ['$route', '$routeParams', '$location',
    function($route, $routeParams, $location) {
      this.$route = $route;
      this.$location = $location;
      this.$routeParams = $routeParams;
    }
  ]);

  app.controller('AccountCtrl', ['$routeParams', function($routeParams) {
    this.name = 'AccountCtrl';
    this.params = $routeParams;
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
