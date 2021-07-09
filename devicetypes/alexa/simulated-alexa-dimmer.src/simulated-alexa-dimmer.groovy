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

//    definition(name: "Alexa Dimmer Switch", namespace: "alexa", author: "mikepluta", runLocally: true, minHubCoreVersion: '000.021.00001', executeCommandsLocally: true, mnmn: "SmartThings") {
	definition(name: "Simulated Alexa Dimmer", namespace: "alexa", author: "mikepluta") {
		capability "Actuator"
		capability "Contact Sensor"
		capability "Refresh"
		capability "Sensor"
		capability "Switch"
		capability "Switch Level"

		command "levelUp"
		command "levelDown"
	}

	simulator {
//		status "open": "contact:open", "switch:on"
//		status "closed": "contact:closed", "switch:off"
		status "open": "contact:open"
		status "closed": "contact:closed"
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "switchTile", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState: "turningOff"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
				attributeState "turningOn", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState: "turningOff"
				attributeState "turningOff", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
			}
			tileAttribute("device.level", key: "SECONDARY_CONTROL") {
				attributeState "level", label: 'Switch Level set to ${currentValue}'
			}
			tileAttribute("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel", defaultState: true
			}
		}
        
		multiAttributeTile(name: "valueTile", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState: "turningOff"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
				attributeState "turningOn", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState: "turningOff"
				attributeState "turningOff", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
			}
			tileAttribute("device.level", key: "VALUE_CONTROL") {
				attributeState "VALUE_UP", action: "levelUp"
				attributeState "VALUE_DOWN", action: "levelDown"
			}
		}

		valueTile("levelTile", "device.level", inactiveLabel: true, height:2, width:2, decoration: "flat") {  
			state "levelValue", label:'${currentValue}', unit:"", backgroundColor: "#53a7c0"  
		}

		standardTile("refreshTile", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		main "switchTile"
		details(["switchTile", "valueTile", "levelTile", "refreshTile"])

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

def setLevel(level) {
	log.info "setLevel $level"
    
    // make sure we don't drive switches past allowed values (command will hang device waiting for it to
    // execute and never come back)
	if (level < 0) {
		level = 0
	}

	if (level > 100) {
		level = 100
	}

	if (level == 0) {
		sendEvent(name: "level", value: level)
		off()
	} else {
		on()
		sendEvent(name: "level", value: level)
		sendEvent(name: "switch.setLevel", value: level)
	}
}

def refresh() {
	log.info "refresh"
}

def levelUp() {
	log.info "levelUp"
	def level = device.latestValue("level") as Integer ?: 0
	if (level < 100) {
		level = level + 1
	}
	setLevel(level)
}

def levelDown() {
	log.info "levelDwn"
	def level = device.latestValue("level") as Integer ?: 0
	if (level > 0) {
		level = level - 1
	}
	setLevel(level)
}