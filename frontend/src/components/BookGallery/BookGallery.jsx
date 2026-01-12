import React from 'react';
import './BookGallery.css';

const BookGallery = ({ books, onBookClick }) => {
  return (
    <div className="book-gallery">
      <div className="book-scroll-row">
        {books.map((book, idx) => (
          <div
            key={idx}
            className="book-card1"
            onClick={() => onBookClick(book)}
            title={book.title}
          >
            <img src={book.imageUrl} alt={book.title} className="book-image" />
          </div>
        ))}
      </div>
    </div>
  );
};

export default BookGallery;
