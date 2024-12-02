    import javax.swing.*;
    import javax.swing.table.*;
    import java.awt.*;
    import java.awt.event.*;
    import java.util.ArrayList;
    import java.util.List;
    
    class User {
        private String id;
        private String name;
        private String code;
        private String grade;
        private String password;
        private String role;
    
        public User(String id, String name, String code, String grade, String password, String role) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.grade = grade;
            this.password = password;
            this.role = role;
        }
    
        public String getId() { return id; }
        public String getName() { return name; }
        public String getCode() { return code; }
        public String getGrade() { return grade; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
    
        public void setName(String name) { this.name = name; }
        public void setCode(String code) { this.code = code; }
        public void setGrade(String grade) { this.grade = grade; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public class UserManagementApp {
        private static List<User> users = new ArrayList<>();
        private static JFrame frame;
        private static User loggedInUser;
    
        public static void main(String[] args) {
            users.add(new User("1", "Géza", "A100", "5", "password123", "Diák"));
            users.add(new User("2", "Réka", "B200", "4", "pass456", "Diák"));
            users.add(new User("3", "admin", "admin", "-", "admin", "Tanár"));
    
            // Bejelentkezési ablak megjelenítése
            SwingUtilities.invokeLater(UserManagementApp::createLoginScreen);
        }
    
        // Ez teszi középre az ablakot
        private static void centerWindow(JFrame frame) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screenSize.width - frame.getWidth()) / 2;
            int y = (screenSize.height - frame.getHeight()) / 2;
            frame.setLocation(x, y);
        }
    
        // Bejelentkező képernyő létrehozása
        private static void createLoginScreen() {
            frame = new JFrame("Felhasználói bejelentkezés");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 250);
            centerWindow(frame);
    
            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20)); // Add margins
    
            JLabel userLabel = new JLabel("Kód:");
            JLabel passLabel = new JLabel("Jelszó:");
            JTextField userField = new JTextField();
            JPasswordField passField = new JPasswordField();
            JButton loginButton = new JButton("Bejelentkezés");
            JButton hintButton = new JButton("Emlékeztető(Admin)");
    
            panel.add(userLabel);
            panel.add(userField);
            panel.add(passLabel);
            panel.add(passField);
            panel.add(new JLabel());
            panel.add(loginButton);
            panel.add(new JLabel());
            panel.add(hintButton);
    
            frame.getContentPane().add(panel);
            frame.setVisible(true);
    
            // Ez ami megoldja a tabulátor és enter gombok működését
            userField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) passField.requestFocus();
                }
            });
            passField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) userField.requestFocus();
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) loginButton.doClick();
                }
            });
    
            // Enter gombra a bejelentkezés
            loginButton.addActionListener(e -> {
                String code = userField.getText();
                String password = new String(passField.getPassword());
                User user = authenticate(code, password);
                if (user != null) {
                    loggedInUser = user;
                    JOptionPane.showMessageDialog(frame, "Sikeres bejelentkezés!", "Siker", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    if (user.getRole().equals("Tanár")) {
                        createTeacherDashboard();
                    } else {
                        createStudentDashboard();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Hibás belépési adatok!", "Hiba", JOptionPane.ERROR_MESSAGE);
                }
            });
    
            //Admin bejelentkezési adatok megjelenítése
            hintButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(frame, "Admin bejelentkezés:\nKód: admin\nJelszó: admin", "Emlékeztető", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    
        // Felhasználó azonosítása
        private static User authenticate(String code, String password) {
            return users.stream()
                    .filter(user -> user.getCode().equals(code) && user.getPassword().equals(password))
                    .findFirst()
                    .orElse(null);
        }
    
        // Hallgatói felület létrehozása
        private static void createStudentDashboard() {
            frame = new JFrame("Hallgatói felület");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            centerWindow(frame);
    
            String[] columnNames = {"ID", "Név", "Kód", "Jegy"};
            String[][] data = users.stream()
                    .map(user -> new String[]{user.getId(), user.getName(), user.getCode(), user.getGrade()})
                    .toArray(String[][]::new);
    
            JTable userTable = new JTable(new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
    
            adjustColumnSizes(userTable);
    
            JScrollPane scrollPane = new JScrollPane(userTable);
    
            JButton logoutButton = new JButton("Kijelentkezés");
    
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(logoutButton, BorderLayout.SOUTH);
    
            frame.getContentPane().add(panel);
            frame.setVisible(true);
    
            // Kijelentkezés gomb működése
            logoutButton.addActionListener(e -> {
                frame.dispose();
                loggedInUser = null;
                createLoginScreen();
            });
        }
    
        // Tanári felület létrehozása
        private static void createTeacherDashboard() {
            frame = new JFrame("Tanár felület");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);
            centerWindow(frame);
    
            String[] columnNames = {"ID", "Név", "Kód", "Jegy", "Jelszó"};
            String[][] data = users.stream()
                    .map(user -> new String[]{user.getId(), user.getName(), user.getCode(), user.getGrade(), user.getPassword()})
                    .toArray(String[][]::new);
    
            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
            JTable userTable = new JTable(tableModel) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column != 0; // Prevent editing of the ID column
                }
            };
    
            userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Single row selection
            adjustColumnSizes(userTable);
    
            JScrollPane scrollPane = new JScrollPane(userTable);
    
            JButton saveButton = new JButton("Változtatások mentése");
            JButton addUserButton = new JButton("Felhasználó hozzáadása");
            JButton logoutButton = new JButton("Kijelentkezés");
    
            JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
            buttonPanel.add(saveButton);
            buttonPanel.add(addUserButton);
            buttonPanel.add(logoutButton);
    
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
    
            frame.getContentPane().add(panel);
            frame.setVisible(true);
    
            // Kijelentkezési gomb működése
            logoutButton.addActionListener(e -> {
                frame.dispose();
                loggedInUser = null;
                createLoginScreen();
            });
    
            // Mentés gomb működése
            saveButton.addActionListener(e -> {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String id = (String) tableModel.getValueAt(i, 0);
                    String name = (String) tableModel.getValueAt(i, 1);
                    String code = (String) tableModel.getValueAt(i, 2);
                    String grade = (String) tableModel.getValueAt(i, 3);
                    String password = (String) tableModel.getValueAt(i, 4);
    
                    // Update user data
                    for (User user : users) {
                        if (user.getId().equals(id)) {
                            user.setName(name);
                            user.setCode(code);
                            user.setGrade(grade);
                            user.setPassword(password);
                        }
                    }
                }
                JOptionPane.showMessageDialog(frame, "Változtatások mentve!", "Mentve", JOptionPane.INFORMATION_MESSAGE);
            });
    
            // Hozzáadás gomb működése
            addUserButton.addActionListener(e -> {
                JDialog addUserDialog = new JDialog(frame, "Új felhasználó hozzáadása", true);
                addUserDialog.setSize(400, 300);
                addUserDialog.setLocationRelativeTo(null);
    
                JPanel addUserPanel = new JPanel(new GridLayout(6, 2, 10, 10));
                addUserPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add margins
    
                JTextField idField = new JTextField();
                JTextField nameField = new JTextField();
                JTextField codeField = new JTextField();
                JTextField gradeField = new JTextField();
                JTextField passwordField = new JTextField();
                JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Diák", "Tanár"});
    
                addUserPanel.add(new JLabel("ID:"));
                addUserPanel.add(idField);
                addUserPanel.add(new JLabel("Név:"));
                addUserPanel.add(nameField);
                addUserPanel.add(new JLabel("Kód:"));
                addUserPanel.add(codeField);
                addUserPanel.add(new JLabel("Jegy:"));
                addUserPanel.add(gradeField);
                addUserPanel.add(new JLabel("Jelszó:"));
                addUserPanel.add(passwordField);
                addUserPanel.add(new JLabel("Státusz:"));
                addUserPanel.add(roleComboBox);
    
                JButton submitButton = new JButton("Mentés");
                JButton cancelButton = new JButton("Mégse");
    
                JPanel buttonPanelDialog = new JPanel(new GridLayout(1, 2, 10, 10));
                buttonPanelDialog.add(submitButton);
                buttonPanelDialog.add(cancelButton);
    
                addUserDialog.setLayout(new BorderLayout());
                addUserDialog.add(addUserPanel, BorderLayout.CENTER);
                addUserDialog.add(buttonPanelDialog, BorderLayout.SOUTH);
    
                // Mentés Gomb Működése
                submitButton.addActionListener(submitEvent -> {
                    String id = idField.getText().trim();
                    String name = nameField.getText().trim();
                    String code = codeField.getText().trim();
                    String grade = gradeField.getText().trim();
                    String password = passwordField.getText().trim();
                    String role = (String) roleComboBox.getSelectedItem();
    
                    if (id.isEmpty() || name.isEmpty() || code.isEmpty() || grade.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(addUserDialog, "Az összes mező legyen kitöltve!", "Hiba", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
    
                    // Felhasználó hozzáadása
                    User newUser = new User(id, name, code, grade, password, role);
                    users.add(newUser);
                    tableModel.addRow(new String[]{id, name, code, grade, password});
                    addUserDialog.dispose();
                    JOptionPane.showMessageDialog(frame, "Új felhasználó sikeresen létrehozva!", "Siker", JOptionPane.INFORMATION_MESSAGE);
                });
    
                // Mégse gomb működése
                cancelButton.addActionListener(cancelEvent -> addUserDialog.dispose());
    
                addUserDialog.setVisible(true);
            });
    
            // Sor törélese a táblázatból a DELETE gombbal
            userTable.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                        int selectedRow = userTable.getSelectedRow();
                        if (selectedRow != -1) {
                            String idToRemove = (String) tableModel.getValueAt(selectedRow, 0);
                            User userToRemove = users.stream()
                                    .filter(user -> user.getId().equals(idToRemove))
                                    .findFirst()
                                    .orElse(null);
    
                            if (userToRemove != null && userToRemove.getName().equals("admin")) {
                                JOptionPane.showMessageDialog(frame, "Az Adminisztrátort nem lehet törölni!", "Hiba", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
    
                            int confirmation = JOptionPane.showConfirmDialog(
                                    frame,
                                    "Biztos ki akarod törölni ezt a felhasználót?",
                                    "Törlés megerősítése",
                                    JOptionPane.YES_NO_OPTION
                            );
    
                            if (confirmation == JOptionPane.YES_OPTION) {
                                // listából törlés
                                users.removeIf(user -> user.getId().equals(idToRemove));
                                // táblázatból törlés
                                tableModel.removeRow(selectedRow);
                                JOptionPane.showMessageDialog(frame, "Felhasználó sikeresen törölve!", "Siker", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Nincs sor kiválasztva!", "Hiba", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        }
    
        // Az oszlopok méretének beállítása
        private static void adjustColumnSizes(JTable table) {
            TableColumnModel columnModel = table.getColumnModel();
            for (int column = 0; column < table.getColumnCount(); column++) {
                int width = 50; // Minimum
                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer renderer = table.getCellRenderer(row, column);
                    Component comp = table.prepareRenderer(renderer, row, column);
                    width = Math.max(comp.getPreferredSize().width + 10, width);
                }
                columnModel.getColumn(column).setPreferredWidth(width);
            }
        }
    }