import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home/Home';
import DashBoard from './pages/DashBoard/DashBoard';
import Profile from './pages/Profile/Profile';
import Admin from './pages/Admin/Admin';
import ProtectedRoute from './components/Auth/ProtectedRoute';

import './App.css';

const App = () => (
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/dashboard" element={<DashBoard />} />
       <Route path="/profile" element={<Profile />} />
       <Route path="/admin" element={<Admin />} />
    </Routes>
  </BrowserRouter>
);

export default App;