import React, { useState } from "react";
import { MdHome, MdRocketLaunch } from "react-icons/md";
import { Link } from 'react-router-dom';

function NavBar() {
    const [selected, setSelected] = useState("home");

    return (
        <div className="navbar-container">
            <div className="navbar">
                <div
                    className={`slider ${selected === "deploy" ? "slide-right" : "slide-left"}`}
                ></div>
                
                <Link to="/">
                    <div
                        className={`nav-item ${selected === "home" ? "active" : ""}`}
                        onClick={() => setSelected("home")}
                    >
                        
                        <MdHome className="react-icon" />
                        
                    </div>
                </Link>
                <Link to="/deploy">
                    <div
                        className={`nav-item ${selected === "deploy" ? "active" : ""}`}
                        onClick={() => setSelected("deploy")}
                    >
                        
                        <MdRocketLaunch className="react-icon" />
                    </div>
                </Link>
            </div>
        </div>
    );
}

export default NavBar;
