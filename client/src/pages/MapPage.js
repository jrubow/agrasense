// MapPage.js
import React, { useState, useCallback } from 'react';
import { GoogleMap, LoadScript, Marker } from '@react-google-maps/api';

const MapPage = () => {
  const [map, setMap] = useState(null);
  
  // Google Maps container style
  const containerStyle = {
    width: '100%',
    height: '400px',
  };

  // Coordinates for the center of the map
  const center = {
    lat: 37.7749, // Latitude for San Francisco
    lng: -122.4194, // Longitude for San Francisco
  };

  // Markers with coordinates (example)
  const markers = [
    { id: 1, lat: 37.7749, lng: -122.4194 }, // San Francisco
    { id: 2, lat: 34.0522, lng: -118.2437 }, // Los Angeles
    { id: 3, lat: 40.7128, lng: -74.0060 },  // New York
  ];

  // On map load
  const onLoad = useCallback(function (map) {
    setMap(map);
  }, []);

  return (
    <div>
      <h1>Map Page</h1>
      <LoadScript googleMapsApiKey={process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY}>
        <GoogleMap
          mapContainerStyle={containerStyle}
          center={center}
          zoom={5}
          onLoad={onLoad}
        >
          {markers.map(marker => (
            <Marker
              key={marker.id}
              position={{ lat: marker.lat, lng: marker.lng }}
            />
          ))}
        </GoogleMap>
      </LoadScript>
    </div>
  );
};

export default MapPage;
