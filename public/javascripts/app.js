/*jslint nomen: true, regexp: true */
/*global window, angular */

(function () {
    'use strict';

    var myApp = angular.module('progressApp', [
           'ui.bootstrap'
        ])
        .factory('progressSocketService', ['$rootScope', function ($rootScope) {
            console.log(" > Progress - Websocket factory...");
            // We return this object to anything injecting our service
            var Service = {};

            $rootScope.dynamicObject = {
                value: 0,
                text : null,
                type: 'success'
            };

            //using play framework we get the absolute URL
            var wsUrl = jsRoutes.controllers.Progress.process().absoluteURL();
            //replace the protocol to http ws
            wsUrl = wsUrl.replace("http", "ws");

            // Create our websocket object with the address to the websocket
            var ws = new WebSocket(wsUrl);

            ws.onopen = function () {
                console.log("Socket has been opened!");
            };

            ws.onmessage = function (message) {
                listener(JSON.parse(message.data));
            };

            function listener(data) {
                var messageObj = data;
                console.log("Received data from websocket: ", messageObj);
                //update the progress bar
                $rootScope.dynamicObject.value = messageObj.value;
                $rootScope.dynamicObject.text = messageObj.text;
                $rootScope.$apply()
            }

            return Service;
        }])

        .controller('ProgressController', [ '$rootScope', 'progressSocketService',
            function ($rootScope, progressSocketService) {
                console.log("service running");
            }
        ]);

}());
