#include "painlessMesh.h" // Include the PainlessMesh library for mesh networking
#include <ArduinoJson.h>   // Include ArduinoJson for parsing JSON messages

// Define mesh network parameters
// MESH_PREFIX: Unique identifier for your mesh network. All devices in the same network must use this.
#define MESH_PREFIX "asterlink"
// MESH_PASSWORD: Password for joining the mesh network.
#define MESH_PASSWORD "AsterLinkMesh2025$#"
// MESH_PORT: Port used for mesh communication.
#define MESH_PORT 5555

// Define the GPIO pin on the ESP8266 connected to the ESP32's interrupt pin.
// Changed from D1 to GPIO2, which often corresponds to IO2 on some ESP8266 boards.
#define ESP32_WAKE_PIN GPIO2
// Duration of the HIGH pulse to send to the ESP32 to trigger its wake-up interrupt, in milliseconds.
#define WAKE_PULSE_DURATION 50

// Create a painlessMesh object. This object manages the mesh network operations.
painlessMesh myMesh;

// --- Function Prototypes ---
// Callback function executed when a message is received from another mesh node.
void receivedCallback(uint32_t from, String &msg);
// Callback function executed when a new node connects to this node in the mesh.
void newConnectionCallback(uint32_t nodeId);
// Callback function executed when connection status changes (e.g., a node disconnects).
void changedConnectionCallback();

// --- Arduino Setup Function ---
// This function runs once when the ESP8266 starts up.
void setup() {
  // Initialize serial communication for debugging output.
  Serial.begin(115200);
  Serial.println(); // Print a newline for cleaner output in the serial monitor.

  // Configure the ESP32_WAKE_PIN as an output.
  pinMode(ESP32_WAKE_PIN, OUTPUT);
  // Ensure the wake-up pin is initially LOW to prevent accidental triggers.
  digitalWrite(ESP32_WAKE_PIN, LOW);

  // Set debug message types for painlessMesh. This helps in debugging mesh issues.
  // You can customize these flags to see more or less debug information.
  myMesh.setDebugMsgTypes( ERROR | STARTUP | CONNECTION | MSG_TYPES | SYNC | COMMUNICATION | GENERAL | PORTAL );

  // Initialize the mesh network with the defined parameters and the receivedCallback function.
  myMesh.init(MESH_PREFIX, MESH_PASSWORD, &receivedCallback, MESH_PORT);
  // Register callback functions for new connections and changed connections.
  myMesh.onNewConnection(&newConnectionCallback);
  myMesh.onChangedConnections(&changedConnectionCallback);

  Serial.println("ESP8266 Mesh Receiver Initialized.");
  Serial.print("Mesh ID: ");
  Serial.println(myMesh.getNodeId()); // Print the unique ID of this mesh node.
}

// --- Arduino Loop Function ---
// This function runs repeatedly after setup().
void loop() {
  // Call myMesh.update() frequently. This is crucial for the mesh library to perform
  // its background tasks, such as sending/receiving messages, maintaining connections, etc.
  myMesh.update();
}

// --- Callback Function for Received Messages ---
void receivedCallback(uint32_t from, String &msg) {
  // Print the received message and its sender's node ID to the serial monitor.
  Serial.printf("Received from %u msg: %s\n", from, msg.c_str());

  // Create a StaticJsonDocument to parse the incoming JSON message.
  // The size (200 bytes here) should be sufficient for simple messages.
  // Adjust if your JSON messages are larger.
  StaticJsonDocument<200> doc;
  // Deserialize the JSON string into the JsonDocument.
  DeserializationError error = deserializeJson(doc, msg);

  // Check if there was an error during JSON deserialization.
  if (error) {
    Serial.print(F("deserializeJson() failed: "));
    Serial.println(error.f_str());
    return; // Exit the function if parsing failed.
  }

  // Check if the JSON document contains the "instructionType" field.
  if (doc.containsKey("instructionType")) {
    // Extract the value of "instructionType" as a String.
    String instructionType = doc["instructionType"].as<String>();
    Serial.printf("Instruction Type: %s\n", instructionType.c_str());

    // If the instructionType is "WAKE", trigger the ESP32 wake-up sequence.
    if (instructionType == "WAKE") {
      Serial.println("WAKE signal received! Waking up ESP32...");
      // Set the wake-up pin HIGH to send a pulse.
      digitalWrite(ESP32_WAKE_PIN, HIGH);
      // Keep the pin HIGH for the specified duration.
      delay(WAKE_PULSE_DURATION);
      // Set the wake-up pin back to LOW.
      digitalWrite(ESP32_WAKE_PIN, LOW);
      Serial.println("ESP32 wake pulse sent.");
    }
  }
}

// --- Callback Function for New Connections ---
void newConnectionCallback(uint32_t nodeId) {
  Serial.printf("--> New connection, nodeId = %u\n", nodeId);
}

// --- Callback Function for Changed Connections ---
void changedConnectionCallback() {
  Serial.printf("Changed connections\n");
}
