var app = angular.module('refresh_div', []);

app.config(['$httpProvider', function ($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
        $httpProvider.defaults.headers.common["Accept"] = "application/json";
        $httpProvider.defaults.headers.common["Content-Type"] = "application/json";
    }
]);

app.controller('refresh_control', function ($scope, $interval, $http, $filter) {
    $interval(function () {
        var response =  $http({
            method: 'GET',
            url: 'http://localhost:8080/sales-weather/webservice/sales-order/1', 
            headers: {'Access-Control-Allow-Origin': '*',                                            
                      'Access-Control-Allow-Headers': 'Origin, X-Requested-With, Content-Type, Accept, Client-Offset, Authorization',
                      'Access-Control-Allow-Credentials':'true',
                      'Authorization': "55d5927329415b000100003b63a9e1b480b64a1040a902a26da862d1"}

        });
        response.success(function (data) {
            $scope.sales = data;
            console.log("[main] # of items: " + data.length);
            angular.forEach(data, function (element) {
                console.log("[main] sale: " + element.prodDescription);
            });

        });
        response.error(function (status) {
            console.log("AJAX failed to get data, status=" + status);
        });
    }, 10000);

});


