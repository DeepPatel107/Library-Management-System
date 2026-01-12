package Library.backend.Service;

import Library.backend.Model.*;
import Library.backend.Repository.BookCopyRepository;
import Library.backend.Repository.BookRepository;
import Library.backend.Repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Book> getAll(){
        return bookRepository.findAll();
    }

    public Book findByTitle(String title){
        return bookRepository.findByTitleIgnoreCase(title);
    }

    public void addBook(Book newBook){
        bookRepository.save(newBook);
    }

    public void addBooks(List<Book> books) {
        bookRepository.saveAll(books);
    }


    public boolean partialUpdateBook(String title, BookUpdateDTO updates) {
        Book book = bookRepository.findByTitleIgnoreCase(title);
        if (book == null) return false;

        if (updates.getTitle() != null) book.setTitle(updates.getTitle());
        if (updates.getAuthor() != null) book.setAuthor(updates.getAuthor());
        if (updates.getDescription() != null) book.setDescription(updates.getDescription());
        if (updates.getImageUrl() != null) book.setImageUrl(updates.getImageUrl());

        bookRepository.save(book);
        return true;
    }



    public boolean deleteBook(String title) {
        if (bookRepository.existsByTitle(title)) {
            bookRepository.deleteByTitle(title);
            return true;
        }
        return false;
    }

    @Autowired
    private BookCopyRepository bookCopyRepository;

    public String addNewBook(Book book) {
        int copies = book.getCopies();
        List<BookCopyInfo> copyList = new ArrayList<>();

        for (int i = 0; i < copies; i++) {
            BookCopyInfo copy = new BookCopyInfo();
            copy.setCopyId(new ObjectId());      // unique ID for this copy
            copy.setIssued(false);
            copy.setStudentId(-1);
            copy.setIssueDate(null);
            copy.setReturnDate(null);
            copyList.add(copy);
        }

        book.setCopiesInfo(copyList);
        bookRepository.save(book);
        return "Book added with " + copies + " copies";
    }



    public String addCopiesToExistingBook(ObjectId bookId, int additionalCopies) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) return "Book not found";

        Book book = bookOpt.get();

        for (int i = 0; i < additionalCopies; i++) {
            BookCopyInfo copy = new BookCopyInfo();
            copy.setCopyId(new ObjectId());
            copy.setBookId(bookId);
            copy.setIssued(false);
            copy.setStudentId(-1);
            book.getCopiesInfo().add(copy);
        }

        book.setCopies(book.getCopies() + additionalCopies);
        bookRepository.save(book);

        return additionalCopies + " new copies added to book: " + book.getTitle();
    }



    public String addMultipleBooks(List<Book> books) {
        for (Book book : books) {
            // Save base book first to get its ObjectId
            Book savedBook = bookRepository.save(book);
            List<BookCopyInfo> copies = new ArrayList<>();

            for (int i = 0; i < savedBook.getCopies(); i++) {
                BookCopyInfo copy = new BookCopyInfo();
                copy.setCopyId(new ObjectId());
                copy.setBookId(savedBook.getId());
                copy.setIssued(false);
                copy.setStudentId(-1);
                copies.add(copy);
            }

            savedBook.setCopiesInfo(copies);
            bookRepository.save(savedBook); // Update with copies
        }
        return books.size() + " books added successfully.";
    }


    public String issueOrReissueBookByTitle(String title, int studentId) {
        Optional<Book> bookOpt = bookRepository.findByTitle(title);
        Optional<User> userOpt = userRepository.findByStudentId(studentId);
        if (bookOpt.isEmpty()) return "Book not found";
        if (userOpt.isEmpty()) return "Student not found";

        Book book = bookOpt.get();
        User user = userOpt.get();
        Date now = new Date();

        for (BookCopyInfo copy : book.getCopiesInfo()) {
            if (copy.getStudentId() == studentId) {
                if (now.after(copy.getReturnDate())) {
                    return "Cannot reissue overdue book. Please return it first.";
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(copy.getReturnDate());
                cal.add(Calendar.DAY_OF_MONTH, 14);
                copy.setReturnDate(cal.getTime());
                bookRepository.save(book);
                return "Book reissued successfully. New return date: " + copy.getReturnDate();
            }
        }

        for (BookCopyInfo copy : book.getCopiesInfo()) {
            if (copy.getStudentId() == -1) {
                copy.setStudentId(studentId);
                copy.setIssueDate(now);
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                cal.add(Calendar.DAY_OF_MONTH, 14);
                copy.setReturnDate(cal.getTime());
                copy.setIssued(true); // 🔧 FIX: Mark as issued
                book.setCopies(book.getCopies() - 1);

                if (!user.getIssuedBooks().contains(book.getId())) {
                    user.getIssuedBooks().add(book.getId());
                }

                bookRepository.save(book);
                userRepository.save(user);
                return "Book issued successfully. Return date: " + copy.getReturnDate();
            }
        }

        return "No copies available";
    }

    public String returnBookByTitle(String title, int studentId) {
        Optional<Book> bookOpt = bookRepository.findByTitle(title);
        Optional<User> userOpt = userRepository.findByStudentId(studentId);
        if (bookOpt.isEmpty()) return "Book not found";
        if (userOpt.isEmpty()) return "Student not found";

        Book book = bookOpt.get();
        User user = userOpt.get();

        for (BookCopyInfo copy : book.getCopiesInfo()) {
            if (copy.getStudentId() == studentId) {
                copy.setStudentId(-1);
                copy.setIssueDate(null);
                copy.setReturnDate(null);
                copy.setIssued(false); // 🔧 FIX: Mark as not issued
                book.setCopies(book.getCopies() + 1);

                user.getIssuedBooks().remove(book.getId());

                bookRepository.save(book);
                userRepository.save(user);

                return "Book returned successfully";
            }
        }

        return "Book was not issued to this student";
    }


    public String returnBook(ObjectId bookId, int studentId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Optional<User> userOpt = userRepository.findByStudentId(studentId);
        if (bookOpt.isEmpty()) return "Book not found";
        if (userOpt.isEmpty()) return "Student not found";

        Book book = bookOpt.get();
        User user = userOpt.get();

        for (BookCopyInfo copy : book.getCopiesInfo()) {
            if (copy.getStudentId() == studentId) {
                copy.setStudentId(-1);
                copy.setIssueDate(null);
                copy.setReturnDate(null);
                book.setCopies(book.getCopies() + 1);

                user.getIssuedBooks().remove(book.getId());

                bookRepository.save(book);
                userRepository.save(user);

                return "Book returned successfully";
            }
        }
        return "Book was not issued to this student";
    }

    public List<BookCopyInfo> getCopiesByBookId(ObjectId bookId) {
        return bookCopyRepository.findByBookId(bookId);
    }



    public List<Book> getBooksIssuedByStudent(int studentId) {
        Optional<User> userOpt = userRepository.findByStudentId(studentId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<ObjectId> bookIds = user.getIssuedBooks();
            List<Book> books = bookRepository.findAllById(bookIds);

            // Filter only the issued copies for the student
            for (Book book : books) {
                List<BookCopyInfo> issuedCopies = book.getCopiesInfo().stream()
                        .filter(copy -> copy.getStudentId() == studentId && copy.getIssueDate() != null)
                        .toList();  // or .collect(Collectors.toList()) if using Java 8
                book.setCopiesInfo(issuedCopies);
            }

            return books;
        }

        return new ArrayList<>();
    }



    public List<Book> findSimilarBooks(String title) {
        System.out.println("Searching for similar books with title: " + title);

        Book baseBook = bookRepository.findByTitleIgnoreCase(title);

        // Try partial match if exact title not found
        if (baseBook == null) {
            List<Book> partialMatches = bookRepository.findByTitleContainingIgnoreCase(title);
            if (!partialMatches.isEmpty()) {
                baseBook = partialMatches.get(0);
            }
        }

        if (baseBook == null) {
            System.out.println("No base book found for title: " + title);
            return getFallbackBooks(); // fallback if even partial match fails
        }

        ObjectId baseId = baseBook.getId();
        String baseAuthor = baseBook.getAuthor();

        Set<Book> similarBooks = new HashSet<>();

        // Find books by same author (excluding the base book)
        List<Book> byAuthor = bookRepository.findByAuthorIgnoreCaseAndIdNot(baseAuthor, baseId);
        similarBooks.addAll(byAuthor);

        // Split title into keywords and search
        String[] keywords = baseBook.getTitle().split("\\s+");
        for (String keyword : keywords) {
            if (keyword.length() > 2) {
                List<Book> byTitleKeyword = bookRepository.findByTitleContainingIgnoreCaseAndIdNot(keyword, baseId);
                similarBooks.addAll(byTitleKeyword);
            }
        }

        List<Book> result = new ArrayList<>(similarBooks);

        if (result.isEmpty()) {
            System.out.println("No similar books found. Returning fallback list.");
            return getFallbackBooks();
        }

        System.out.println("Similar books found: " + result.size());
        return result;
    }

    private List<Book> getFallbackBooks() {
        List<Book> allBooks = bookRepository.findAll();
        System.out.println("Fallback: returning " + allBooks.size() + " books.");
        return allBooks.size() > 5 ? allBooks.subList(0, 5) : allBooks; // Limit fallback to 5 books
    }
    public List<Map<String, Object>> getOverdueBooks() {
        List<Book> allBooks = bookRepository.findAll();
        List<Map<String, Object>> overdueList = new ArrayList<>();

        Date now = new Date();

        for (Book book : allBooks) {
            List<BookCopyInfo> copies = book.getCopiesInfo();
            for (int i = 0; i < copies.size(); i++) {
                BookCopyInfo copy = copies.get(i);
                if (copy.getReturnDate() != null && copy.getReturnDate().before(now)) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("title", book.getTitle());
                    record.put("copyIndex", i);
                    record.put("studentId", copy.getStudentId());
                    record.put("issueDate", copy.getIssueDate());
                    record.put("returnDate", copy.getReturnDate());
                    overdueList.add(record);
                }
            }
        }

        return overdueList;
    }


}
