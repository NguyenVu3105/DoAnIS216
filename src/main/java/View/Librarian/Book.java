package View.Librarian;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import DAO.SachDao;
import Model.Sach;
import DAO.TheLoaiDao;
import Model.TheLoai;

public class Book extends JPanel {
    private static final long serialVersionUID = 1L;
    private JPanel pnl_content;
    private String fullName; // Lưu fullName
    private List<Sach> fullBookList; // Danh sách đầy đủ sách
    private List<Sach> displayedBooks; // Danh sách sách đã hiển thị
    private JPanel pnl_center; // Để cập nhật giao diện
    private static final int BOOKS_PER_LOAD = 20; // Số sách tải mỗi lần
    private boolean isFiltering = false; //Chế độ tìm kiếm
    private JPanel pnl_Main;

    // Constructor nhận fullName
    public Book(String fullName) {
        this.fullName = fullName;
        initializeUI();
    }

    public Book() {
        this.fullName = "Guest"; // Giá trị mặc định
        initializeUI();
    }

    public JPanel getContentPane() {
    return pnl_content;
}

    private void initializeUI() {
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        //setTitle("Thư viện mini");
        //setLocationRelativeTo(null);

        pnl_content = new JPanel(new BorderLayout());
        pnl_content.setBorder(new EmptyBorder(5, 5, 5, 5));
        //setContentPane(pnl_content);

       // JPanel pnl_Sidebar = createSidebar();
        //pnl_content.add(pnl_Sidebar, BorderLayout.WEST);

        pnl_Main = createMain();
        pnl_Main.setBackground(Color.WHITE);
        pnl_content.add(pnl_Main, BorderLayout.CENTER);

        //JPanel pnl_Header = createHeader();
        //pnl_content.add(pnl_Header, BorderLayout.NORTH);
    }

    private JPanel createSidebar() {
        JPanel pnl_Sidebar = new JPanel();
        pnl_Sidebar.setLayout(new BoxLayout(pnl_Sidebar, BoxLayout.Y_AXIS));
        pnl_Sidebar.setPreferredSize(new Dimension(200, 600));
        pnl_Sidebar.setBackground(new Color(106, 85, 85));

        JButton btn_Book = new JButton("Sách");
        btn_Book.setPreferredSize(new Dimension(200, 50));
        btn_Book.setMaximumSize(new Dimension(200, 50));
        btn_Book.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn_Book.setBackground(new Color(182,162,162));
        btn_Book.setOpaque(true);
        pnl_Sidebar.add(btn_Book);

        JButton btn_User = new JButton("Độc giả");
        btn_User.setPreferredSize(new Dimension(200, 50));
        btn_User.setMaximumSize(new Dimension(200, 50));
        btn_User.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnl_Sidebar.add(btn_User);

        JButton btn_Borrow = new JButton("Mượn sách");
        btn_Borrow.setPreferredSize(new Dimension(200, 50));
        btn_Borrow.setMaximumSize(new Dimension(200, 50));
        btn_Borrow.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnl_Sidebar.add(btn_Borrow);

        JButton btn_PenaltyTicketInfo = new JButton("Thông tin phiếu phạt");
        btn_PenaltyTicketInfo.setPreferredSize(new Dimension(200, 50));
        btn_PenaltyTicketInfo.setMaximumSize(new Dimension(200, 50));
        btn_PenaltyTicketInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnl_Sidebar.add(btn_PenaltyTicketInfo);

        JButton btn_Statistics = new JButton("Thống kê");
        btn_Statistics.setPreferredSize(new Dimension(200, 50));
        btn_Statistics.setMaximumSize(new Dimension(200, 50));
        btn_Statistics.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnl_Sidebar.add(btn_Statistics);

        pnl_Sidebar.add(Box.createVerticalGlue());

        JButton btn_Logout = new JButton("Đăng xuất");
        btn_Logout.setPreferredSize(new Dimension(200, 50));
        btn_Logout.setMaximumSize(new Dimension(200, 50));
        btn_Logout.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnl_Sidebar.add(btn_Logout);
        
        // Thêm xử lý sự kiện đăng xuất
        btn_Logout.addActionListener(e -> {
            //dispose();
            SwingUtilities.invokeLater(() -> {
                View.Login.Login loginForm = new View.Login.Login();
                loginForm.setVisible(true);
            });
        });

        return pnl_Sidebar;
    }

