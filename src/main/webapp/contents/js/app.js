(function() {
  'use strict';
  angular.module('sales-app', ['ngRoute', 'zingchart-angularjs', 'ui.router.title']);

  angular.module('sales-app')
    .run(function($rootScope, $route, $routeParams, $location) {
      
      $rootScope.$on('$routeChangeStart',function(evt,next,current){
        console.log('Nome do Evento:'+evt.name);
        console.log('Próxima Rota:'+ angular.toJson(next));
        console.log('Rota Atual:'+ angular.toJson(current)); 
      });
      
      $rootScope.$route = $route;
      $rootScope.$location = $location;
      $rootScope.$routeParams = $routeParams;
    });
})();