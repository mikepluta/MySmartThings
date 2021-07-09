/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {

    definition(name: "Alexa Dimmer Switch", namespace: "alexa", author: "mikepluta", runLocally: true, minHubCoreVersion: '000.021.00001', executeCommandsLocally: true, mnmn: "SmartThings") {
    	capability "Switch"
        capability "Switch Level"
        capability "Sensor"
        capability "Contact Sensor"
        capability "Actuator"
        capability "Refresh"
    }
    
    simulator {
		status "open": "contact:open"
		status "closed": "contact:closed"
	}

    // tiles {
    //     standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
    //         state "off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
    //         state "on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC"
    //     }
    //     main "switch"
    //     details(["switch"])
    // }
    
    // from dimmer....
    tiles(scale: 2) {
		multiAttributeTile(name: "switch", type: "switch", width: 6, height: 4, canChangeIcon: true, canChangeBackground: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
    			attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
		      	attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821", nextState: "turningOff"
				attributeState "turningOff", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
		      	attributeState "turningOn", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821", nextState: "turningOff"
        	}
        	tileAttribute("device.level", key: "SLIDER_CONTROL") {
            	attributeState "level", action:"switch level.setLevel"
        	}
      		tileAttribute("level", key: "SECONDARY_CONTROL") {
            	attributeState "level", label: 'Switch Level set to ${currentValue}'
        	}
		}
        
        valueTile("lValue", "device.level", inactiveLabel: true, height:2, width:2, decoration: "flat") {  
			state "levelValue", label:'${currentValue}', unit:"", backgroundColor: "#53a7c0"  
        }
        
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
		main "switch"
		details(["switch","lValue","refresh"])

	}
    
}

def parse(String description) {
}

def on() {
	log.info "switch:on, contact:open"
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "contact", value: "open")
}

def off() {
	log.info "switch:off, contact:closed"
    sendEvent(name: "switch", value: "off")
    sendEvent(name: "contact", value: "closed")
}

def setLevel(val) {
    log.info "setLevel $val"
    
    // make sure we don't drive switches past allowed values (command will hang device waiting for it to
    // execute and never come back)
    if (val < 0) {
    	val = 0
    }

    if (val > 100) {
    	val = 100
    }

    if (val == 0) { 
    	sendEvent(name: "level", value: val)
    	off()
    } else {
    	on()
    	sendEvent(name: "level", value: val)
    	sendEvent(name: "switch.setLevel", value: val)
    }
}

def refresh() {
    log.info "refresh"
}