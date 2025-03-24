import React, { useState, useEffect } from 'react';
import {Link, useNavigate} from "react-router-dom"
import '../css/pages/devicepage.css';
import Plot from 'react-plotly.js';
import axios from "axios"

const DevicePage = () => {
  // Extract query parameters from URL
  const queryParams = new URLSearchParams(window.location.search)
  const sentinel = queryParams.get('sentinel')
  const deviceId = queryParams.get('device_id')
  const [relayDevices, setRelayDevices] = useState([])
  const [device, setDevice] = useState(null)
  const [tempN, setTempN] = useState([])
  const [lastOnlineDifferential, setLastOnlineDifferential ] = useState(0)
  const navigate = useNavigate()

  const layout = {
    title: 'Temperature Changes Over Time',
    xaxis: {
      title: 'Time',
      tickangle: -45,  // Rotate the time labels for better readability
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


    useEffect(() => {
        async function getDevice() {
            if (device) {
                setLastOnlineDifferential(new Date() - new Date(device.last_online));
            }

            if (sentinel === '1') {
                try {
                    const relayResponse = await axios.get(`/api/devices/relay/network/${deviceId}`)
                    setRelayDevices(relayResponse.data);
                    const deviceResponse = await axios.get(`/api/devices/sentinel/get/${deviceId}`)
                    console.log(deviceResponse.data)
                    setDevice(deviceResponse.data)
                } catch (error) {
                    console.error("Error getting relay devices:", error)
                }
                
            } else {
                try {
                    const response = await axios.get(`/api/devices/relay/get/${deviceId}`)
                    setDevice(response.data);
                } catch (error) {
                    console.error("Error getting relay devices:", error)
                }
            }
        }

        async function getNRecords() {
            try {
                const response = await axios.get(`/api/record/recent?device_id=${deviceId}&n=25`)
                setTempN(response.data)
                console.log(response.data)

            } catch (error) {
                console.error("Error getting n records", error)
            }
        }

        getDevice()
        getNRecords()
    }, [sentinel, deviceId])

    useEffect(() => {
        if (device && device.last_online != null) {
            setLastOnlineDifferential(new Date() - new Date(device.last_online));
        }
    }, [device]);

  return (
    <div className="devicepage-top-container">
        <div className="devicepage-info-container">
            {device != null ? <div className="devicepage-container" >
                <div className="devicepage-header">
                    <div className="online-status-bubble" style={lastOnlineDifferential > 120000 || lastOnlineDifferential == 0 ? {backgroundColor:"red"} : {backgroundColor:"green"}}></div>
                    <h1>{sentinel === "1" ? "Sentinel " : "Relay "}Device Details</h1>
                </div>
                { lastOnlineDifferential > 120000 && lastOnlineDifferential != 0 ? <h3 style={{color:"red"}}>Device has not reported for {Math.round(lastOnlineDifferential / 60000) } minutes</h3> : ""}
                <div className="device-info">
                    <p><strong>Device ID:</strong> {device.device_id}</p>
                    <p><strong>Battery Life:</strong> {device.battery_life}</p>
                    <p><strong>Is Deployed:</strong> {device.deployed ? "Yes" : "No"}</p>
                    <p><strong>Deployed Date:</strong> {device.deployed_date}</p>
                    <p><strong>Is Connected:</strong> {device.is_connected ? "Yes" : "No"}</p>
                    <p><strong>Last Online:</strong> {device.last_online}</p>
                    <p><strong>Latitude:</strong> {device.latitude}</p>
                    <p><strong>Longitude:</strong> {device.longitude}</p>
                    {sentinel === "1" ? 
                        <p><strong># of Connected Devices:</strong> {relayDevices.length}</p> 
                        : 
                        <p><strong>Sentinel Id:</strong> <Link to={"/device?sentinel=1&device_id=" + device.sentinel_id}>{device.sentinel_id}</Link></p> }
                </div>
            </div> : ""}
            {sentinel === "1" ? "" : 
                <Plot
                    data={[
                        {
                        x: tempN.map(item => item.timestamp),
                        y: tempN.map(item => item.value),
                        type: 'scatter',
                        mode: 'lines+markers',
                        name: 'Temperature',
                        },
                    ]}
                    layout={layout}
                    style={{ width: '100%', height: '400px' }}
                    />

            }
            { sentinel === "1" ? 
                <section className="table-section">
            <h2>Connected Relay Devices</h2>
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
                    <td>{device.battery_life}</td>
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
        </section> : ""}
        </div>
    </div>
  );
};

export default DevicePage;
