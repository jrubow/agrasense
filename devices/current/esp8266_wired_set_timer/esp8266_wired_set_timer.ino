// ESP8266 Wired Beacon Sketch
// This sketch toggles a GPIO pin (D1/GPIO5) every 30 seconds
// to act as a wired signal for an attached ESP32.

const int SIGNAL_PIN = 2; // Changed from D2 to 2 (direct GPIO2)

unsigned long lastSignalTime = 0;
const long signalInterval = 30000; // Send signal every 30 seconds (in milliseconds)
bool signalState = LOW; // Current state of the signal pin

void setup() {
  Serial.begin(115200);
  Serial.println("\nESP8266 Wired Beacon Initializing...");

  pinMode(SIGNAL_PIN, OUTPUT);
  digitalWrite(SIGNAL_PIN, LOW); // Ensure the signal pin starts LOW

  Serial.print("ESP8266 Signal Pin: GPIO");
  Serial.print(SIGNAL_PIN);
  Serial.println("");
  Serial.println("Sending a pulse every 30 seconds...");
}

void loop() {
  // Check if it's time to send a signal
  if (millis() - lastSignalTime >= signalInterval) {
    // Send a short HIGH pulse
    digitalWrite(SIGNAL_PIN, HIGH);
    Serial.println("Signal HIGH (pulse started)");
    delay(100); // Keep HIGH for 100ms
    digitalWrite(SIGNAL_PIN, LOW);
    Serial.println("Signal LOW (pulse ended)");

    lastSignalTime = millis(); // Update last signal time
  }
}
