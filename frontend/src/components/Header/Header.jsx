// src/components/Header/Header.jsx
import React from 'react';
import './Header.css';
import logo from '../../assets/VNIT Logo.webp'; // Adjust if logo file name differs

const Header = () => {
  return (
    <div className="navbar">
      <img src={logo} alt="VNIT Logo" className="logo" />
      <h3 className="institute-name">
        Visvesvaraya National Institute of Technology, Nagpur
      </h3>
    </div>
  );
};

export default Header;
