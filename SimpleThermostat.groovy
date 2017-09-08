definition(
    name: "SimpleThermostat",
    namespace: "",
    author: "Erick Ortiz",
    description: "Control A/C using a temperature sensor."
)

preferences {
	section("Choose a temperature sensor... (outside)"){
		input "weather", "capability.temperatureMeasurement", title: "weather"
	}
	section("Choose a temperature sensor... (inside)"){
		input "sensor", "capability.temperatureMeasurement", title: "Sensor"
	}
	section("Select the air conditioner outlet(s)... "){
		input "outlets", "capability.switch", title: "Outlets", multiple: true
	}
	section("Set the desired temperature..."){
		input "setTemp", "decimal", title: "Set Temp"
	}
}

def initialize() {
	state.outTemp = 0.0
}

def installed()
{
	subscribe(weather, "temperature", weatherHandler)
    subscribe(sensor, "temperature", temperatureHandler)

}

def updated()
{
	unsubscribe()
	subscribe(sensor, "temperature", temperatureHandler)
}

def weatherHandler(evt)
{
	state.outTemp = evt.doubleValue
}

def temperatureHandler(evt)
{
	subscribe(weather, "temperature", weatherHandler)
	if (state.outTemp >= 90.0) {
    	log.debug "*"
    	evaluate(state.outTemp, state.outTemp - 20.0)
    }
	else if (setTemp) {
		evaluate(evt.doubleValue, setTemp)
	}
	else {
		outlets.off()
    }
}

private evaluate(currentTemp, desiredTemp)
{   
	log.debug "EVALUATE($currentTemp, $desiredTemp)"
    def threshold = 1.0

	if (currentTemp - desiredTemp >= threshold) {
		outlets.on()
	}
	else if (desiredTemp - currentTemp >= threshold) {
		outlets.off()
	}
}
