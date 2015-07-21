angular.module('mrlapp.service')
// FIXME FIXME FIXME
// THIS IS NOT SERVICE SPECIFIC !! MEANING IT NEEDS TO BE COMBINED WITH mainCtrl !!!
// DO NOT ADD NEW DATA TO service
// panel should probably be retrieved from serviceSvc
.controller('ServiceCtrl', ['$scope', '$modal', '$ocLazyLoad', 'mrl', 'serviceSvc', '$log', 
    function($scope, $modal, $ocLazyLoad, mrl, serviceSvc, $log) {
        // TEMPORARY $scope variables !!!!
        // this runs through for each service - 
        // all scope data is temporary unless its put into an associative  array
        // the name switches every time.
        // $scope.anker = name + '_-_' + $scope.service.panelname + '_-_';
        var name = $scope.service.name;
        var panel = serviceSvc.getServicePanel(name);
        
        $scope.anker = name;
        $scope.panel = panel;
        
        $log.info('ServiceCtrl', name);

        var type = $scope.panel.type;

        $log.info('lazy-loading:', type);
        $ocLazyLoad.load("service/js/" + type + "gui.js").then(function() {
            $scope.serviceloaded = true; // FIXME why is this needed?
        }, function(e) {
            $log.info('lazy-loading wasnt successful:', type);
            $scope.servicenotfound = true;
        });

   
        var initDone = false;
        panel.initDone = function() {
            if (!initDone) {
                initDone = true;
                // create message bindings
                mrl.subscribeToService(panel.onMsg, name);
            }
        };

        panel.setPanelNames = function(names) {
            $log.info('setting panelnames', names);
        };
        panel.setPanelShowNames = function(show) {
            $log.info('setting panelshownames', show);
        };
        panel.setPanelSizes = function(sizes) {
            $log.info('setting panelsizes');
            serviceSvc.notifyPanelSizesChanged(name, sizes);
        };

        //TODO: not completly happy
        //to be overriden
        if (panel.onMsg == null) {
            panel.onMsg = function() {
                console.log('ERR got message to default service endpoint!');
            };
        }
        //END_specific Service-Initialisation

        $scope.hide = function(val) {
             $scope.show = !val;  
        }

        //footer-size-change-buttons
        $scope.changesize = function(size) {
            console.log("change size", name, size);
            $scope.service.panelsize.oldsize = $scope.service.panelsize.aktsize;
            $scope.service.panelsize.aktsize = size;
            $scope.notifySizeChanged();
            if (size == "full") {
                //launch the service as a modal ('full')
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'service/servicefulltemplate.html',
                    controller: 'ServiceFullCtrl',
                    size: 'lg',
                    resolve: {                        
                        panel: function() {
                            return panel;
                        },
                        service: function() {
                            return $scope.service;
                        }
                    }
                });
            }
        };


        // assign a scope method to the panel
        serviceSvc.getServicePanel(name).hide = $scope.hide;

    }]);
