 /*
  responsible for: loading {{serviceType}}controller
  keeps all servicePanel specific infomation (state, position, size, etc)
  can save all this information to the WebGUI service

  creates the initial service panels

  registers with Runtime to dynamically add a service panel for any new service and
  remove a panel for any released service

  data: 
*/
angular.module('mrlapp.service')
.service('serviceSvc', ['$log', 'mrl', function($log, mrl) {
        $log.info('serviceSvc');
        
        var _self = this;
        var servicePanels = {};
        
        var isUndefinedOrNull = function(val) {
            return angular.isUndefined(val) || val === null;
        };


        // returns map/object of panels
        // similar to mrl.getRegistry()
        this.getPanels = function() {
            return servicePanels;
        }


        // returns list of panels
        // similar to mrl.getServices()
        this.getPanelList = function() {
            var arrayOfPanels = Object.keys(servicePanels).map(function(key) {
                return servicePanels[key]
            });
            return arrayOfPanels;
        }
        
        this.addServicePanel = function(name) {
            $log.info('serviceSvc.addServicePanel', name);
            var service = mrl.getService(name);
            
            if (!isUndefinedOrNull(servicePanels[name])) 
            {
                $log.error(name, " panel already created");
                return;
            }
            
            var service = mrl.getService(name);

            // creating new PANEL !!!
            var panel = {};
            //panel.show = true;
            panel.name = name;
            panel.type = service.simpleName.toLowerCase();
            // FIXME !!! REMOVE THIS all normalized values can
            // be copied over .. 
            // panel.service = service; // FIXME - probably should not do this
            panel.simpleName = service.simpleName;
            panel.type = service.simpleName.toLowerCase();
            panel.simpleName = service.simpleName;

            // adding it to our map of panels
            servicePanels[name] = panel;
        
        };
        
        this.hideAll = function() {
            for (var name in servicePanels) {
                if (servicePanels.hasOwnProperty(name)) {
                    var panel = servicePanels[name];
                    panel.hide(true);
                }
            }        
        }
        
        this.showAll = function() {
            for (var name in servicePanels) {
                if (servicePanels.hasOwnProperty(name)) {
                    var panel = servicePanels[name];
                    panel.hide(false);
                }
            }        
        }
        
        
        this.getServicePanel = function(name) {
            $log.info('serviceSvc.getServicePanel', name);
            if (isUndefinedOrNull(servicePanels[name])) {
                $log.error("could not get panel for ", name);
                return null;
            }
            return servicePanels[name];
        };
        
        this.removeServicePanel = function(name) {
            $log.info('serviceSvc.removeServicePanel', name);
            delete servicePanels[name];
        };
        
        this.onMsg = function(msg) {
            switch (msg.method) {
                case 'onRegistered':
                    var newService = msg.data[0];
                    _self.addServicePanel(newService.name);
                    break;
                
                case 'onReleased':
                    var service = msg.data[0];
                    _self.removeServicePanel(service.name);
                    break;
            }
        };
        
        mrl.subscribeToService(this.onMsg, mrl.getRuntime().name);
        
        var registry = mrl.getRegistry();
        for (var name in registry) {
            if (registry.hasOwnProperty(name)) {
                this.addServicePanel(name);
            }
        }
    }]);
