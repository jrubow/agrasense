import React, { useState, useCallback, useEffect } from 'react';
import { GoogleMap, LoadScript, Marker, InfoWindow } from '@react-google-maps/api';
import {Link} from "react-router-dom"
import axios from "axios"
import "../css/pages/mappage.css"

const MapPage = () => {
  const [map, setMap] = useState(null)
  const [devices, setDevices] = useState([])
  const [userLocation, setUserLocation] = useState(null)
  const [activeMarker, setActiveMarker] = useState(null)
  const [selectedDevice, setSelectedDevice] = useState(null)

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

  return (
    <div className="map-container">
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
  );
};

export default MapPage;
