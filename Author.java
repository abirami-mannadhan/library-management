package i202;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
class Author {
    private String name;
    private String bio;

    public Author(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }
}

class Book {
    private int id;
    private String title;
    private Author author;
    private int totalCopies;
    private int availableCopies;

    public Book(int id, String title, Author author, int totalCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public Author getAuthor() { return author; }
    public boolean isAvailable() { return availableCopies > 0; }

    public boolean borrowBook() {
        if (isAvailable()) {
            availableCopies--;
            return true;
        }
        return false;
    }

    public void returnBook() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    @Override
    public String toString() {
        return id + ". " + title + " by " + author.getName() +
               " | Available: " + availableCopies + "/" + totalCopies;
    }
}

class Member {
    protected int id;
    protected String name;
    protected int maxBooks = 3;
    protected int borrowedBooks = 0;

    public Member(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean canBorrow() {
        return borrowedBooks < maxBooks;
    }

    public void borrowOne() {
        borrowedBooks++;
    }

    public void returnOne() {
        borrowedBooks--;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return id + ". " + name + " (Max: " + maxBooks + ")";
    }
}

class PremiumMember extends Member {
    private int extraDays = 7;

    public PremiumMember(int id, String name) {
        super(id, name);
        this.maxBooks = 5;
    }

    public int getExtraDays() {
        return extraDays;
    }

    @Override
    public String toString() {
        return id + ". " + name + " (Premium, Max: " + maxBooks + ")";
    }
}

class BorrowTransaction {
    private Book book;
    private Member member;
    private LocalDate borrowDate;
    private LocalDate dueDate;

    public BorrowTransaction(Book book, Member member) {
        this.book = book;
        this.member = member;
        this.borrowDate = LocalDate.now();
        int borrowDays = (member instanceof PremiumMember) ? 21 : 14;
        this.dueDate = borrowDate.plusDays(borrowDays);
    }

    public Book getBook() { return book; }
    public Member getMember() { return member; }
    public LocalDate getDueDate() { return dueDate; }

    @Override
    public String toString() {
        return "Borrowed: " + book.getTitle() + " by " + member.getName() +
               "\nDue Date: " + dueDate;
    }
}

class ReturnTransaction {
    private BorrowTransaction borrowTx;
    private LocalDate returnDate;
    private double fine;

    public ReturnTransaction(BorrowTransaction borrowTx) {
        this.borrowTx = borrowTx;
        this.returnDate = LocalDate.now();
        this.fine = calculateFine();
    }

    private double calculateFine() {
        long daysLate = ChronoUnit.DAYS.between(borrowTx.getDueDate(), returnDate);
        return (daysLate > 0) ? daysLate * 10.0 : 0.0;
    }

    @Override
    public String toString() {
        return "Returned: " + borrowTx.getBook().getTitle() + " by " +
               borrowTx.getMember().getName() + "\nFine: ₹" + fine;
    }
}

class Library {
    private List<Book> books = new ArrayList<>();
    private List<Member> members = new ArrayList<>();
    private List<BorrowTransaction> borrowList = new ArrayList<>();

    private Scanner sc = new Scanner(System.in);

    public void addBook() {
        System.out.print("Enter book ID: ");
        int id = sc.nextInt(); sc.nextLine();
        System.out.print("Enter title: ");
        String title = sc.nextLine();
        System.out.print("Enter author name: ");
        String authorName = sc.nextLine();
        Author author = new Author(authorName, "");
        System.out.print("Enter total copies: ");
        int copies = sc.nextInt();

        books.add(new Book(id, title, author, copies));
        System.out.println("✅ Book added successfully!");
    }

    public void addMember() {
        System.out.print("Enter member ID: ");
        int id = sc.nextInt(); sc.nextLine();
        System.out.print("Enter name: ");
        String name = sc.nextLine();
        System.out.print("Is Premium Member? (y/n): ");
        char type = sc.next().charAt(0);

        if (type == 'y' || type == 'Y')
            members.add(new PremiumMember(id, name));
        else
            members.add(new Member(id, name));

        System.out.println("✅ Member added successfully!");
    }

    public void showAvailableBooks() {
        System.out.println("\n--- Available Books ---");
        for (Book b : books) System.out.println(b);
    }

    public void borrowBook() {
        showAvailableBooks();
        System.out.print("Enter Book ID to borrow: ");
        int bookId = sc.nextInt();
        System.out.print("Enter Member ID: ");
        int memId = sc.nextInt();

        Book book = findBook(bookId);
        Member member = findMember(memId);

        if (book == null || member == null) {
            System.out.println("❌ Invalid Book or Member ID!");
            return;
        }

        if (!book.isAvailable()) {
            System.out.println("❌ Book not available!");
            return;
        }

        if (!member.canBorrow()) {
            System.out.println("❌ Borrow limit reached!");
            return;
        }

        book.borrowBook();
        member.borrowOne();
        BorrowTransaction tx = new BorrowTransaction(book, member);
        borrowList.add(tx);
        System.out.println("\n📘 Borrow Receipt:\n" + tx);
    }

    public void returnBook() {
        System.out.print("Enter Book ID to return: ");
        int bookId = sc.nextInt();
        System.out.print("Enter Member ID: ");
        int memId = sc.nextInt();

        BorrowTransaction tx = findBorrowTx(bookId, memId);
        if (tx == null) {
            System.out.println("❌ No such borrow record!");
            return;
        }

        tx.getBook().returnBook();
        tx.getMember().returnOne();
        ReturnTransaction rtx = new ReturnTransaction(tx);
        borrowList.remove(tx);

        System.out.println("\n📗 Return Receipt:\n" + rtx);
    }

    private Book findBook(int id) {
        for (Book b : books)
            if (b.getId() == id) return b;
        return null;
    }

    private Member findMember(int id) {
        for (Member m : members)
            if (m.getId() == id) return m;
        return null;
    }

    private BorrowTransaction findBorrowTx(int bookId, int memId) {
        for (BorrowTransaction tx : borrowList)
            if (tx.getBook().getId() == bookId && tx.getMember().getId() == memId)
                return tx;
        return null;
    }
}