    private JPanel createHeader() {
        JPanel pnl_Header = new JPanel();
        pnl_Header.setLayout(new BoxLayout(pnl_Header, BoxLayout.X_AXIS));
        pnl_Header.setPreferredSize(new Dimension(0, 100));
        pnl_Header.setBackground(new Color(106, 85, 85));

        ImageIcon icon_Book = new ImageIcon("Pictures/book.png");
        if (icon_Book.getIconWidth() == -1) {
            JLabel lbl_icon = new JLabel("BOOK");
            lbl_icon.setFont(new Font("Arial", Font.BOLD, 20));
            lbl_icon.setForeground(Color.WHITE);
            lbl_icon.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 60));
            pnl_Header.add(lbl_icon);
        } else {
            Image scaledImage = icon_Book.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            icon_Book = new ImageIcon(scaledImage);
            JLabel lbl_icon = new JLabel(icon_Book);
            lbl_icon.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 60));
            pnl_Header.add(lbl_icon);
        }

        JLabel lbl_Title = new JLabel("THƯ VIỆN MINI");
        lbl_Title.setFont(new Font("Arial", Font.BOLD, 24));
        lbl_Title.setForeground(Color.WHITE);
        lbl_Title.setBorder(null);
        pnl_Header.add(lbl_Title);
        pnl_Header.add(Box.createHorizontalGlue());

        JLabel lbl_LibrarianName = new JLabel("Thủ thư: " + fullName); // Hiển thị fullName
        lbl_LibrarianName.setFont(new Font("Arial", Font.PLAIN, 16));
        lbl_LibrarianName.setForeground(Color.WHITE);
        lbl_LibrarianName.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        pnl_Header.add(lbl_LibrarianName);

        Dimension size = new Dimension(50, 50);

        ImageIcon icon_Bell = new ImageIcon("Pictures/bell.png");
        JButton btn_Notification;
        if (icon_Bell.getIconWidth() != -1) {
            Image scaledImage = icon_Bell.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            icon_Bell = new ImageIcon(scaledImage);
            btn_Notification = new JButton(icon_Bell);
        } else {
            btn_Notification = new JButton("🔔");
        }
        btn_Notification.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        btn_Notification.setPreferredSize(size);
        btn_Notification.setMaximumSize(size);
        btn_Notification.setMinimumSize(size);
        pnl_Header.add(btn_Notification);

        ImageIcon icon_Profile = new ImageIcon("Pictures/profile.png");
        JButton btn_Profile;
        if (icon_Profile.getIconWidth() != -1) {
            Image scaledImage = icon_Profile.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            icon_Profile = new ImageIcon(scaledImage);
            btn_Profile = new JButton(icon_Profile);
        } else {
            btn_Profile = new JButton("👤");
        }
        btn_Profile.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        btn_Profile.setPreferredSize(size);
        btn_Profile.setMaximumSize(size);
        btn_Profile.setMinimumSize(size);
        pnl_Header.add(btn_Profile);

        return pnl_Header;
    }

    private JPanel createMain() {
        pnl_Main = new JPanel(new BorderLayout());
        isFiltering=false;
        
        JPanel pnl_top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl_top.setBackground(Color.WHITE);
        JTextField txt_search = new JTextField(25);
        txt_search.setPreferredSize(new Dimension(0, 40));
        txt_search.setMaximumSize(new Dimension(300, 40));
        txt_search.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txt_search.setForeground(Color.GRAY);
        txt_search.setText("Nhập tên sách");
        
        txt_search.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt_search.getText().trim().equals("Nhập tên sách")) {
                    txt_search.setText("");
                    txt_search.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt_search.getText().trim().isEmpty()) {
                    txt_search.setText("Nhập tên sách");
                    txt_search.setForeground(Color.GRAY);
                }
            }
        });

        
        JPopupMenu suggestionsPopup = new JPopupMenu();
        pnl_top.add(txt_search);
        pnl_top.add(suggestionsPopup);

        JList<String> suggestionList = new JList<>();
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionsPopup.setFocusable(false);
        suggestionsPopup.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        suggestionsPopup.add(new JScrollPane(suggestionList));
        
        txt_search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { showSuggestions(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { showSuggestions(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { showSuggestions(); }

            private void showSuggestions() {
                String input = txt_search.getText().trim().toLowerCase();
                if (input.isEmpty()) {
                    suggestionsPopup.setVisible(false);
                    return;
                }

                List<String> matches = new ArrayList<>();
                for (Sach sach : fullBookList) {
                    if (sach.getTenSach().toLowerCase().contains(input)) {
                        matches.add(sach.getTenSach());
                    }
                }

                if (matches.isEmpty()) {
                    suggestionsPopup.setVisible(false);
                    return;
                }

                suggestionList.setListData(matches.toArray(new String[0]));
                suggestionList.setSelectedIndex(0);

                // Vị trí popup
                suggestionsPopup.setPopupSize(txt_search.getWidth(), 150);
                suggestionsPopup.show(txt_search, 0, txt_search.getHeight());
            }
        });

        txt_search.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && suggestionsPopup.isVisible()) {
                    String selected = suggestionList.getSelectedValue();
                    if (selected != null) {
                        txt_search.setText(selected);
                        suggestionsPopup.setVisible(false);
                        updateDisplayedBooksByKeyword(selected);
                    }
                }
            }
        });
        
        JButton btn_search = new JButton("Tìm");
        btn_search.setPreferredSize(new Dimension(60, 40));
        btn_search.setMaximumSize(new Dimension(60, 40));
        btn_search.setMinimumSize(new Dimension(60, 40));
        btn_search.addActionListener(e -> {
            String keyword = txt_search.getText().trim();
            if (keyword.isEmpty() | keyword.equals("Nhập tên sách")) {
                isFiltering = false;
                pnl_center.removeAll();
                displayedBooks.clear();
                loadMoreBooks(); // Hiển thị lại danh sách mặc định
            } else {
                updateDisplayedBooksByKeyword(keyword); // Gọi hàm tìm kiếm
            }
        });
        pnl_top.add(btn_search);

        JButton btn_add = new JButton("Thêm");
        btn_add.setPreferredSize(new Dimension(80, 40));
        btn_add.setMaximumSize(new Dimension(80, 40));
        btn_add.setMinimumSize(new Dimension(80, 40));
        btn_add.addActionListener(e -> showAddBookForm());
        pnl_top.add(btn_add);
        
        pnl_center = new JPanel();
        pnl_center.setLayout(new BoxLayout(pnl_center, BoxLayout.Y_AXIS));
        pnl_center.setBackground(Color.WHITE);
        pnl_center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Khởi tạo danh sách sách
        SachDao sachDao = SachDao.getInstance();
        fullBookList = sachDao.layDanhSach();
        displayedBooks = new ArrayList<>();

        // Hiển thị 20 cuốn sách đầu tiên (nếu có)
        loadMoreBooks();

        JScrollPane scrollPane = new JScrollPane(pnl_center);
        // Thêm lắng nghe sự kiện cuộn
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                JScrollBar scrollBar = (JScrollBar) e.getSource();
                int extent = scrollBar.getModel().getExtent();
                int maximum = scrollBar.getModel().getMaximum();
                int value = scrollBar.getModel().getValue();
                // Kiểm tra nếu cuộn đến gần cuối
                if (value + extent >= maximum - 10) { // -10 để phát hiện sớm hơn
                    loadMoreBooks();
                }
            }
        });

        pnl_Main.add(pnl_top, BorderLayout.NORTH);
        pnl_Main.add(scrollPane, BorderLayout.CENTER);

        return pnl_Main;
    }

    private void loadMoreBooks() {
    if (isFiltering) return;

    int currentSize = displayedBooks.size();
    int booksToLoad = Math.min(fullBookList.size() - currentSize, BOOKS_PER_LOAD);

    if (booksToLoad > 0) {
        List<Sach> newBooks = fullBookList.subList(currentSize, currentSize + booksToLoad);
        displayedBooks.addAll(newBooks);

        JPanel rowPanel = new JPanel(new GridBagLayout());
        rowPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST; // Canh trái

        int count = 0;
        for (Sach book : newBooks) {
            gbc.gridx = count % 5;
            gbc.gridy = count / 5;

            JPanel pnl_card = createCard(book);
            rowPanel.add(pnl_card, gbc);

            count++;
        }

        pnl_center.add(rowPanel);
        pnl_center.revalidate();
        pnl_center.repaint();
    }
}

    
    private void updateDisplayedBooksByKeyword(String keyword) {
        isFiltering = true;
        displayedBooks.clear();   // Dọn danh sách hiển thị
        pnl_center.removeAll();   // Xóa toàn bộ card đang hiển thị

        JPanel rowPanel = null;
        int count = 0;

        for (Sach sach : fullBookList) {
            if (sach.getTenSach().toLowerCase().contains(keyword.toLowerCase())) {
                // Tạo hàng mới nếu bắt đầu nhóm 5 cuốn
                if (count % 5 == 0) {
                    rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));  // Canh trái
                    rowPanel.setBackground(Color.WHITE);
                    pnl_center.add(rowPanel);
                }

                JPanel pnl_card = createCard(sach);
                rowPanel.add(pnl_card);
                displayedBooks.add(sach);

                count++;
            }
        }

        pnl_center.revalidate();  // Refresh layout
        pnl_center.repaint();     // Vẽ lại giao diện
    }

    
    private JPanel createCard(Sach book) {
        JPanel pnl_card = new JPanel();
        pnl_card.setLayout(new BorderLayout());
        pnl_card.setBackground(Color.WHITE);
        pnl_card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnl_card.setPreferredSize(new Dimension(120, 180));
        pnl_card.setMaximumSize(new Dimension(120,180));
        pnl_card.setMinimumSize(new Dimension(120,180));
        
        pnl_card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showEditBookForm(book); // mở form cập nhật
            }
        });
        
        JLabel lbl_image = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("pictures/" + book.getAnh());
            Image scaledImage = icon.getImage().getScaledInstance(100, 130, Image.SCALE_SMOOTH);
            lbl_image.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            lbl_image.setText("No Image");
            lbl_image.setHorizontalAlignment(SwingConstants.CENTER);
        }
        lbl_image.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        lbl_image.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl_image.setHorizontalAlignment(SwingConstants.CENTER);
        pnl_card.add(lbl_image, BorderLayout.NORTH);

        JLabel lbl_Title = new JLabel(book.getTenSach());
        lbl_Title.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl_Title.setHorizontalAlignment(SwingConstants.CENTER);
        pnl_card.add(lbl_Title);

        return pnl_card;
    }

    private void showAddBookForm() {
        List<TheLoai> categories = TheLoaiDao.getInstance().layDanhSach();
        List<String> selectedCategories = new ArrayList<>();
        final String[] selectedImagePath = {null};

        // Khung preview ảnh (click để chọn ảnh)
        JLabel imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(120, 150));
        imagePreview.setMaximumSize(new Dimension(120,150));
        imagePreview.setMinimumSize(new Dimension(120,150));
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreview.setVerticalAlignment(SwingConstants.CENTER);
        imagePreview.setText("Chọn ảnh");

        imagePreview.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedImagePath[0] = fileChooser.getSelectedFile().getAbsolutePath();
                    ImageIcon icon = new ImageIcon(selectedImagePath[0]);
                    Image scaled = icon.getImage().getScaledInstance(
                        imagePreview.getPreferredSize().width, 
                        imagePreview.getPreferredSize().height, 
                        Image.SCALE_SMOOTH
                    );
                    imagePreview.setIcon(new ImageIcon(scaled));
                    imagePreview.setText(""); // Ẩn text "Chọn ảnh"
                    pnl_Main.revalidate(); // Cập nhật lại panel
                    pnl_Main.repaint();
                }
            }
        });


        // Panel chứa form nhập
        JPanel addBookPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblBookName = new JLabel("Tên sách");
        JTextField txtBookName = new JTextField(20);

        JLabel lblCategory = new JLabel("Thể loại");
        JTextField txtCategory = new JTextField();
        txtCategory.setEditable(false);

        JLabel lblPublisher = new JLabel("Nhà xuất bản");
        JTextField txtPublisher = new JTextField(20);

        JLabel lblYear = new JLabel("Năm xuất bản");
        JTextField txtYear = new JTextField(20);

        JLabel lblPrice = new JLabel("Giá");
        JTextField txtPrice = new JTextField(20);

        JButton btnAdd = new JButton("Thêm");
        JButton btnCancel = new JButton("Hủy");

        // ============ Bố cục nhập liệu ============
        int row = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;

        gbc.gridx = 0; gbc.gridy = row;
        addBookPanel.add(lblBookName, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        addBookPanel.add(txtBookName, gbc);

        gbc.gridy = ++row; gbc.gridx = 0; gbc.weightx = 0;
        addBookPanel.add(lblCategory, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        addBookPanel.add(txtCategory, gbc);

        gbc.gridy = ++row; gbc.gridx = 0; gbc.weightx = 0;
        addBookPanel.add(lblPublisher, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        addBookPanel.add(txtPublisher, gbc);

        gbc.gridy = ++row; gbc.gridx = 0; gbc.weightx = 0;
        addBookPanel.add(lblYear, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        addBookPanel.add(txtYear, gbc);

        gbc.gridy = ++row; gbc.gridx = 0; gbc.weightx = 0;
        addBookPanel.add(lblPrice, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        addBookPanel.add(txtPrice, gbc);

        JPanel panelBtn = new JPanel();
        panelBtn.add(btnCancel);
        panelBtn.add(btnAdd);
        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addBookPanel.add(panelBtn, gbc);

        // ============ Panel ảnh ============
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setPreferredSize(new Dimension(140, 200));
        imagePanel.add(imagePreview);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(160, 240));
        leftPanel.add(imagePanel, BorderLayout.CENTER);

        // ============ Render tổng thể ============
        pnl_Main.removeAll();
        pnl_Main.setLayout(new BorderLayout());
        pnl_Main.add(leftPanel, BorderLayout.WEST);
        pnl_Main.add(addBookPanel, BorderLayout.CENTER);
        pnl_Main.revalidate();
        pnl_Main.repaint();

        // ============ Menu đa chọn thể loại ============
        JPopupMenu popupMenu = new JPopupMenu();
        List<JCheckBoxMenuItem> categoryItems = new ArrayList<>();

        for (TheLoai tl : categories) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(tl.getTenTheLoai());
            item.addActionListener(ev -> {
                selectedCategories.clear();
                for (JCheckBoxMenuItem it : categoryItems) {
                    if (it.isSelected()) {
                        selectedCategories.add(it.getText());
                    }
                }
                txtCategory.setText(String.join(", ", selectedCategories));
            });
            categoryItems.add(item);
            popupMenu.add(item);
        }

        // Mở popup khi click vào ô txtCategory
        txtCategory.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                popupMenu.show(txtCategory, 0, txtCategory.getHeight());
            }
        });

        // ============ Hủy ============
        btnCancel.addActionListener(e -> {
            pnl_content.remove(pnl_Main);        // Gỡ panel cũ ra khỏi content
            pnl_Main = createMain();             // Tạo lại panel chính
            pnl_content.add(pnl_Main, BorderLayout.CENTER); // Thêm vào lại
            pnl_content.revalidate();
            pnl_content.repaint();
        });

        // ============ Thêm ============
        btnAdd.addActionListener(e -> {
            if (txtBookName.getText().isEmpty() || selectedCategories.isEmpty() || txtPublisher.getText().isEmpty() ||
                txtYear.getText().isEmpty() || txtPrice.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            String maSach = generateBookCode();
            Sach newBook = new Sach();
            newBook.setMaSach(maSach);
            newBook.setTenSach(txtBookName.getText());
            newBook.setnXB(txtPublisher.getText());
            newBook.setNamXB(Integer.parseInt(txtYear.getText()));
            newBook.setGia(Double.parseDouble(txtPrice.getText()));
            newBook.setAnh(maSach + ".png");

            List<TheLoai> selectedTL = new ArrayList<>();
            for (String name : selectedCategories) {
                for (TheLoai tl : categories) {
                    if (tl.getTenTheLoai().equals(name)) {
                        selectedTL.add(tl); break;
                    }
                }
            }
            newBook.setDsTheLoai(selectedTL);
            // Lưu ảnh
            if (selectedImagePath[0] != null) {
                File src = new File(selectedImagePath[0]);
                File dest = new File("Pictures/" + maSach + ".png");
                try {
                    Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            SachDao.getInstance().addCategoriesToBook(maSach, selectedTL);
            SachDao.getInstance().themDoiTuong(newBook);
            JOptionPane.showMessageDialog(null, "Thêm sách thành công!");
            pnl_content.remove(pnl_Main);        // Gỡ panel cũ ra khỏi content
            pnl_Main = createMain();             // Tạo lại panel chính
            pnl_content.add(pnl_Main, BorderLayout.CENTER); // Thêm vào lại
            pnl_content.revalidate();
            pnl_content.repaint();
        });
    }

    private String generateBookCode() {
        // Lấy mã sách mới nhất từ cơ sở dữ liệu và cộng thêm 1
        String lastBookCode = SachDao.getInstance().getLastBookCode(); // Cần viết phương thức getLastBookCode() trong SachDao
        int nextCode = Integer.parseInt(lastBookCode.substring(1)) + 1;
        return "S" + String.format("%04d", nextCode); // Đảm bảo mã sách có dạng S0000
    }
    private void showEditBookForm(Sach book) {
    List<TheLoai> categories = TheLoaiDao.getInstance().layDanhSach();
    List<String> selectedCategories = new ArrayList<>();
    final String[] selectedImagePath = {null};

    // Ảnh preview
    JLabel imagePreview = new JLabel();
    imagePreview.setPreferredSize(new Dimension(120, 160));
    imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
    imagePreview.setVerticalAlignment(SwingConstants.CENTER);
    try {
        ImageIcon icon = new ImageIcon("Pictures/" + book.getAnh());
        Image scaled = icon.getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH);
        imagePreview.setIcon(new ImageIcon(scaled));
    } catch (Exception ex) {
        imagePreview.setText("Chọn ảnh");
    }

    imagePreview.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImagePath[0] = chooser.getSelectedFile().getAbsolutePath();
                ImageIcon newIcon = new ImageIcon(selectedImagePath[0]);
                Image scaled = newIcon.getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH);
                imagePreview.setIcon(new ImageIcon(scaled));
            }
        }
    });
    
    // Panel chứa form chỉnh sửa
    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel lblBookId = new JLabel("Mã sách");
    JTextField txtBookId = new JTextField(book.getMaSach());
    txtBookId.setEditable(false);
    
    JLabel lblBookName = new JLabel("Tên sách");
    JTextField txtBookName = new JTextField(book.getTenSach(),20);

    JLabel lblCategory = new JLabel("Thể loại");
    JTextField txtCategory = new JTextField();
    txtCategory.setEditable(false);

    JLabel lblPublisher = new JLabel("Nhà xuất bản");
    JTextField txtPublisher = new JTextField(book.getnXB(),20);

    JLabel lblYear = new JLabel("Năm xuất bản");
    JTextField txtYear = new JTextField(String.valueOf(book.getNamXB()),20);

    JLabel lblPrice = new JLabel("Giá");
    JTextField txtPrice = new JTextField(String.valueOf(book.getGia()),20);


    // ============ Bố cục chỉnh sửa ============
    int row = 0;
    gbc.weightx = 0;
    gbc.anchor = GridBagConstraints.EAST;

    gbc.gridx = 0; gbc.gridy = row;
    formPanel.add(lblBookId, gbc);
    gbc.gridx = 1; gbc.weightx = 1.0;
    formPanel.add(txtBookId, gbc);
    row++;
    
    gbc.gridx = 0; gbc.gridy = row;
    formPanel.add(lblBookName, gbc);
    gbc.gridx = 1; gbc.weightx = 1.0;
    formPanel.add(txtBookName, gbc);

    gbc.gridy = ++row; gbc.gridx = 0; gbc.weightx = 0;
    formPanel.add(lblCategory, gbc);
    gbc.gridx = 1; gbc.weightx = 1.0;
    formPanel.add(txtCategory, gbc);

    gbc.gridy = ++row; gbc.gridx = 0; gbc.weightx = 0;
    formPanel.add(lblPublisher, gbc);
    gbc.gridx = 1; gbc.weightx = 1.0;
    formPanel.add(txtPublisher, gbc);

    gbc.gridy = ++row; gbc.gridx = 0; gbc.weightx = 0;
    formPanel.add(lblYear, gbc);
    gbc.gridx = 1; gbc.weightx = 1.0;
    formPanel.add(txtYear, gbc);

    gbc.gridy = ++row; gbc.gridx = 0; gbc.weightx = 0;
    formPanel.add(lblPrice, gbc);
    gbc.gridx = 1; gbc.weightx = 1.0;
    formPanel.add(txtPrice, gbc);


    // ============ Panel ảnh ============
    JPanel imagePanel = new JPanel(new GridBagLayout());
    imagePanel.setPreferredSize(new Dimension(140, 200));
    imagePanel.add(imagePreview);

    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.setPreferredSize(new Dimension(160, 240));
    leftPanel.add(imagePanel, BorderLayout.CENTER);

    // ============ Render tổng thể ============
    pnl_Main.removeAll();
    pnl_Main.setLayout(new BorderLayout());
    pnl_Main.add(leftPanel, BorderLayout.WEST);
    pnl_Main.add(formPanel, BorderLayout.CENTER);
    pnl_Main.revalidate();
    pnl_Main.repaint();
    
    // Lưu thể loại hiện tại
    for (TheLoai tl : book.getDsTheLoai()) {
        selectedCategories.add(tl.getTenTheLoai());
    }
    txtCategory.setText(String.join(", ", selectedCategories));

    // Tạo menu thể loại
    JPopupMenu popupMenu = new JPopupMenu();
    List<JCheckBoxMenuItem> categoryItems = new ArrayList<>();
    for (TheLoai tl : categories) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(tl.getTenTheLoai());
        if (selectedCategories.contains(tl.getTenTheLoai())) item.setSelected(true);
        item.addActionListener(ev -> {
            selectedCategories.clear();
            for (JCheckBoxMenuItem it : categoryItems) {
                if (it.isSelected()) {
                    selectedCategories.add(it.getText());
                }
            }
            txtCategory.setText(String.join(", ", selectedCategories));
        });
        categoryItems.add(item);
        popupMenu.add(item);
    }
    txtCategory.addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            popupMenu.show(txtCategory, 0, txtCategory.getHeight());
        }
    });

    // Nút
    JButton btnUpdate = new JButton("Cập nhật");
    JButton btnDelete = new JButton("Xóa");
    JButton btnCancel = new JButton("Hủy");
    JPanel panelBtn = new JPanel();
    panelBtn.add(btnUpdate);
    panelBtn.add(btnDelete);
    panelBtn.add(btnCancel);
    gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
    formPanel.add(panelBtn, gbc);


    // ====== Xử lý nút ======
    btnCancel.addActionListener(e -> {
        pnl_content.remove(pnl_Main);
        pnl_Main = createMain();
        pnl_content.add(pnl_Main, BorderLayout.CENTER);
        pnl_content.revalidate();
        pnl_content.repaint();
    });

    btnUpdate.addActionListener(e -> {
        if (txtBookName.getText().isEmpty() ||selectedCategories.isEmpty() || txtPublisher.getText().isEmpty() ||
            txtYear.getText().isEmpty() || txtPrice.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        book.setTenSach(txtBookName.getText());
        book.setnXB(txtPublisher.getText());
        book.setNamXB(Integer.parseInt(txtYear.getText()));
        book.setGia(Double.parseDouble(txtPrice.getText()));
        book.setAnh(book.getAnh());

        List<TheLoai> selectedTL = new ArrayList<>();
        for (String name : selectedCategories) {
            for (TheLoai tl : categories) {
                if (tl.getTenTheLoai().equals(name)) {
                    selectedTL.add(tl); break;
                }
            }
        }
        book.setDsTheLoai(selectedTL);

        if (selectedImagePath[0] != null) {
            try {
                Files.copy(new File(selectedImagePath[0]).toPath(),
                        new File("Pictures/" + book.getAnh()).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        SachDao.getInstance().updateBookAndCategories(book);
        JOptionPane.showMessageDialog(null, "Cập nhật thành công!");

        pnl_content.remove(pnl_Main);     
        pnl_Main = createMain();            
        pnl_content.add(pnl_Main, BorderLayout.CENTER);
        pnl_content.revalidate();
        pnl_content.repaint();
    });

    btnDelete.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa sách này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int ketQua=SachDao.getInstance().xoaDoiTuong(book);
            if(ketQua!=0) {
            	JOptionPane.showMessageDialog(null, "Đã xóa sách.");
	            pnl_content.remove(pnl_Main);
	            pnl_Main = createMain();
	            pnl_content.add(pnl_Main, BorderLayout.CENTER);
	            pnl_content.revalidate();
	            pnl_content.repaint();
            }
            else JOptionPane.showMessageDialog(null, "Sách đang được mượn không thể xóa");        }
    });
}

    /* 
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Book frame = new Book();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    */
}