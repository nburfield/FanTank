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
          redirectTo: '/dashboard',
        });

      $locationProvider.html5Mode(true);
    }
  ]);

  app.controller('Dashboard', ['$scope', '$route', '$routeParams', '$location',
    function($scope, $route, $routeParams, $location) {
      this.$route = $route;
      this.$location = $location;
      this.$routeParams = $routeParams;

      $scope.tabs = [
          { link : 'dashboard', label : 'Account' },
          { link : 'dashboard/investments', label : 'Investments' },
        ]; 
        
      $scope.selectedTab = $scope.tabs[0];    
      $scope.setSelectedTab = function(tab) {
        $scope.selectedTab = tab;
      }
      
      $scope.tabClass = function(tab) {
        if ($scope.selectedTab == tab) {
          return "active";
        } else {
          return "";
        }
      }
    }
  ]);

  app.controller('AccountCtrl', ['$routeParams', '$scope', '$http', function($routeParams, $scope, $http) {
    this.name = 'AccountCtrl';
    this.params = $routeParams;
    $scope.readonly = true;
    $http.get('/user/data').
      then(function(response) {
        $scope.user = response.data;
      },
      function(data) {
        if(data.data.error == "UserNotFound") {
          $window.location.href = "/login?redirect=true";
        }
      });
  }])

  app.controller('InvestmentsCtrl', ['$routeParams', '$scope', '$http', '$window', function($routeParams, $scope, $http, $window) {
    this.name = 'InvestmentsCtrl';
    this.params = $routeParams;
    $scope.investments = [{offering_name:"-", amount: "-", unit_count: "-", status: "-"}];
    $http.get('/user/funding').
      then(function(response) {
        $scope.investments = response.data;
      },
      function(data) {
        if(data.data.error == "UserNotFound") {
          $window.location.href = "/login?redirect=true";
        }
      });
  }])

  app.controller('ExternalCtrl', ['$routeParams', function($routeParams) {
    console.log($routeParams)
    this.name = 'ExternalCtrl';
    this.params = $routeParams;
  }])

})(window.angular);
