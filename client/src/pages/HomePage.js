import React, { useEffect, useState } from "react";
import axios from "axios";
import { Line } from "react-chartjs-2";
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from "chart.js";
import DeviceInfo from "../components/DeviceInfo"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

const options = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      min: 0, // Set a fixed minimum value
      max: 100, // Set a maximum value (adjust based on your dataset)
      ticks: {
        stepSize: 10, // Adjust step size for better readability
      },
    },
  },
};

const options2 = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      min: 0, // Set a fixed minimum value
      max: 50, // Set a maximum value (adjust based on your dataset)
      ticks: {
        stepSize: 10, // Adjust step size for better readability
      },
    },
  },
};

function HomePage() {
  const [temperatureData, setTemperatureData] = useState([]);
  const [humidityData, setHumidityData] = useState([]);
  const [lightData, setLightData] = useState([]);
  const [soilData, setSoilData] = useState([]);
  const [timestamps, setTimestamps] = useState([]);

  // Fetch the data from the API
  const fetchData = async () => {
    try {
      const response = await axios.get(
        "/record/averages"
      );

      const data = response.data; // Assuming the data returned is an array of objects
      console.log(response.data)

      const tempData = data.map(item => item.avgTemp);
      const humidityData = data.map(item => item.avgHumidity);
      const lightData = data.map(item => item.avgLight);
      const soilData = data.map(item => item.avgSoil);
      const timestamps = data.map(item => item.timestamp);

      setTemperatureData(tempData);
      setHumidityData(humidityData);
      setLightData(lightData);
      setSoilData(soilData);
      setTimestamps(timestamps);
    } catch (error) {
      console.error("Error fetching data: ", error);
    }
  };

  // Function to generate chart data structure
  const chartData = (label, data) => ({
    labels: timestamps,
    datasets: [
      {
        label,
        data,
        borderColor: "#007bff",
        backgroundColor: "#66b3ff",
      },
    ],
  });

  // Set up periodic data fetching every 10 seconds
  useEffect(() => {
    fetchData(); // Initial fetch
    const interval = setInterval(fetchData, 10000);

    return () => clearInterval(interval);
  }, []); 

  const devices = [{"deviceId":99, "status":"Online", "lastOnline":"test_timestamp", "isSentinel":true}, 
    {"deviceId":100, "status":"Online", "lastOnline":"test_timestamp", "isSentinel":false},
    {"deviceId":101, "status":"Offline", "lastOnline":"test_timestamp", "isSentinel":false},
    {"deviceId":102, "status":"Offline", "lastOnline":"test_timestamp", "isSentinel":false}];

  const [selected, setSelected] = useState(100)

  return (
    <div>
      <div className="devices">
        {devices.map(device => (
          <DeviceInfo device={device} selected={selected} setSelected={setSelected}/>
        ))}
      </div>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "20px", padding: "20px", height: "100vh" }}>
        <div style={{ height: "300px" }}>
          <h3>Average Temperature</h3>
          <Line data={chartData("Temperature", temperatureData)} options={options2} />
        </div>
        <div style={{ height: "300px" }}>
          <h3>Average Humidity</h3>
          <Line data={chartData("Humidity", humidityData)} options={options2} />
        </div>
        <div style={{ height: "300px" }}>
          <h3>Average Light</h3>
          <Line data={chartData("Light", lightData)} options={options} />
        </div>
        <div style={{ height: "300px" }}>
          <h3>Average Soil Moisture</h3>
          <Line data={chartData("Soil", soilData)} options={options} />
        </div>
      </div>
    </div>
  );
}

export default HomePage;
