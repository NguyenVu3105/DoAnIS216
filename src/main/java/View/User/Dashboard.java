package View.User;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import DAO.LichSuMuonSachDao;
import DAO.SachDao;
import DAO.TheLoaiDao;
import Model.LichSuMuonSach;
import Model.Sach;
import Model.TheLoai;
import View.Login.Login;

public class Dashboard extends JPanel {
    private static final long serialVersionUID = 1L;
    private String fullName;
    private List<Sach> bookList;
    private List<Sach> fullBookList;
    private int currentBookIndex = 0;
    private static final int BATCH_SIZE = 20;
    private JPanel pnl_Content;
    private JTextField txt_Search;
    private User parentFrame;
    private String maDocGia;

    public Dashboard(String fullName, User parentFrame, String maDocGia) {
        this.fullName = fullName;
        this.parentFrame = parentFrame;
        this.maDocGia = maDocGia;
        initializeUI();
    }

    public Dashboard(User parentFrame) {
        this.fullName = "Guest";
        this.parentFrame = parentFrame;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel pnl_Main = new JPanel(new BorderLayout());

        JPanel pnl_Header = createHeader();
        pnl_Main.add(pnl_Header, BorderLayout.NORTH);

        pnl_Content = createContentPanel();
        JScrollPane scrollPane = new JScrollPane(pnl_Content);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            JScrollBar scrollBar = (JScrollBar) e.getSource();
            int extent = scrollBar.getModel().getExtent();
            int maximum = scrollBar.getModel().getMaximum();
            int value = scrollBar.getValue();
            if (value + extent >= maximum - 10 && currentBookIndex < bookList.size()) {
                loadMoreBooks();
            }
        });

        pnl_Main.add(scrollPane, BorderLayout.CENTER);
        add(pnl_Main, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel pnl_Header = new JPanel(new BorderLayout());
        pnl_Header.setPreferredSize(new Dimension(0, 160));
        pnl_Header.setBackground(new Color(107, 142, 35));

        JPanel pnl_TopRow = new JPanel();
        pnl_TopRow.setLayout(new BoxLayout(pnl_TopRow, BoxLayout.X_AXIS));
        pnl_TopRow.setOpaque(false);
        pnl_TopRow.setBorder(BorderFactory.createEmptyBorder(10, 35, 0, 20));

        JButton btn_Avatar = createIconButton("pictures/profile.png", "👤");
        JButton btn_Notification = createIconButton("pictures/bell.png", "🔔");

        btn_Avatar.setAlignmentY(Component.CENTER_ALIGNMENT);
        btn_Avatar.addActionListener(e -> showUserInfoDialog());

        btn_Notification.setAlignmentY(Component.CENTER_ALIGNMENT);
        btn_Notification.addActionListener(e -> showNotificationDialog());

        JLabel lbl_UserName = new JLabel(fullName);
        lbl_UserName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl_UserName.setForeground(Color.WHITE);

        pnl_TopRow.add(btn_Avatar);
        pnl_TopRow.add(Box.createHorizontalStrut(10));
        pnl_TopRow.add(lbl_UserName);
        pnl_TopRow.add(Box.createHorizontalGlue());
        pnl_TopRow.add(btn_Notification);

        txt_Search = new JTextField("Nhập Tên Sách", 20);
        txt_Search.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txt_Search.setForeground(Color.GRAY);

        txt_Search.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt_Search.getText().equals("Nhập Tên Sách")) {
                    txt_Search.setText("");
                    txt_Search.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt_Search.getText().isEmpty()) {
                    txt_Search.setText("Nhập Tên Sách");
                    txt_Search.setForeground(Color.GRAY);
                }
            }
        });

        txt_Search.getDocument().addDocumentListener(new DocumentListener() {
            Timer timer = new Timer(300, null);

            {
                timer.setRepeats(false);
                timer.addActionListener(e -> {
                    String text = txt_Search.getText();
                    if (text.equals("Nhập Tên Sách")) {
                        text = "";
                    }
                    search(text);
                });
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timer.restart();
            }
        });

        JButton btn_Clear = new JButton("Xóa tìm kiếm");
        btn_Clear.setPreferredSize(new Dimension(120, 40));
        btn_Clear.addActionListener(e -> {
            txt_Search.setText("");
            txt_Search.setForeground(Color.BLACK);
            clearFilters();
        });

        JPanel pnl_SearchWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl_SearchWrapper.setOpaque(false);
        pnl_SearchWrapper.setBorder(BorderFactory.createEmptyBorder(15, 35, 10, 10));
        txt_Search.setPreferredSize(new Dimension(300, 40));
        pnl_SearchWrapper.add(txt_Search);
        pnl_SearchWrapper.add(btn_Clear);

        JButton btn_Filter = new JButton("Lọc thể loại");
        btn_Filter.setPreferredSize(new Dimension(120, 40));
        btn_Filter.setFocusable(false);
        btn_Filter.addActionListener(e -> openFilterDialog());

        pnl_SearchWrapper.add(btn_Filter);

        JButton btnClearFilter = new JButton("Hủy lọc");
        btnClearFilter.setPreferredSize(new Dimension(120, 40));
        btnClearFilter.setFocusable(false);
        btnClearFilter.addActionListener(e -> clearFilters());

        pnl_SearchWrapper.add(btnClearFilter);

        pnl_Header.add(pnl_TopRow, BorderLayout.NORTH);
        pnl_Header.add(pnl_SearchWrapper, BorderLayout.CENTER);

        return pnl_Header;
    }

    private void showNotificationDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog notificationDialog;
        if (parent instanceof Frame) {
            notificationDialog = new JDialog((Frame) parent, "Thông báo mượn sách", true);
        } else {
            notificationDialog = new JDialog((Frame) null, "Thông báo mượn sách", true);
        }
        notificationDialog.setSize(600, 400);
        notificationDialog.setLocationRelativeTo(parent);
        notificationDialog.setLayout(new BorderLayout());
        notificationDialog.setResizable(false);

        JPanel notificationPanel = createNotificationPanel();
        notificationDialog.add(notificationPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(new Color(107, 142, 35));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusable(false);
        closeButton.setPreferredSize(new Dimension(80, 35));
        closeButton.addActionListener(e -> notificationDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);

        notificationDialog.add(buttonPanel, BorderLayout.SOUTH);
        notificationDialog.setVisible(true);
    }

    private JPanel createNotificationPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    LichSuMuonSachDao lichSuDao = LichSuMuonSachDao.getInstance();
    List<LichSuMuonSach> borrowList = lichSuDao.layDanhSachTheoDK(maDocGia);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date today = new java.util.Date();
    long oneDayMillis = 24 * 60 * 60 * 1000;

    List<LichSuMuonSach> notifications = borrowList.stream()
            .filter(ls -> ls.getTrangThai().equals("Chưa trả"))
            .collect(Collectors.toList());

    if (notifications.isEmpty()) {
        JLabel lblNoNotifications = new JLabel("Không có thông báo nào về sách sắp đến hạn hoặc quá hạn.");
        lblNoNotifications.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblNoNotifications.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalGlue());
        panel.add(lblNoNotifications);
        panel.add(Box.createVerticalGlue());
    } else {
        JLabel lblHeader = new JLabel("Thông báo sách sắp đến hạn hoặc quá hạn");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblHeader);
        panel.add(Box.createVerticalStrut(10));

        JPanel headerRow = new JPanel(new GridLayout(1, 4, 5, 5));
        headerRow.setBackground(Color.WHITE);
        addHeaderLabel(headerRow, "Mã sách", true);
        addHeaderLabel(headerRow, "Ngày mượn", true);
        addHeaderLabel(headerRow, "Ngày trả", true);
        addHeaderLabel(headerRow, "Trạng thái", true);
        panel.add(headerRow);

        for (LichSuMuonSach ls : notifications) {
            JPanel row = new JPanel(new GridLayout(1, 4, 5, 5));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            addLabel(row, ls.getMaSach(), false);
            addLabel(row, ls.getNgayMuon() != null ? sdf.format(ls.getNgayMuon()) : "N/A", false);
            addLabel(row, ls.getNgayTra() != null ? sdf.format(ls.getNgayTra()) : "N/A", false);
            String status = ls.getNgayTra().getTime() < today.getTime() ? "Quá hạn" : "Sắp đến hạn";
            addLabel(row, status, false);
            panel.add(row);
            panel.add(Box.createVerticalStrut(5));
        }
    }

    return panel;
}
    
    private void addHeaderLabel(JPanel panel, String text, boolean bold) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 14));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lbl);
    }

    private void addLabel(JPanel panel, String text, boolean bold) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 12));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lbl);
    }

    private JButton createIconButton(String imagePath, String fallbackText) {
        ImageIcon icon = new ImageIcon(imagePath);
        JButton button;
        if (icon.getIconWidth() != -1) {
            Image scaled = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            button = new JButton(new ImageIcon(scaled));
        } else {
            button = new JButton(fallbackText);
        }
        button.setPreferredSize(new Dimension(60, 60));
        button.setMaximumSize(new Dimension(60, 60));
        button.setFocusable(false);
        return button;
    }

    private JPanel createContentPanel() {
        pnl_Content = new JPanel();
        pnl_Content.setBackground(Color.WHITE);
        pnl_Content.setLayout(new BoxLayout(pnl_Content, BoxLayout.Y_AXIS));
        pnl_Content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        SachDao sachDao = SachDao.getInstance();
        fullBookList = sachDao.layDanhSach();
        bookList = new ArrayList<>(fullBookList);

        loadMoreBooks();
        return pnl_Content;
    }

    private void loadMoreBooks() {
        int endIndex = Math.min(currentBookIndex + BATCH_SIZE, bookList.size());
        int count = 0;
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        rowPanel.setBackground(Color.WHITE);
        for (int i = currentBookIndex; i < endIndex; i++) {
            Sach book = bookList.get(i);
            JPanel card = createBookCard(book);
            rowPanel.add(card);
            count++;
            if (count % 4 == 0) {
                pnl_Content.add(rowPanel);
                rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
                rowPanel.setBackground(Color.WHITE);
            }
        }
        if (rowPanel.getComponentCount() > 0) {
            pnl_Content.add(rowPanel);
        }
        currentBookIndex = endIndex;
        pnl_Content.revalidate();
        pnl_Content.repaint();
    }

    private JPanel createBookCard(Sach book) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setPreferredSize(new Dimension(180, 260));

        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon("pictures/" + book.getAnh());
            Image scaledImage = icon.getImage().getScaledInstance(140, 180, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            lblImage.setText("No Image");
        }
        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblImage.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JLabel lblTitle = new JLabel(book.getTenSach());
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        lblTitle.setMaximumSize(new Dimension(160, 50));

        card.add(lblImage);
        card.add(Box.createVerticalStrut(5));
        card.add(lblTitle);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showBookDetails(book);
            }
        });

        return card;
    }

    private void showBookDetails(Sach book) {
    // Use JFrame as the parent if available, otherwise use null
    Window parent = SwingUtilities.getWindowAncestor(this);
    JDialog detailDialog;
    if (parent instanceof Frame) {
        detailDialog = new JDialog((Frame) parent, "Thông tin sách", true);
    } else {
        detailDialog = new JDialog((Frame) null, "Thông tin sách", true);
    }
    detailDialog.setSize(500, 350);
    detailDialog.setLocationRelativeTo(parent);
    detailDialog.setLayout(new BorderLayout(15, 15));
    detailDialog.getContentPane().setBackground(Color.WHITE);
    detailDialog.setResizable(false);

    JLabel imgLabel = new JLabel();
    try {
        ImageIcon icon = new ImageIcon("pictures/" + book.getAnh());
        Image img = icon.getImage().getScaledInstance(160, 220, Image.SCALE_SMOOTH);
        imgLabel.setIcon(new ImageIcon(img));
    } catch (Exception e) {
        imgLabel.setText("No Image");
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    imgLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10));

    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBackground(Color.WHITE);
    infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 15));

    addInfoLabel(infoPanel, "Tên sách:", book.getTenSach(), 18, true);
    addInfoLabel(infoPanel, "Mã sách:", book.getMaSach(), 14, false);
    addInfoLabel(infoPanel, "Nhà xuất bản:", book.getnXB(), 14, false);
    addInfoLabel(infoPanel, "Năm xuất bản:", String.valueOf(book.getNamXB()), 14, false);

    String theLoai = (book.getDsTheLoai() != null && !book.getDsTheLoai().isEmpty())
            ? book.getDsTheLoai().stream().map(TheLoai::getTenTheLoai).reduce((a, b) -> a + ", " + b).get()
            : "Không rõ";
    addInfoLabel(infoPanel, "Thể loại:", theLoai, 14, false);

    detailDialog.add(imgLabel, BorderLayout.WEST);
    detailDialog.add(infoPanel, BorderLayout.CENTER);

    JButton closeButton = new JButton("Đóng");
    closeButton.setBackground(new Color(107, 142, 35));
    closeButton.setForeground(Color.WHITE);
    closeButton.setFocusable(false);
    closeButton.setPreferredSize(new Dimension(80, 35));
    closeButton.addActionListener(e -> detailDialog.dispose());

    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.add(closeButton);

    detailDialog.add(buttonPanel, BorderLayout.SOUTH);

    detailDialog.setVisible(true);
}

    private void addInfoLabel(JPanel panel, String title, String content, int fontSize, boolean bold) {
        JLabel lbl = new JLabel("<html><b>" + title + "</b> " + content + "</html>");
        lbl.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, fontSize));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        panel.add(lbl);
    }

    public void openFilterDialog() {
    // Use JFrame as the parent if available, otherwise use null
    Window parent = SwingUtilities.getWindowAncestor(this);
    JDialog filterDialog;
    if (parent instanceof Frame) {
        filterDialog = new JDialog((Frame) parent, "Lọc theo thể loại", true);
    } else {
        filterDialog = new JDialog((Frame) null, "Lọc theo thể loại", true);
    }
    filterDialog.setSize(300, 400);
    filterDialog.setLocationRelativeTo(parent);
    filterDialog.setLayout(new BorderLayout());

    List<TheLoai> allGenres = TheLoaiDao.getInstance().layDanhSach();

    JPanel genrePanel = new JPanel();
    genrePanel.setLayout(new BoxLayout(genrePanel, BoxLayout.Y_AXIS));
    genrePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    Map<String, JCheckBox> checkBoxMap = new HashMap<>();
    for (TheLoai genre : allGenres) {
        JCheckBox cb = new JCheckBox(genre.getTenTheLoai());
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        genrePanel.add(cb);
        checkBoxMap.put(genre.getTenTheLoai(), cb);
    }

    JScrollPane scrollPane = new JScrollPane(genrePanel);
    filterDialog.add(scrollPane, BorderLayout.CENTER);

    JButton btnApply = new JButton("Áp dụng");
    btnApply.setBackground(new Color(107, 142, 35));
    btnApply.setForeground(Color.WHITE);
    btnApply.setFocusable(false);
    btnApply.setPreferredSize(new Dimension(100, 30));

    btnApply.addActionListener(e -> {
        applyGenreFilter(checkBoxMap);
        filterDialog.dispose();
    });

    JPanel btnPanel = new JPanel();
    btnPanel.add(btnApply);
    filterDialog.add(btnPanel, BorderLayout.SOUTH);

    filterDialog.setVisible(true);
}

    private void applyGenreFilter(Map<String, JCheckBox> checkBoxMap) {
        List<String> selectedGenres = checkBoxMap.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (selectedGenres.isEmpty()) {
            bookList = new ArrayList<>(fullBookList);
        } else {
            bookList = fullBookList.stream()
                    .filter(sach -> sach.getDsTheLoai() != null &&
                            sach.getDsTheLoai().stream()
                                    .map(TheLoai::getTenTheLoai)
                                    .anyMatch(selectedGenres::contains))
                    .collect(Collectors.toList());
        }

        currentBookIndex = 0;
        pnl_Content.removeAll();
        loadMoreBooks();
    }

    public void clearFilters() {
        bookList = new ArrayList<>(fullBookList);
        currentBookIndex = 0;
        txt_Search.setText("Nhập Tên Sách");
        txt_Search.setForeground(Color.GRAY);
        pnl_Content.removeAll();
        loadMoreBooks();
    }

    private void showUserInfoDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog infoDialog;
        if (parent instanceof Frame) {
            infoDialog = new JDialog((Frame) parent, "Thông tin người dùng", true);
        } else {
            infoDialog = new JDialog((Frame) null, "Thông tin người dùng", true);
        }
        infoDialog.setSize(400, 400);
        infoDialog.setLocationRelativeTo(parent);
        infoDialog.setLayout(new BorderLayout());
        infoDialog.setResizable(false);

        UserInfoPanel userInfoPanel = new UserInfoPanel(maDocGia);
        infoDialog.add(userInfoPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(new Color(107, 142, 35));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusable(false);
        closeButton.setPreferredSize(new Dimension(80, 35));
        closeButton.addActionListener(e -> infoDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);

        infoDialog.add(buttonPanel, BorderLayout.SOUTH);
        infoDialog.setVisible(true);
    }


    public void search(String keyword) {
        SwingUtilities.invokeLater(() -> {
            if (keyword.trim().isEmpty() || keyword.equals("Nhập Tên Sách")) {
                bookList = new ArrayList<>(fullBookList);
            } else {
                bookList = fullBookList.stream()
                        .filter(s -> s.getTenSach().toLowerCase().contains(keyword.toLowerCase().trim()))
                        .collect(Collectors.toList());
            }
            currentBookIndex = 0;
            pnl_Content.removeAll();
            loadMoreBooks();
        });
    }
}