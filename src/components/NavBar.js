import React from 'react';
import "../css/components/navbar.css";

const NavBar = () => {
  return (
    <nav className="navbar">
      <ul>
        <li><a href="/">Home</a></li>
        <li><a href="/map">Map</a></li>
      </ul>
    </nav>
  );
};

export default NavBar;
