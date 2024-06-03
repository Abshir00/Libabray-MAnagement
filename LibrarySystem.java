import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibrarySystem extends JFrame {

    private JTextField bookIDField, titleField, authorField, yearField;
    private JButton addButton, deleteButton, refreshButton;
    private JTable booksTable;
    private DefaultTableModel tableModel;

    public LibrarySystem() {
        setTitle("Library System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set up the main panel with a BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form to add new books
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel lblBookID = new JLabel("Book ID:");
        lblBookID.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblBookID, gbc);

        bookIDField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(bookIDField, gbc);
        
        JLabel lblTitle = new JLabel("Title:");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblTitle, gbc);

        titleField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);
        
        JLabel lblAuthor = new JLabel("Author:");
        lblAuthor.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblAuthor, gbc);

        authorField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(authorField, gbc);
        
        JLabel lblYear = new JLabel("Year:");
        lblYear.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(lblYear, gbc);

        yearField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(yearField, gbc);
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        addButton = new JButton("Add Book");
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        deleteButton = new JButton("Delete Book");
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        refreshButton = new JButton("Refresh List");
        refreshButton.setBackground(new Color(34, 139, 34));
        refreshButton.setForeground(Color.WHITE);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Table to display books
        tableModel = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "Year"}, 0);
        booksTable = new JTable(tableModel);
        booksTable.setFillsViewportHeight(true);
        booksTable.setFont(new Font("Arial", Font.PLAIN, 14));
        booksTable.setRowHeight(24);
        JScrollPane tableScrollPane = new JScrollPane(booksTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // Event listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshBookList();
            }
        });
    }

    private void addBook() {
        String bookID = bookIDField.getText();
        String title = titleField.getText();
        String author = authorField.getText();
        String year = yearField.getText();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Books (BookID, Title, Author, Year) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, bookID);
            statement.setString(2, title);
            statement.setString(3, author);
            statement.setString(4, year);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book added successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding book.");
        }
    }

    private void deleteBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow != -1) {
            String bookID = (String) tableModel.getValueAt(selectedRow, 0);

            try (Connection connection = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM Books WHERE BookID = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, bookID);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Book deleted successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting book.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a book to delete.");
        }
    }

    private void refreshBookList() {
        tableModel.setRowCount(0); // Clear existing data

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Books";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String bookID = resultSet.getString("BookID");
                String title = resultSet.getString("Title");
                String author = resultSet.getString("Author");
                String year = resultSet.getString("Year");
                tableModel.addRow(new Object[]{bookID, title, author, year});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving books.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LibrarySystem().setVisible(true);
            }
        });
    }
}
