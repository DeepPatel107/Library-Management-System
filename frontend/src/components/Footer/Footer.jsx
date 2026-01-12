import React from 'react'
import  './Footer.css'
import fotterlogo from '../../assets/logofooter.png'
const Footer = () => {
  return (
    <div className='footer'>
      <div className="fotterlinks">
      <ul className='implinks'>
        <li><a href="http://172.31.4.24:8080/exam/faces/infrastructure/SLogin.jsf">VNIT Login</a></li>
        <li><a href="https://vnit.ac.in/section/hostel/">Hostel Section</a></li>
        <li><a href="https://vnit.ac.in/~">Administration</a></li>
      </ul>
      <ul className='implinks'>
        <li><a href="">Help</a></li>
        <li><a href="">Support</a></li>
        <li><a href="">Contact Us</a></li>
      </ul>
      </div>
      <img src={fotterlogo} alt=""  className='footerlogo'/>
    </div>
  )
}

export default Footer
