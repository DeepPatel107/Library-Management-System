import React, { useState } from 'react';
import axios from 'axios';
import './SearchBar.css';

const SearchBar = ({ setBooks }) => {
  const [query, setQuery] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;
    try {
      const response = await axios.get(`http://localhost:8085/public/similar/${encodeURIComponent(query)}`);
      setBooks(response.data); // expects an array of book objects
    } catch (error) {
      setBooks([]);
    }
  };

  return (  
    <form className="search-bar" onSubmit={handleSearch}>
      <input
        type="text"
        placeholder="Enter book name"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />
      <button type="submit">Search</button>
    </form>
  );
};

export default SearchBar;