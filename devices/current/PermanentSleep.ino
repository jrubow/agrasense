void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  while (!Serial) {
    delay(10);  // Tiny wait
  }
  delay(1000);
  Serial.println("Going to sleep");
  delay(1000);
  Serial.flush();
  esp_deep_sleep_start();
  Serial.println("Should not be printed");
}

void loop() {
  // put your main code here, to run repeatedly:
  Serial.println("Should not be printed");
}
