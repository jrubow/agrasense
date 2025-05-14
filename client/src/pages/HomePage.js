import React, { useState, useEffect } from 'react';
import '../css/pages/homepage.css';
import { useNavigate } from "react-router-dom"
import axios from "axios"
import Plot from 'react-plotly.js';

const Homepage = () => {
  const navigate = useNavigate();

  const [relayDevices, setRelayDevices] = useState([]);
  const [sentinelDevices, setSentinelDevices] = useState([]);
  const [tempAverages, setTempAverages ] = useState([]);
  const [humAverages, setHumAverages] = useState([])

  const layout_temperature = {
    title: 'Temperature Changes Over Time',
    xaxis: {
      title: 'Time',
      tickangle: -45,
    },
    yaxis: {
      title: 'Temperature (°C)',
    },
    margin: {
      t: 20,
      r: 20,
      b: 80,
      l: 40,
    },
  };

  const layout_humidity = {
    title: 'Humidity Changes Over Time',
    xaxis: {
      title: 'Time',
      tickangle: -45,
    },
    yaxis: {
      title: 'Humidity (%)',
    },
    line: {
      color: 'red',
      width: 2,
    },
    margin: {
      t: 20,
      r: 20,
      b: 80,
      l: 40,
    },
  }

  useEffect(() => {
    // GET relay devices
    const getRelayDevices = async () => {
      try {
        const response = await axios.get("/api/devices/relay/all")
        console.log(response.data)
        setRelayDevices(response.data)
      } catch (error) {
        console.error("Error getting relay devices:", error)
      }
    };

    // GET sentinel devices
    const getSentinelDevices = async () => {
      try {
        const response = await axios.get("/api/devices/sentinel/all")
        console.log(response.data)
        setSentinelDevices(response.data)
      } catch (error) {
        console.error("Error getting sentinel devices:", error)
      }
    };

    async function getAverages() {
      try {
        const currentUTC = new Date().toISOString(); // Current time in UTC
        const twelveHoursAgo = new Date(Date.now() - 16 * 60 * 60 * 1000).toISOString(); // 12 hours ago
        const tempResponse = await axios.get(`/api/record/average?type=1&start_timestamp=${twelveHoursAgo}&end_timestamp=${currentUTC}&interval=2`);
        const humAverages = await axios.get(`/api/record/average?type=2&start_timestamp=${twelveHoursAgo}&end_timestamp=${currentUTC}&interval=2`);
        console.log("Retrieving Data")
        setTempAverages(tempResponse.data)
        setHumAverages(humAverages.data)
      } catch (error ) {
        console.error("Error getting averages: ", error)
      }
    }

    getRelayDevices()
    getSentinelDevices()
    getAverages();
  }, [])

  return (
    <div className="homepage-container">
      <section className="table-section">
        <h2>Sentinel Devices</h2>
        <table>
          <thead>
            <tr>
              <th>Device ID</th>
              <th>Latitude</th>
              <th>Longitude</th>
              <th>Battery Life</th>
              <th>Is Connected</th>
              <th>Last Online</th>
              <th>Deployed</th>
              <th>Deployed Date</th>
              <th>Connected Devices</th>
              <th>Password</th>
              <th>Client ID</th>
            </tr>
          </thead>
          <tbody>
            {sentinelDevices.map((device, index) => (
              <tr key={index} onClick={() => navigate(`/device?sentinel=1&device_id=${device.device_id}`)}>
                <td>{device.device_id}</td>
                <td>{device.latitude}</td>
                <td>{device.longitude}</td>
                <td>{device.battery_life}</td>
                <td>{device.is_connected ? "Yes" : "No"}</td>
                <td>{device.last_online}</td>
                <td>{device.deployed ? "Yes" : "No"}</td>
                <td>{device.deployed_date}</td>
                <td>{device.numConnected_devices}</td>
                <td>{device.password}</td>
                <td>{device.client_id}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
      <section className="table-section">
        <h2>Relay Devices</h2>
        <table>
          <thead>
            <tr>
              <th>Device ID</th>
              <th>Sentinel ID</th>
              <th>Latitude</th>
              <th>Longitude</th>
              <th>Battery Life</th>
              <th>Sentinel Connection</th>
              <th>Is Connected</th>
              <th>Last Online</th>
              <th>Deployed</th>
              <th>Deployed Date</th>
              <th>Password</th>
            </tr>
          </thead>
          <tbody>
            {relayDevices.map((device, index) => (
              <tr key={index} onClick={() => navigate(`/device?sentinel=0&device_id=${device.device_id}`)}>
                <td>{device.device_id}</td>
                <td>{device.sentinel_id}</td>
                <td>{device.latitude}</td>
                <td>{device.longitude}</td>
                <td>{device.batteryLife}</td>
                <td>{device.sentinel_connection ? "Yes" : "No"}</td>
                <td>{device.is_connected ? "Yes" : "No"}</td>
                <td>{device.last_online}</td>
                <td>{device.deployed ? "Yes" : "No"}</td>
                <td>{device.deployed_date}</td>
                <td>{device.password}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>

      <div className="divider"></div>

      <h2>Temperature Record Averages</h2>

      <Plot
        data={[
          {
            x: tempAverages.map(item => item.section),
            y: tempAverages.map(item => item.value),
            type: 'scatter',
            mode: 'lines+markers',
            name: 'Temperature',
          },
        ]}
        layout={layout_temperature}
        style={{ width: '100%', height: '400px' }}
      />
      <h2>Humidity Record Averages</h2>

      <Plot
        data={[
          {
            x: humAverages.map(item => item.section),
            y: humAverages.map(item => item.value),
            type: 'scatter',
            mode: 'lines+markers',
            name: 'Humidity',
            line: { color: 'red', width: 2 }
          },
        ]}
        layout={layout_humidity}
        style={{ width: '100%', height: '400px' }}
        
      />
    </div>
  );
};

export default Homepage;
