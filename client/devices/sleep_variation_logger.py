import serial
import time
import csv
from datetime import datetime

# === CONFIG ===
SERIAL_PORT = 'COM3'
BAUD_RATE = 115200
CSV_FILENAME = 'log.csv'

# === Setup CSV file ===
def init_csv(filename):
    try:
        with open(filename, 'x', newline='') as f:
            writer = csv.writer(f)
            writer.writerow(['timestamp', 'state', 'epoch'])
    except FileExistsError:
        pass  # File already exists

# === Main Logger ===
def listen_and_log():
    init_csv(CSV_FILENAME)

    with serial.Serial(SERIAL_PORT, BAUD_RATE, timeout=5) as ser:
        print("Listening for 'SIG'...")

        while True:
            line = ser.readline().decode().strip()
            if not line:
                continue

            print(f"Received: {line}")

            if line == "SIG":
                timestamp = datetime.now() - timedelta(seconds=1)
                timestamp_str = timestamp.isoformat()

                # decode state:epoch
                state_epoch_line = ser.readline().decode().strip()
                print(f"State/Epoch: {state_epoch_line}")

                try:
                    state, epoch = map(int, state_epoch_line.split(":"))
                except ValueError:
                    print("Failed to parse state/epoch.")
                    continue

                # Append to CSV
                with open(CSV_FILENAME, 'a', newline='') as f:
                    writer = csv.writer(f)
                    writer.writerow([timestamp_str, state, epoch])

                print(f"Logged: {timestamp_str}, state={state}, epoch={epoch}")

if __name__ == "__main__":
    from datetime import timedelta
    listen_and_log()
