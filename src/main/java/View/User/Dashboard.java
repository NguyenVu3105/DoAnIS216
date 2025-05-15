package View.User;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import DAO.SachDao;
import DAO.TheLoaiDao;
import Model.Sach;
import Model.TheLoai;
import View.Login.Login;

public class Dashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private String fullName;
    private List<Sach> bookList;
    private List<Sach> fullBookList;
    private int currentBookIndex = 0;
    private static final int BATCH_SIZE = 20;
    private JPanel pnl_Content;
    private JTextField txt_Search;

    public Dashboard(String fullName) {
        this.fullName = fullName;
        initializeUI();
    }

    public Dashboard() {
        this.fullName = "Guest";
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Thư viện mini");
        setSize(1000, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JPanel pnl_Sidebar = createSidebar();
        contentPane.add(pnl_Sidebar, BorderLayout.WEST);

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
        contentPane.add(pnl_Main, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel pnl_Sidebar = new JPanel();
        pnl_Sidebar.setLayout(new BoxLayout(pnl_Sidebar, BoxLayout.Y_AXIS));
        pnl_Sidebar.setPreferredSize(new Dimension(220, 600));
        pnl_Sidebar.setBackground(new Color(240, 233, 222));

        JLabel lbl_Title = new JLabel("Thư viện MINI");
        lbl_Title.setFont(new Font("SansSerif", Font.BOLD, 24));
        lbl_Title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnl_Sidebar.add(lbl_Title);

        JPanel pnl_ButtonGroup = new JPanel();
        pnl_ButtonGroup.setLayout(new BoxLayout(pnl_ButtonGroup, BoxLayout.Y_AXIS));
        pnl_ButtonGroup.setBackground(new Color(240, 233, 222));
        pnl_ButtonGroup.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        String[] btnLabels = {"Trang chủ", "Lịch sử", "Phiếu phạt", "Đăng xuất"};
        for (String text : btnLabels) {
            JButton btn = new JButton(text);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            if (text.equals("Trang chủ")) {
            	btn.setBackground(Color.white);
            	btn.setOpaque(true);
            }
            else if (text.equals("Đăng xuất")) {
                btn.addActionListener(e -> {
                    dispose();
                    SwingUtilities.invokeLater(() -> new Login().setVisible(true));
                });
            }
            pnl_ButtonGroup.add(Box.createRigidArea(new Dimension(0, 10)));
            pnl_ButtonGroup.add(btn);
        }

        pnl_Sidebar.add(pnl_ButtonGroup);
        return pnl_Sidebar;
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

        JLabel lbl_UserName = new JLabel(fullName);
        lbl_UserName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl_UserName.setForeground(Color.WHITE);

        pnl_TopRow.add(btn_Avatar);
        pnl_TopRow.add(Box.createHorizontalStrut(10));
        pnl_TopRow.add(lbl_UserName);
        pnl_TopRow.add(Box.createHorizontalGlue());
        pnl_TopRow.add(btn_Notification);

        // Khởi tạo txt_Search với placeholder
        txt_Search = new JTextField("Nhập Tên Sách", 20);
        txt_Search.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txt_Search.setForeground(Color.GRAY);

        // Xử lý placeholder
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

        // DocumentListener để tìm kiếm
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

            private void search(String keyword) {
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
        });

        JButton btn_Clear = new JButton("Tìm");
        btn_Clear.setPreferredSize(new Dimension(60, 40));
        btn_Clear.addActionListener(e -> {
            txt_Search.setText("");
            txt_Search.setForeground(Color.BLACK);
            bookList = new ArrayList<>(fullBookList);
            currentBookIndex = 0;
            pnl_Content.removeAll();
            loadMoreBooks();
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
            if (count % 5 == 0) {
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
        card.setPreferredSize(new Dimension(120, 180));

        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon("pictures/" + book.getAnh());
            Image scaledImage = icon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
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
        JDialog detailDialog = new JDialog(this, "Thông tin sách", true);
        detailDialog.setSize(500, 350);
        detailDialog.setLocationRelativeTo(this);
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

    private void openFilterDialog() {
        JDialog filterDialog = new JDialog(this, "Lọc theo thể loại", true);
        filterDialog.setSize(300, 400);
        filterDialog.setLocationRelativeTo(this);
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

    private void clearFilters() {
        bookList = new ArrayList<>(fullBookList);
        currentBookIndex = 0;
        txt_Search.setText("Nhập Tên Sách");
        txt_Search.setForeground(Color.GRAY);
        pnl_Content.removeAll();
        loadMoreBooks();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Dashboard frame = new Dashboard();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}