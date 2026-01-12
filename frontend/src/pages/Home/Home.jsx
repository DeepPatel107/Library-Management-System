import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Home.css';
import hero_banner from '../../assets/VNIT.jpg';
import Footer from '../../components/Footer/Footer';
import Header from '../../components/Header/Header';

const Home = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [studentId, setStudentId] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  // LOGIN FUNCTION
  const handleLogin = async (e) => {
    e.preventDefault();
    const credentials = btoa(`${studentId}:${password}`);

    try {
      const response = await axios.post(
        'http://localhost:8080/user/signin',
        {},
        {
          headers: {
            Authorization: `Basic ${credentials}`,
          },
        }
      );

      const token = response.data.token;
      const role = response.data.role;

      localStorage.setItem('token', token);
      localStorage.setItem('role', role);

      setMessage('Login successful!');

      // Redirect based on role
      if (role === 'ADMIN') {
        navigate('/admin');
      } else {
        navigate('/dashboard');
      }

    } catch (error) {
      if (error.response) {
        setMessage(error.response.data || 'Login failed');
      } else {
        setMessage('Network or server error during login');
      }
    }
  };

  // ENROLL FUNCTION
  const handleEnroll = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post('http://localhost:8080/public/signup', {
        username,
        email,
        studentId,
        password,
      });

      setMessage('Enrollment successful! Please log in.');
      setIsLogin(true); // Switch to login view

    } catch (error) {
      if (error.response) {
        setMessage(error.response.data || 'Enrollment failed');
      } else {
        setMessage('Network or server error during enrollment');
      }
    }
  };

  return (
    <div className="home">
      <img src={hero_banner} alt="Library" className="banner-img" />
      <Header />

      <div className="auth-section">
        <h2>{isLogin ? 'Library Login' : 'Enroll'}</h2>

        <form onSubmit={isLogin ? handleLogin : handleEnroll}>
          {!isLogin && (
            <>
              <input
                type="text"
                placeholder="Full Name"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
              <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </>
          )}

          <input
            type="text"
            placeholder="ID Number"
            value={studentId}
            onChange={(e) => setStudentId(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button type="submit">{isLogin ? 'Login' : 'Enroll'}</button>
        </form>

        {message && <p className="message">{message}</p>}

        <hr className="divider" />
        {isLogin ? (
          <>
            <p className="not-registered">Not registered?</p>
            <button className="enroll-btn" onClick={() => setIsLogin(false)}>Enroll</button>
          </>
        ) : (
          <>
            <p className="not-registered">Already registered?</p>
            <button className="enroll-btn" onClick={() => setIsLogin(true)}>Login</button>
          </>
        )}
      </div>

      <Footer />
    </div>
  );
};

export default Home;
