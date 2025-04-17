#include <Arduino.h>
#include <Preferences.h>

#define NOTHING 0
#define NUM_EPOCHS 100
#define NUM_STATES 15
#define BASE_SLEEP_DURATION_US (60ULL * 1000000ULL)
#define RESET 1


Preferences preferences;

void setup() {
  if (!RESET) {
    Serial.begin(9600);
    // delay(1000);

    Serial.println("SIG");
    Serial.flush();
    delay(1000);

    preferences.begin("configuration", false);
    uint64_t state = preferences.getUInt("state", NOTHING);
    uint64_t epoch = preferences.getUInt("epoch", NOTHING);

    Serial.printf("%llu:%llu\n", state, epoch);

    if (state == NOTHING) {
      delay(30000);
      preferences.putUInt("state", 1);
      preferences.putUInt("epoch", 1);
      state = 1;
      epoch = 1;
    } else if (epoch >= NUM_EPOCHS) {
      if (state != 1) {
        preferences.putUInt("state", state + 15);
        state += 15;
      } else {
        preferences.putUInt("state", 15);
        state = 15;
      }
      preferences.putUInt("epoch", 1);
      
      epoch = 1;
    } else {
      preferences.putUInt("epoch", epoch + 1);
      epoch += 1;
    }

    if (state <= NUM_STATES) {
      uint64_t sleepDuration = BASE_SLEEP_DURATION_US * state;
      Serial.flush(); 
      esp_sleep_enable_timer_wakeup(sleepDuration);
      esp_deep_sleep_start();
    } else {
      Serial.println("Complete");
    }
  } else {
    preferences.begin("configuration", false);
    preferences.remove("state");
    preferences.remove("epoch");
    preferences.end();
  }
}

void loop() {
  // ignored
}
