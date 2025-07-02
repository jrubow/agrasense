import React, { useState, useEffect, useCallback } from 'react';
import { GoogleMap, LoadScript, Marker, InfoWindow } from '@react-google-maps/api';
import '../css/pages/homepage.css';
import { useNavigate } from "react-router-dom"
import axios from "axios"
import Plot from 'react-plotly.js';

const Homepage = () => {
  const navigate = useNavigate();

  const [relayDevices, setRelayDevices] = useState([])
  const [sentinelDevices, setSentinelDevices] = useState([])
  const [tempAverages, setTempAverages ] = useState([])
  const [humAverages, setHumAverages] = useState([])
  const [daysAgo, setDaysAgo] = useState(1)
  const [map, setMap] = useState(null);
  const [devices, setDevices] = useState([])
  const [userLocation, setUserLocation] = useState(null)
  const [activeMarker, setActiveMarker] = useState(null)
  const [selectedDevice, setSelectedDevice] = useState(null)

  const layout_temperature = {
    title: 'Average Records',
    xaxis: {
      title: 'Time',
      tickangle: -45,
    },
    yaxis: {
      title: 'Units',
    },
    autosize: true,
    paper_bgcolor: '#f5f5f5',
    plot_bgcolor: '#f5f5f5',margin: {
    l: 50,
    r: 50,
    t: 50,
    b: 50
  },
  };

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

    

    getRelayDevices()
    getSentinelDevices()
  }, [])

  useEffect(() => {
    async function getAverages() {
      try {
        const currentUTC = new Date().toISOString();
        const xDaysAgo = new Date(Date.now() - daysAgo * 24 * 60 * 60 * 1000).toISOString();
        const tempResponse = await axios.get(`/api/record/average?type=1&start_timestamp=${xDaysAgo}&end_timestamp=${currentUTC}&interval=2`);
        const humAverages = await axios.get(`/api/record/average?type=2&start_timestamp=${xDaysAgo}&end_timestamp=${currentUTC}&interval=2`);
        console.log("Retrieving Data")
        setTempAverages(tempResponse.data)
        setHumAverages(humAverages.data)
      } catch (error ) {
        console.error("Error getting averages: ", error)
      }
    }

    getAverages();
  }, [daysAgo])

  useEffect(() => {
    const getRelayDevices = async () => {
      try {
        const response = await axios.get("/api/devices/relay/all")
        console.log(response.data)
        setDevices((prevDevices) => {
          const newDevices = response.data.filter(device => 
            !prevDevices.some(existingDevice => existingDevice.device_id === device.device_id)
          );
          return [...prevDevices, ...newDevices];
        });
      } catch (error) {
        console.error("Error getting relay devices:", error)
      }
    };

    // GET sentinel devices
    const getSentinelDevices = async () => {
      try {
        const response = await axios.get("/api/devices/sentinel/all")
        console.log([...devices, ...response.data])
        setDevices((prevDevices) => {
          const newDevices = response.data.filter(device => 
            !prevDevices.some(existingDevice => existingDevice.device_id === device.device_id)
          );
          return [...prevDevices, ...newDevices];
        });
      } catch (error) {
        console.error("Error getting sentinel devices:", error)
      }
    };

    getRelayDevices()
    getSentinelDevices()
  }, []) 

  const containerStyle = {
    width: '100%',
    height: '100%',
  };

  const onLoad = useCallback(function (map) {
    setMap(map);
  }, []);

  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setUserLocation({
            lat: position.coords.latitude,
            lng: position.coords.longitude,
          });
        },
        (error) => {
          console.error("Error getting user's location:", error)
          setUserLocation({
            lat: 37.7749,
            lng: -122.4194,
          });
        }
      );
    } else {
      console.error("Geolocation is not supported by this browser.");
      setUserLocation({
        lat: 37.7749,
        lng: -122.4194,
      });
    }
  }, []);

  const center = userLocation || { lat: 37.7749, lng: -122.4194 }; 

  const handleMarkerClick = (device) => {
    setSelectedDevice(device);
    setActiveMarker(device.device_id); // Mark this marker as active
  };

  function getOnlineSentinelDevices() {
    const TWO_HOURS_MS = 2 * 60 * 60 * 1000;
    const now = Date.now();

    const recentDevices = sentinelDevices.filter(device => {
      const lastOnline = new Date(device.last_online).getTime();
      return now - lastOnline <= TWO_HOURS_MS;
    });

    return recentDevices;
  }

  function getOnlineRelayDevices() {
    const TWO_HOURS_MS = 2 * 60 * 60 * 1000;
    const now = Date.now();

    const recentDevices = relayDevices.filter(device => {
      const lastOnline = new Date(device.last_online).getTime();
      return now - lastOnline <= TWO_HOURS_MS;
    });

    return recentDevices;
  }

  const handleSlideBarChange = (event) => {
    setDaysAgo(Number(event.target.value));
  };

  const goToAccount = () => {
    navigate('/account');
  };

  return (
    <div className="homepage-container">
      <section className="homepage-sidebar-section"> 
        <ul>
          <img src="/logo_white.png" width="200px"/>
          <li>Analytics</li>
          <li>Devices</li>
          <li>Field Blocks</li>
          <li onClick={goToAccount}>
            My Account
          </li>
        </ul>
      </section>
      <div className="homepage-right-section">
        <div className="homepage-map-container">
        <LoadScript googleMapsApiKey={process.env.TESTKEY}>
          <GoogleMap
            mapContainerStyle={containerStyle}
            center={center}
            zoom={16}
            onLoad={onLoad}
          >
            {devices.map(device => (
              <Marker
                key={device.device_id}
                position={{ lat: device.latitude, lng: device.longitude }}
                onClick={() => handleMarkerClick(device)}
                icon={{
                  url: `http://maps.google.com/mapfiles/ms/icons/${device.sentinel_id != null ? "red" : "blue"}-dot.png`, // Replace with any color or image URL
                  scaledSize: new window.google.maps.Size(32, 32), // Adjust size if needed
                }}
              >
              {activeMarker === device.device_id && selectedDevice && (
                  <InfoWindow
                    position={{ lat: device.latitude, lng: device.longitude }}
                    onCloseClick={() => setActiveMarker(null)} // Close the info window
                  >
                    <div>
                      <p>Device ID: {selectedDevice.device_id}</p>
                      <p>Last Online: {selectedDevice.last_online}</p>
                      <Link to={`/device?sentinel=${selectedDevice.sentinel_id != null ? "0" : "1"}&device_id=${device.device_id}`}>View Device</Link>
                    </div>
                  </InfoWindow>
                )}
              </Marker>
            ))}
          </GoogleMap>
        </LoadScript>
        </div>
        <div className="homepage-stats-bar">
            <div>
              <p>{getOnlineSentinelDevices().length}/{sentinelDevices.length} Sentinel Devices Online</p>
            </div>
            <div>
              <p>{getOnlineRelayDevices().length}/{relayDevices.length} Relay Devices Online</p>
            </div>
            <div>
              <p>{5} Average Temp</p>
            </div>
            <div>
              <p>{5} Average Humidity</p>
            </div>
        </div>

        

        <div className="homepage-record-averages" >
          <div style={{display:"flex", alignItems:"center"}}>
            <h2>Record Averages</h2>
            <div className="homepage-slider">
              <p>Filter by</p>
              <div className="homepage-slider-button" style={daysAgo === 1 ? { backgroundColor: "var(--secondary-green)" } : {}} onClick={() => setDaysAgo(1)}>1</div>
              <div className="homepage-slider-button" style={daysAgo === 5 ? { backgroundColor: "var(--secondary-green)" } : {}} onClick={() => setDaysAgo(5)}>5</div>
              <div className="homepage-slider-button" style={daysAgo === 10 ? { backgroundColor: "var(--secondary-green)" } : {}} onClick={() => setDaysAgo(10)}>10</div>
              <div className="homepage-slider-button" style={daysAgo === 25 ? { backgroundColor: "var(--secondary-green)" } : {}} onClick={() => setDaysAgo(25)}>25</div>
              <div className="homepage-slider-button" style={daysAgo === 50 ? { backgroundColor: "var(--secondary-green)" } : {}} onClick={() => setDaysAgo(50)}>50</div>
              <p style={{paddingLeft:"10px"}}>days</p>
            </div>
          </div>
          <Plot
            data={[
              {
                x: tempAverages.map(item => item.section),
                y: tempAverages.map(item => item.value),
                type: 'scatter',
                mode: 'lines+markers',
                name: 'Temperature',
              },
              {
                x: humAverages.map(item => item.section),
                y: humAverages.map(item => item.value),
                type: 'scatter',
                mode: 'lines+markers',
                name: 'Humidity',
                line: { color: 'red', width: 2 }
              },
            ]}
            layout={layout_temperature}
            style={{ width: 'calc(100% - 20px)', height: 'calc(100% - 48px)', backgroundColor: '#f5f5f5'}}
            useResizeHandler={true}
          />
        </div>
      </div>
    </div>
  );
};

export default Homepage;
