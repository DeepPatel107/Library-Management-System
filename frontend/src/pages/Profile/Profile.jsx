import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import defaultAvatar from '../../assets/spiderman-icon.jpg';
import hero_banner from '../../assets/profile.jpg';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';

import './Profile.css';

const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [issuedBooks, setIssuedBooks] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) return;

    // Fetch profile
    axios
      .get('https://library-management-system-production-c592.up.railway.app/user/profile', {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setProfile(res.data))
      .catch((err) => {
        console.error('Error fetching profile:', err);
        alert('Session expired. Please login again.');
        localStorage.removeItem('token');
        navigate('/');
      });

    // Fetch issued books
    axios
      .get('https://library-management-system-production-c592.up.railway.app/user/issued-books', {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setIssuedBooks(res.data))
      .catch((err) => console.error('Error fetching issued books:', err));
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/');
  };

  if (!profile) return <div className="profile-container">Loading...</div>;

  // Extract only issued copies with issueDate and returnDate
  const issuedCopies = issuedBooks.flatMap((book) =>
    (book.copiesInfo || []).filter(copy =>
      copy.studentId === profile.studentId 
     
    ).map(copy => ({
      ...copy,
      title: book.title,
      imageUrl: book.imageUrl,
    }))
  );

  return (
    <div className="profile">
      <Header />
      <img src={hero_banner} alt="Library" className="banner-img" />

      <div className="profile-container">
        <div className="profile-header">
          <button className="back-button" onClick={() => navigate('/dashboard')}>
            ← Dashboard
          </button>
          <div className="profile-top-right">
            <img src={defaultAvatar} alt="Profile" className="profile-avatar" />
            <button className="logout-button" onClick={handleLogout}>Logout</button>
          </div>
        </div>

        <div className="body">
          <div className="profile-details">
            <div>
              <img src={profile.imageUrl || defaultAvatar} alt="Profile" className="prof-img" />
            </div>
            <div className="prof-info">
              <p><strong>Username:</strong> {profile.username}</p>
              <p><strong>Email:</strong> {profile.email}</p>
              <p><strong>Student ID:</strong> {profile.studentId}</p>
              <p><strong>Issued Books:</strong> {issuedCopies.length}</p>
            </div>
          </div>

          <div className="issued-books">
            {issuedCopies.length > 0 ? (
              <div className="books-grid">
                {issuedCopies.map((copy, idx) => (
                  <div key={idx} className="book-card">
                    <img src={copy.imageUrl} alt={copy.title} className="book-image" />
                    <div className="book-info">
                      <p><strong>Title:</strong> {copy.title}</p>
                      <p><strong>Copy ID:</strong> {copy.copyId?.timestamp}</p>
                      <p><strong>Issue Date:</strong> {new Date(copy.issueDate).toLocaleDateString()}</p>
                      <p><strong>Return Date:</strong> {new Date(copy.returnDate).toLocaleDateString()}</p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p style={{ color: 'white', fontSize: '1.2rem' }}>No books issued.</p>
            )}
          </div>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default Profile;
