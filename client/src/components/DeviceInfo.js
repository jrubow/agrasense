import React, {useState} from 'react'
import "../css/components/deviceinfo.css"

function DeviceInfo({device, selected, setSelected}) {
  return (
    <div className='device-info-container' style={{ border: selected === device.deviceId ? "5px solid lightgreen" : "" }} onClick={() => setSelected(device.deviceId)}>

      <div className="device-title">
        {device.deviceId}
      </div>
      <div className="device-status">
        {device.isOnline ? "Online" : "Offline"}
      </div>
      <div>
        {device.isOnline ? "Sentinel Device" : "Relay Device"}
      </div>
    </div>
  )
}

export default DeviceInfo