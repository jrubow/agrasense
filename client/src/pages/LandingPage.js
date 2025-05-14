import React from 'react';
import { Link } from 'react-router-dom';
import { IoIosWater } from "react-icons/io";
import { FaArrowsRotate, FaArrowTrendUp } from "react-icons/fa6";
import { FaSolarPanel, FaTools, FaTemperatureHigh, FaWifi, FaSun, FaPlug } from "react-icons/fa"
import { MdSpeed } from "react-icons/md"
import { IoHardwareChip } from "react-icons/io5"
import { WiHumidity } from "react-icons/wi"
import "../css/pages/landingpage.css"

export default function LandingPage() {
  return (
    <div className="landingpage-container">
      <header className="landingpage-header">
        <Link to="/"><img src="/logo_white.png" width="200px"/></Link>
      </header>

      <section className="landingpage-hero">
        <div className="landingpage-hero-text">
          <h2>Smart Agriculture,</h2>
          <h2>Smarter Decisions.</h2>
          <p>
            Cut costs and farm smarter with real-time, affordable field insights.
          </p>
          {/* Add a call-to-action button if you like */}
        </div>
        <div className="landingpage-hero-image">
          <div className="landingpage-hero-image-img">
            <img src="/device.png" alt="Product Image"/>
            {/* <p>AgraSensor</p> */}
          </div>
          <div className="landingpage-hero-image-info">
              <div className="align-parallel"><FaSun className="landingpage-info-icon" style={{color:"orange"}}/><h1>Solar Powered</h1></div>
              <div className="align-parallel"><FaWifi className="landingpage-info-icon" style={{color:"rgb(0, 150, 250)"}}/><h1>Offline Connectivity</h1></div>
              <div className="align-parallel"><FaTemperatureHigh className="landingpage-info-icon" style={{color:"rgb(215, 31, 31)"}}/><h1>Multiple Sensors</h1></div>
              <div className="align-parallel"><FaPlug className="landingpage-info-icon" style={{color:"purple"}}/><h1>Plug & Play</h1></div>
              <div className="align-parallel"><MdSpeed className="landingpage-info-icon" style={{color:"rgb(0, 150, 250)"}}/><h1>Timely Data</h1></div>
              <div className="align-parallel"><FaTools className="landingpage-info-icon" style={{color:"darkgray"}}/><h1>No Maintenance</h1></div>
          </div>
        </div>
      </section>

      <section className="landingpage-section landingpage-ratings">
        {/* <h3>What our customers say</h3>
        <div className="stars">★★★★★</div>
        <p>“This changed the way we…” – Happy Customer</p> */}
      </section>

      <section className="landingpage-section">
        <h1>Applications</h1>
        <div className="landingpage-usecases">
          <div className="landingpage-usecase">
            <IoIosWater className="landingpage-icon" style={{color:"rgb(0, 150, 250)"}}/>
            <div>
              <h4>Reduce Irrigation Costs</h4>
              <p>Automatically track soil moisture levels to avoid overwatering, cut water usage, and lower utility expenses.</p>
            </div>
          </div>
          <div className="landingpage-usecase">
            <FaArrowsRotate className="landingpage-icon" style={{color:"#1d7933ff"}}/>
            <div>
              <h4>Crop Rotation Analysis</h4>
              <p>Analyze historical and real-time soil data to optimize crop rotation schedules and improve long-term yields.</p>
            </div>
          </div>
          <div className="landingpage-usecase">
            <FaArrowTrendUp className="landingpage-icon" style={{color:"purple"}}/>
            <div>
              <h4>Monitor Crop Health</h4>
              <p>Detect early signs of stress or disease using environmental data and sensor alerts to take timely action.</p>
            </div>
          </div>
        </div>
      </section>

      <section className="landingpage-section landingpage-ratings">
        {/* <h3>What our customers say</h3>
        <div className="stars">★★★★★</div>
        <p>“This changed the way we…” – Happy Customer</p> */}
      </section>

      <footer className="landingpage-footer">
        <p>AgraSense | <a href="mailto:joshrubow@outlook.com.com">Contact us</a></p>
      </footer>
    </div>
  );
}
