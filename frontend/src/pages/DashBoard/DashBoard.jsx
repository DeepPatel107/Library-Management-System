import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import SearchBar from '../../components/SearchBar/SearchBar';
import BookGallery from '../../components/BookGallery/BookGallery';
import BookDetails from '../../components/BookDetail/BookDetail';
import hero_banner from '../../assets/backgnd.jpg';
import profileIcon from '../../assets/spiderman-icon.jpg' // <-- Make sure this exists
import './Dashboard.css';
const BASE_URL = import.meta.env.VITE_API_BASE_URL;

const defaultBooks = [
  {
    title: "Clean Code",
    author: "Robert C. Martin",
    description: "A Handbook of Agile Software Craftsmanship",
    imageUrl: "https://m.media-amazon.com/images/I/41jEbK-jG+L.jpg",
    copies: 5
  },
  {
    title: "The Pragmatic Programmer",
    author: "Andrew Hunt and David Thomas",
    description: "Your Journey to Mastery, 20th Anniversary Edition",
    imageUrl: "https://m.media-amazon.com/images/I/41as+WafrFL._SX258_BO1,204,203,200_.jpg",
    copies: 3
  },
  {
    title: "System Design Interview",
    author: "Alex Xu",
    description: "An insider's guide to approaching and communicating complex system design problems in technical interviews.",
    imageUrl: "https://imgs.search.brave.com/EscsHdJWOV3yHibTbiVLzQQp_2SK39JBNfJPqCKCAeI/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9waWN0/dXJlcy5hYmVib29r/cy5jb20vaXNibi85/NzgxNzM2MDQ5MTUw/LXVzLmpwZw",
    copies: 6
  },
  {
    title: "Communicating Systems with UML 2",
    author: "Michel Soria",
    description: "Modeling and design of communicating systems using UML notation.",
    imageUrl: "https://imgs.search.brave.com/z-mOivN4IGRovxOT8KM459zb_1wjuA1Ez5eIxLfx3Ns/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NTFUeFlJTmZEd0wu/anBn",
    copies: 4
  },
  {
    title: "Distributed Systems: Concepts and Design",
    author: "George Coulouris",
    description: "Comprehensive coverage of distributed systems principles and inter-process communication.",
    imageUrl: "https://imgs.search.brave.com/MZYXaE0breHuZDW38zyF8-yW7bbWj14UUS20a_nDayg/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NDE1czJqREd1NEwu/anBn",
    copies: 5
  },
  {
    title: "The Art of System Architecture",
    author: "Mark Maier",
    description: "Methods for communicating and documenting complex system architectures.",
    imageUrl: "https://imgs.search.brave.com/cr3WLkpcEXDq9Ic_NBDyBsojSnHDhTtRBRO0Bo0ZsjI/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NzFKTDJ3STlMMkwu/anBn",
    copies: 4
  },
  {
    title: "Enterprise Integration Patterns",
    author: "Gregor Hohpe",
    description: "Designing, building, and documenting messaging systems for enterprise communication.",
    imageUrl: "https://imgs.search.brave.com/6AkL1etGFI5tAqYJC1uOtvTqk8lY39gyYYGFKu7tq0M/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/aW5mb3JtaXQuY29t/L1Nob3dDb3Zlci5h/c3B4P2lzYm49OTc4/MDMyMTIwMDY4NiZ0/eXBlPWY",
    copies: 5
  }

];

const Dashboard = () => {
  const [books, setBooks] = useState([]);
  const [selectedBook, setSelectedBook] = useState(defaultBooks[0]);
  const navigate = useNavigate();

  useEffect(() => {
    if (books.length > 0) {
      setSelectedBook(books[0]);
    } else {
      setSelectedBook(defaultBooks[0]);
    }
  }, [books]);

  return (
    <div className="dashboard">
      
      <img
        src={profileIcon}
        alt="Profile"
        className="profile-icon"
        onClick={() => navigate('/profile')}
      />

      <Header />
      <img src={hero_banner} alt="Library" className="banner-img" />
      <SearchBar setBooks={setBooks} />

      <div className="dashboard-content">
        <div className="gallery-section">
          <BookGallery books={books.length > 0 ? books : defaultBooks} onBookClick={setSelectedBook} />
        </div>
        <div className="details-section">
          <BookDetails book={selectedBook} />
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default Dashboard;
