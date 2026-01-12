import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import profileIcon from '../../assets/spiderman-icon.jpg';
import { useNavigate } from 'react-router-dom';
import './Admin.css';

const actions = {
  "Issue Book": ["Student ID", "Book Title"],
  "Return Book": ["Student ID", "Book Title"],
  "Add New Book": ["Title", "Author", "Description", "Image URL", "Total Copies"],
  "Add Copies of Book": ["Book Title", "Number of Copies"],
  "Update Book": ["Book Title", "New Description", "New Author", "New Image URL"],
  "Get Books Issued by User": ["Student ID"],
  "Get Users Who Issued Book": ["Book Title"],
  "Get All Books": [],
  "Reset User Password": ["Student ID", "New Password"],
  "Get Overdue Books": [],
  "Delete User": ["Student ID"],
  "Delete Book Copy": ["Copy ID"],
  "Find Similar Books": ["Book Title"]
};

const Admin = () => {
  const navigate = useNavigate();
  const [selectedAction, setSelectedAction] = useState('Issue Book');
  const [formData, setFormData] = useState({});
  const [responseMsg, setResponseMsg] = useState('');

  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');

  // Redirect if not admin
  useEffect(() => {
    if (role !== 'ADMIN') {
      alert('❌ Not authorized as Admin.');
      navigate('/dashboard');
    }
  }, [role, navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    navigate('/');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setResponseMsg('');

    try {
      let url = '';
      let method = 'POST';
      let data = {};

      switch (selectedAction) {
        case 'Issue Book':
          url = `/admin/issue/title/${formData['Book Title']}/to/${formData['Student ID']}`;
          break;
        case 'Return Book':
          url = `/admin/return/title/${formData['Book Title']}/from/${formData['Student ID']}`;
          break;
        case 'Add New Book':
          url = '/admin/add';
          data = {
            title: formData['Title'],
            author: formData['Author'],
            description: formData['Description'],
            imageUrl: formData['Image URL'],
            copies: parseInt(formData['Total Copies']),
          };
          break;
        case 'Add Copies of Book':
          url = `/admin/add-copies/${formData['Book Title']}`;
          data = { numberOfCopies: parseInt(formData['Number of Copies']) };
          break;
        case 'Update Book':
          url = `/admin/update/title/${formData['Book Title']}`;
          method = 'PUT';
          data = {
            description: formData['New Description'],
            author: formData['New Author'],
            imageUrl: formData['New Image URL'],
          };
          break;
        case 'Get Books Issued by User':
          url = `/admin/issued-books/${formData['Student ID']}`;
          method = 'GET';
          break;
        case 'Get Users Who Issued Book':
          url = `/admin/users-issued/title/${formData['Book Title']}`;
          method = 'GET';
          break;
        case 'Get All Books':
          url = `/admin/all-books`;
          method = 'GET';
          break;
        case 'Reset User Password':
          url = `/admin/reset-password/${formData['Student ID']}`;
          method = 'PUT';
          data = { newPassword: formData['New Password'] };
          break;
        case 'Get Overdue Books':
          url = `/admin/overdue-books`;
          method = 'GET';
          break;
        case 'Delete User':
          url = `/admin/${formData['Student ID']}`;
          method = 'DELETE';
          break;
        case 'Delete Book Copy':
          url = `/admin/delete-copy/${formData['Copy ID']}`;
          method = 'DELETE';
          break;
        case 'Find Similar Books':
          url = `/admin/similar/${formData['Book Title']}`;
          method = 'GET';
          break;
        default:
          return;
      }

      const config = {
        method,
        url: `http://localhost:8080${url}`,
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      };

      if (method !== 'GET' && method !== 'DELETE') {
        config.data = data;
      }

      const res = await axios(config);
      if (res.status === 200) {
      setResponseMsg(res.data.message || 'Success ✅');

      // Show popup on success
      if (selectedAction === 'Issue Book') {
        alert('✅ Book issued successfully!');
      } else if (selectedAction === 'Return Book') {
        alert('✅ Book returned successfully!');
      }else if (selectedAction === 'Add New Book') {
        alert('✅ Book added successfully!');
      }else if (selectedAction === 'Add Copies of Book') {
        alert('✅ Copies added successfully!');
      }else if (selectedAction === 'Update Book') {
        alert('✅ Book updated successfully!');
      }else if (selectedAction === 'Reset User Password') {
        alert('✅ Password reset successfully!');
      }else if (selectedAction === 'Delete User') {
        alert('✅ User deleted successfully!');
      }else if (selectedAction === 'Delete Book Copy') {
        alert('✅ Book copy deleted successfully!');
      }

    }

      setResponseMsg(JSON.stringify(res.data, null, 2));
    } catch (error) {
      if (error.response) {
        if (error.response.status === 403) {
          setResponseMsg("❌ Forbidden (403): You are not authorized. Please ensure you're logged in as an Admin.");
        } else if (error.response.status === 401) {
          setResponseMsg("❌ Unauthorized (401): Invalid or expired token.");
        } else {
          setResponseMsg(error.response.data?.error || '❌ Operation failed');
        }
      } else {
        setResponseMsg("Something went wrong.");
      }
      console.error(error);
    }
  };

  const renderIssuedBooksGrid = () => {
  try {
    const books = JSON.parse(responseMsg);

    if (!Array.isArray(books)) return null;

    return (
      <div className="issued-books-grid">
        {books.map((book, index) => {
          // Suppose you have currentUserId from auth
          const issuedCopy = book.copiesInfo?.find(copy => copy.issuedTo === currentUserId);

          return (
            <div key={index} className="book-card3">
              <h4>{book.title}</h4>
              <p><strong>Copy ID:</strong> {issuedCopy?.copyId?.timestamp}</p>
              <p><strong>Issued On:</strong> {new Date(issuedCopy?.issueDate).toLocaleDateString()}</p>
              <p><strong>Return By:</strong> {new Date(issuedCopy?.returnDate).toLocaleDateString()}</p>
            </div>
          );
        })}
      </div>
    );
  } catch (e) {
    console.error("Error parsing books data:", e);
  }
};

const renderUsersWhoIssuedBookGrid = () => {
  try {
    const users = JSON.parse(responseMsg);
    if (!Array.isArray(users)) return null;

    return (
      <div className="issued-books-grid">
        {users.map((user, index) => (
          <div key={index} className="book-card3">
            <h4>{user.username}</h4>
            <p><strong>Student ID:</strong> {user.studentId}</p>
          </div>
        ))}
      </div>
    );
  } catch (e) {
    console.error("Error parsing books data:", e);
  }
};


const renderOverdueBooksGrid = () => {
  try {
    const books = JSON.parse(responseMsg);
    if (!Array.isArray(books)) return null;

    return (
      <div className="issued-books-grid">
        {books.map((book, index) => {
          const returnDate = new Date(book.returnDate);

          // check overdue condition
          const isOverdue = returnDate < new Date();

          return (
            <div key={index} className="book-card3">
              <h4>{book.title}</h4>
              <p><strong>Student ID:</strong> {book.studentId}</p>
              <p><strong>Issued On:</strong> {new Date(book.issueDate).toLocaleDateString()}</p>
              <p><strong>Was Due On:</strong> {returnDate.toLocaleDateString()}</p>
              <p><strong>Status:</strong> {isOverdue ? "❌ Overdue" : "✅ On Time"}</p>
            </div>
          );
        })}
      </div>
    );
  } catch (e) {
        console.error("Error parsing books data:", e);

  }
};


const renderSimilarBooksGrid = () => {
  try {
    const books = JSON.parse(responseMsg);
    if (!Array.isArray(books)) return null;

    return (
      <div className="issued-books-grid">
        {books.map((book, idx) => (
          <div className="book-card3" key={idx}>
            <img src={book.imageUrl} alt={book.title} />
            <h4>{book.title}</h4>
            <p><strong>Author:</strong> {book.author}</p>
            <p style={{ fontSize: '0.9rem', textAlign: 'center' }}>
              {book.description}
            </p>
          </div>
        ))}
      </div>
    );
  } catch (e) {
    console.error("Error parsing books data:", e);
  }
};



const renderAllBooksGrid = () => {
  try {
    const books = JSON.parse(responseMsg);
    if (!Array.isArray(books)) return null;

    return (
      <div className="issued-books-grid">
        {books.map((book, index) => (
          <div key={index} className="book-card3">
            <img src={book.imageUrl} alt={book.title} />
            <h4>{book.title}</h4>
            <p><strong>Author:</strong> {book.author}</p>
            <p><strong>Copies:</strong> {book.copies}</p>
            <p style={{ textAlign: 'center', fontSize: '0.9rem' }}>
              {book.description}
            </p>
          </div>
        ))}
      </div>
    );
  } catch (e) {
    console.error("Error parsing books data:", e);
  }
};


  const renderForm = () => {
    const fields = actions[selectedAction];
    const noInputActions = ["Get All Books", "Get Overdue Books"];

    return (
      <div className="admin-form-section">
        <h3>{selectedAction}</h3>

        {fields.length > 0 && (
          <form className="admin-form">
            {fields.map((field, index) => (
              <input
                key={index}
                type={field.toLowerCase().includes("password") ? "password" : "text"}
                placeholder={field}
                value={formData[field] || ''}
                onChange={(e) =>
                  setFormData({ ...formData, [field]: e.target.value })
                }
              />
            ))}
            <button type="submit" onClick={handleSubmit}>Submit</button>
          </form>
        )}

        {noInputActions.includes(selectedAction) && (
          <button className="fetch-button" onClick={handleSubmit}>Fetch</button>
        )}

        {selectedAction === "Get Books Issued by User"
          ? renderIssuedBooksGrid()
          : selectedAction === "Get Users Who Issued Book"
          ? renderUsersWhoIssuedBookGrid()
          : selectedAction === "Get All Books"
          ? renderAllBooksGrid()
          : selectedAction === "Get Overdue Books"
          ? renderOverdueBooksGrid()
          : selectedAction === "Find Book by Title"
          ? renderSimilarBooksGrid()
          : responseMsg && <pre className="admin-response-box">{responseMsg}</pre>
        }




      </div>
    );
  };

  

  return (
    <div className="admin-page">
      <Header />
      <img src={profileIcon} alt="Profile" className="admin-profile-icon" />
      <button onClick={handleLogout} className="logout-btn">Logout</button>

      <div className="admin-container">
        <div className="admin-buttons-left">
          {Object.keys(actions).map((action) => (
            <button
              key={action}
              className={`admin-btn ${selectedAction === action ? 'active' : ''}`}
              onClick={() => {
                setSelectedAction(action);
                setFormData({});
                setResponseMsg('');
              }}
            >
              {action}
            </button>
          ))}
        </div>

        <div className="admin-details-panel">
          {renderForm()}
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default Admin;
