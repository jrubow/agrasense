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
                
                <div
                    className={`nav-item ${selected === "home" ? "active" : ""}`}
                    onClick={() => setSelected("home")}
                >
                    <Link to="/">
                    <MdHome className="react-icon" />
                    </Link>
                </div>
                <div
                    className={`nav-item ${selected === "deploy" ? "active" : ""}`}
                    onClick={() => setSelected("deploy")}
                >
                    <Link to="/deploy">
                    <MdRocketLaunch className="react-icon" />
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default NavBar;
