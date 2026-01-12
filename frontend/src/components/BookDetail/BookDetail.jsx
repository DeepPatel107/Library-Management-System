import React from "react";
import "./BookDetail.css";

const BookDetail = ({ book }) => {
  if (!book) return null;

  return (
    <div className="book-detail-container">
      <div className="detail-top">
        <img src={book.imageUrl} alt={book.title} className="detail-image" />

        <div className="detail-meta">
          <h2 className="detail-title">{book.title}</h2>
          <p className="detail-author">— by {book.author}</p>
          <p className="detail-description">{book.description}</p>
        </div>
      </div>


      <div className="detail-copies">
        Available: <strong>{book.copies} </strong> copies
      </div>
    </div>
  );
};

export default BookDetail;
