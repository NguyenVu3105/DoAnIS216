package View.Librarian;

import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Controller.PdfController;
import DAO.DocGiaDao;
import DAO.LichSuMuonSachDao;
import DAO.ThuThuDao;
import Model.DocGia;
import Model.LichSuMuonSach;
import Model.ThongTinThongKe;
import Model.ThuThu;
import Session.LoginSession;
import View.Login.Login;

public class Librarian extends JFrame {
    private String fullName;
    private JPanel mainPanel;
    private String maThuThu;
    private DefaultTableModel tableModel;
    private JTable table;
    private JPanel pnl_cards;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private PdfController pdfExportController = new PdfController();
    private DecimalFormat currencyFormat;

    private int userCurrentPage = 1;
    private int userPageSize = 20;
    private int userTotalRecords = 0;
    private List<DocGia> docGiaList = new ArrayList<>();

    private JLabel lbl_pageInfo;
    private JButton user_btn_previous;
    private JButton user_btn_next;

    // Regex patterns for user validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern USERNAME_PASSWORD_PATTERN = Pattern.compile("^.{8,}$");

    public Librarian(String fullName) {
        this.fullName = fullName;
        this.maThuThu = getCurrentLibrarianCode();
        this.mainPanel = createMainPanel();
    }

    public Librarian() {
        this.fullName = "Admin";
        this.maThuThu = "UNKNOWN";
        this.mainPanel = createMainPanel();
    }

    private String getCurrentLibrarianCode() {
        String fullName = LoginSession.getInstance().getFullName();
        List<ThuThu> librarians = ThuThuDao.getInstance().layDanhSachTheoDK(fullName);
        if (!librarians.isEmpty()) {
            return librarians.get(0).getMaNguoiDung();
        }
        return "UNKNOWN";
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel pnl_Sidebar = createSidebar();
        mainPanel.add(pnl_Sidebar, BorderLayout.WEST);

        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                return 0;
            }
            @Override
            protected int calculateTabAreaWidth(int tabPlacement, int vertRunCount, int maxTabWidth) {
                return 0;
            }
        });

        tabbedPane.addTab("Sách", createBookPanel());
        tabbedPane.addTab("Độc giả", createUserPanel());
        tabbedPane.addTab("Mượn sách", createBorrowPanel());
        tabbedPane.addTab("Thông tin phiếu phạt", createPenaltyTicketInfoPanel());
        tabbedPane.addTab("Thống kê", createStatisticsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JPanel pnl_Header = createHeader();
        mainPanel.add(pnl_Header, BorderLayout.NORTH);

        return mainPanel;
    }

    private JPanel createSidebar() {
        JPanel pnl_Sidebar = new JPanel();
        pnl_Sidebar.setLayout(new BoxLayout(pnl_Sidebar, BoxLayout.Y_AXIS));
        pnl_Sidebar.setPreferredSize(new Dimension(200, 600));
        pnl_Sidebar.setBackground(new Color(106, 85, 85));
    
        JButton[] buttons = new JButton[] {
            new JButton("Sách"),
            new JButton("Độc giả"),
            new JButton("Mượn sách"),
            new JButton("Thông tin phiếu phạt"),
            new JButton("Thống kê"),
            new JButton("Đăng xuất")
        };
    
        for (int i = 0; i < buttons.length; i++) {
            JButton btn = buttons[i];
            btn.setPreferredSize(new Dimension(200, 50));
            btn.setMaximumSize(new Dimension(200, 50));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            btn.setOpaque(true);
            if (i < 5) {
                final int tabIndex = i;
                btn.addActionListener(e -> tabbedPane.setSelectedIndex(tabIndex));
            }
            if (i == 0) {
                btn.setBackground(new Color(182, 162, 162));
            }
            pnl_Sidebar.add(btn);
            if (i == 4) {
                pnl_Sidebar.add(Box.createVerticalGlue());
            }
        }
    
        buttons[5].addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION);
    
            if (confirm == JOptionPane.YES_OPTION) {
                LoginSession.getInstance().logout();
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(pnl_Sidebar);
                if (frame != null) {
                    frame.setVisible(false);
                    frame.dispose();
                }
                SwingUtilities.invokeLater(() -> {
                    Login loginForm = new Login();
                    loginForm.setVisible(true);
                });
            }
        });
    
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            for (int i = 0; i < 5; i++) {
                buttons[i].setBackground(i == selectedIndex ? new Color(182, 162, 162) : null);
            }
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

        JLabel lbl_LibrarianName = new JLabel("Thủ thư: " + fullName);
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
        btn_Notification.addActionListener(e -> showOverdueBorrowersDialog());
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
        btn_Profile.setMinimumSize(size);
        btn_Profile.setMaximumSize(size);
        btn_Profile.addActionListener(e -> showLibrarianInfoDialog());
        pnl_Header.add(btn_Profile);

        return pnl_Header;
    }

    private void showOverdueBorrowersDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (parent instanceof Frame) {
            dialog = new JDialog((Frame) parent, "Danh sách mượn sách quá hạn", true);
        } else {
            dialog = new JDialog((Frame) null, "Danh sách mượn sách quá hạn", true);
        }
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        String[] columnNames = {"Mã Độc Giả", "Tên Độc Giả", "Mã Sách", "Ngày Mượn", "Ngày Trả"};
        DefaultTableModel overdueTableModel = new DefaultTableModel(columnNames, 0);
        JTable overdueTable = new JTable(overdueTableModel);
        JScrollPane scrollPane = new JScrollPane(overdueTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadOverdueBorrowersData(overdueTableModel);

        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(new Color(139, 69, 69));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusable(false);
        closeButton.setPreferredSize(new Dimension(80, 35));
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadOverdueBorrowersData(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try {
            LichSuMuonSachDao lichSuDao = LichSuMuonSachDao.getInstance();
            DocGiaDao docGiaDao = DocGiaDao.getInstance();
            List<LichSuMuonSach> overdueList = lichSuDao.layDanhSach();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date currentDate = lichSuDao.getCurrentDate();

            for (LichSuMuonSach ls : overdueList) {
                if ("Chưa trả".equals(ls.getTrangThai()) && ls.getNgayTra() != null && ls.getNgayTra().before(currentDate)) {
                    String tenNguoiDung = docGiaDao.getTenNguoiDungByMaNguoiDung(ls.getMaDocGia());
                    if (tenNguoiDung == null) {
                        tenNguoiDung = "Unknown";
                    }
                    Object[] row = {
                        ls.getMaDocGia(),
                        tenNguoiDung,
                        ls.getMaSach(),
                        sdf.format(ls.getNgayMuon()),
                        sdf.format(ls.getNgayTra())
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách mượn quá hạn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createBookPanel() {
        Book bookPanel = new Book(fullName);
        return bookPanel.getContentPane();
    }

    private JPanel createUserPanel() {
        JPanel pnl_Main = new JPanel(new BorderLayout());
        String[] columnNames = {"Mã Người Dùng", "Tên Người Dùng", "Tài Khoản", "Email", "Số Điện Thoại", "Ngày Tạo"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        JPanel pnl_top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl_top.setBackground(Color.WHITE);

        JTextField txt_search = new JTextField("Tìm mã độc giả, tên, tài khoản...", 25);
        txt_search.setPreferredSize(new Dimension(0, 40));
        txt_search.setMaximumSize(new Dimension(300, 40));
        txt_search.setFont(new Font("Arial", Font.PLAIN, 14));
        txt_search.setForeground(Color.GRAY);

        txt_search.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt_search.getText().equals("Tìm mã độc giả, tên, tài khoản...")) {
                    txt_search.setText("");
                    txt_search.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt_search.getText().isEmpty()) {
                    txt_search.setText("Tìm mã độc giả, tên, tài khoản...");
                    txt_search.setForeground(Color.GRAY);
                }
            }
        });

        pnl_top.add(txt_search);

        JButton btn_search = new JButton("Tìm Kiếm");
        btn_search.setPreferredSize(new Dimension(120, 40)); // Extended width to 120
        btn_search.setMaximumSize(new Dimension(120, 40));   // Extended width to 120
        btn_search.setMinimumSize(new Dimension(120, 40));   // Extended width to 120
        btn_search.setFont(new Font("Arial", Font.BOLD, 14));
        btn_search.addActionListener(e -> {
            userCurrentPage = 1;
            String keyword = txt_search.getText().trim();
            if (keyword.equals("Tìm mã độc giả, tên, tài khoản...")) {
                keyword = "";
            }
            loadTableData(keyword);
        });
        pnl_top.add(btn_search);

        JButton btn_add = new JButton("Thêm");
        btn_add.setPreferredSize(new Dimension(80, 40));
        btn_add.setMaximumSize(new Dimension(80, 40));
        btn_add.setMinimumSize(new Dimension(80, 40));
        btn_add.addActionListener(e -> {
            JTextField tenNguoiDungField = new JTextField(10);
            JTextField taiKhoanField = new JTextField(10);
            JTextField matKhauField = new JTextField(10);
            JTextField emailField = new JTextField(10);
            JTextField soDienThoaiField = new JTextField(10);

            JPanel panel = new JPanel(new GridLayout(5, 2));
            panel.add(new JLabel("Tên Người Dùng:"));
            panel.add(tenNguoiDungField);
            panel.add(new JLabel("Tài Khoản:"));
            panel.add(taiKhoanField);
            panel.add(new JLabel("Mật Khẩu:"));
            panel.add(matKhauField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Số Điện Thoại:"));
            panel.add(soDienThoaiField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Thêm Độc Giả", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String tenNguoiDung = tenNguoiDungField.getText().trim();
                String taiKhoan = taiKhoanField.getText().trim();
                String matKhauGoc = matKhauField.getText().trim();
                String email = emailField.getText().trim();
                String soDienThoai = soDienThoaiField.getText().trim();

                if (tenNguoiDung.isEmpty() || taiKhoan.isEmpty() || matKhauGoc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên, tài khoản và mật khẩu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!USERNAME_PASSWORD_PATTERN.matcher(taiKhoan).matches()) {
                    JOptionPane.showMessageDialog(this, "Tài khoản phải có ít nhất 8 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!USERNAME_PASSWORD_PATTERN.matcher(matKhauGoc).matches()) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 8 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
                    JOptionPane.showMessageDialog(this, "Email phải có định dạng hợp lệ (ví dụ: user@gmail.com)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!soDienThoai.isEmpty() && !PHONE_PATTERN.matcher(soDienThoai).matches()) {
                    JOptionPane.showMessageDialog(this, "Số điện thoại phải có đúng 10 chữ số (ví dụ: 0123456789)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    DocGiaDao docGiaDao = DocGiaDao.getInstance();
                    if (docGiaDao == null) {
                        throw new Exception("DocGiaDao instance is null");
                    }

                    if (docGiaDao.kiemTraTaiKhoanTonTai(taiKhoan)) {
                        JOptionPane.showMessageDialog(this, "Tài khoản '" + taiKhoan + "' đã tồn tại! Vui lòng chọn tài khoản khác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String maNguoiDung = docGiaDao.layMaNguoiDungMoiNhat();
                    if (maNguoiDung == null) {
                        throw new Exception("Cannot generate new maNguoiDung");
                    }
                    java.util.Date ngayTao = new java.util.Date();
                    String matKhauHash = hashSHA1(matKhauGoc);

                    DocGia docGia = new DocGia(maNguoiDung, tenNguoiDung, taiKhoan, matKhauHash, email, soDienThoai, ngayTao);
                    int rowsAffected = docGiaDao.themDoiTuong(docGia);
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Thêm độc giả thành công!");
                        String keyword = txt_search.getText().trim();
                        if (keyword.equals("Tìm mã độc giả, tên, tài khoản...")) {
                            keyword = "";
                        }
                        loadTableData(keyword);
                    } else {
                        JOptionPane.showMessageDialog(this, "Thêm độc giả thất bại!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi thêm độc giả: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pnl_top.add(btn_add);

        JButton btn_delete = new JButton("Xóa");
        btn_delete.setPreferredSize(new Dimension(80, 40));
        btn_delete.setMaximumSize(new Dimension(80, 40));
        btn_delete.setMinimumSize(new Dimension(80, 40));
        btn_delete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String maNguoiDung = (String) tableModel.getValueAt(selectedRow, 0);
                String tenNguoiDung = (String) tableModel.getValueAt(selectedRow, 1);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa độc giả '" + tenNguoiDung + "' (Mã: " + maNguoiDung + ") không?\n" +
                        "Các thông tin liên quan đến độc giả này sẽ được xóa toàn bộ.",
                        "Xác nhận xóa độc giả",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        DocGiaDao docGiaDao = DocGiaDao.getInstance();
                        if (docGiaDao == null) {
                            throw new Exception("DocGiaDao instance is null");
                        }
                        DocGia tmp = new DocGia();
                        tmp.setMaNguoiDung(maNguoiDung);
                        int rowsAffected = docGiaDao.xoaDoiTuong(tmp);
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Xóa độc giả thành công!");
                            String keyword = txt_search.getText().trim();
                            if (keyword.equals("Tìm mã độc giả, tên, tài khoản...")) {
                                keyword = "";
                            }
                            int totalPages = (int) Math.ceil((double) userTotalRecords / userPageSize);
                            if (userCurrentPage > totalPages && totalPages > 0) {
                                userCurrentPage = totalPages;
                            }
                            loadTableData(keyword);
                        } else {
                            JOptionPane.showMessageDialog(this, "Xóa độc giả thất bại! Không tìm thấy độc giả hoặc lỗi cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi xóa độc giả: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một độc giả để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        pnl_top.add(btn_delete);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnl_pagination = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnl_pagination.setBackground(Color.WHITE);

        JLabel lbl_pageSize = new JLabel("Số dòng/trang:");
        pnl_pagination.add(lbl_pageSize);

        Integer[] pageSizes = {20, 30, 40};
        JComboBox<Integer> cmb_pageSize = new JComboBox<>(pageSizes);
        cmb_pageSize.setSelectedItem(20);
        cmb_pageSize.setPreferredSize(new Dimension(60, 30));
        cmb_pageSize.addActionListener(e -> {
            userPageSize = (Integer) cmb_pageSize.getSelectedItem();
            userCurrentPage = 1;
            String keyword = txt_search.getText().trim();
            if (keyword.equals("Tìm mã độc giả, tên, tài khoản...")) {
                keyword = "";
            }
            loadTableData(keyword);
        });
        pnl_pagination.add(cmb_pageSize);

        user_btn_previous = new JButton("Previous");
        user_btn_previous.setPreferredSize(new Dimension(100, 30));
        user_btn_previous.addActionListener(e -> {
            if (userCurrentPage > 1) {
                userCurrentPage--;
                String keyword = txt_search.getText().trim();
                if (keyword.equals("Tìm mã độc giả, tên, tài khoản...")) {
                    keyword = "";
                }
                loadTableData(keyword);
            }
        });
        pnl_pagination.add(user_btn_previous);

        lbl_pageInfo = new JLabel("Trang 1/1");
        lbl_pageInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        pnl_pagination.add(lbl_pageInfo);

        user_btn_next = new JButton("Next");
        user_btn_next.setPreferredSize(new Dimension(100, 30));
        user_btn_next.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) userTotalRecords / userPageSize);
            if (userCurrentPage < totalPages) {
                userCurrentPage++;
                String keyword = txt_search.getText().trim();
                if (keyword.equals("Tìm mã độc giả, tên, tài khoản...")) {
                    keyword = "";
                }
                loadTableData(keyword);
            }
        });
        pnl_pagination.add(user_btn_next);

        pnl_Main.add(pnl_top, BorderLayout.NORTH);
        pnl_Main.add(scrollPane, BorderLayout.CENTER);
        pnl_Main.add(pnl_pagination, BorderLayout.SOUTH);

        loadTableData("");

        return pnl_Main;
    }

    private String hashSHA1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashed = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Không thể hash mật khẩu", e);
        }
    }

    private JPanel createBorrowPanel() {
        MuonSach muonSachPanel = new MuonSach();
        return muonSachPanel;
    }

    private void showLibrarianInfoDialog() {
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

        LibrarianInfoPanel librarianInfoPanel = new LibrarianInfoPanel(maThuThu);
        infoDialog.add(librarianInfoPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(new Color(139, 69, 69));
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

    private JPanel createPenaltyTicketInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JPanel phieuPhat = new ThongTinPhieuPhat();
        panel.add(phieuPhat, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel pnl_Main = new JPanel(new BorderLayout());
        pnl_cards = new JPanel();
        currencyFormat = new DecimalFormat("#,##0.00");
        currencyFormat.setGroupingSize(3);

        JPanel pnl_top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl_top.setBackground(Color.WHITE);

        int currentYear = Year.now().getValue();
        Integer[] years = new Integer[currentYear - 2023 + 1];
        for (int i = 0; i < years.length; i++) {
            years[i] = 2023 + i;
        }
        JComboBox<Integer> yearComboBox = new JComboBox<>(years);
        yearComboBox.setPreferredSize(new Dimension(100, 40));
        yearComboBox.setMaximumSize(new Dimension(100, 40));
        yearComboBox.setSelectedItem(currentYear);
        pnl_top.add(yearComboBox);

        JButton btn_ExportPDF = new JButton("Xuất PDF");
        btn_ExportPDF.setPreferredSize(new Dimension(100, 40));
        btn_ExportPDF.setMaximumSize(new Dimension(100, 40));
        btn_ExportPDF.addActionListener(e -> {
            pdfExportController.exportToPDF((Integer) yearComboBox.getSelectedItem());
        });
        pnl_top.add(btn_ExportPDF);

        pnl_cards.setLayout(new BoxLayout(pnl_cards, BoxLayout.Y_AXIS));
        pnl_cards.setBackground(Color.WHITE);
        pnl_cards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(pnl_cards);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        updateStatisticsPanel(currentYear);

        yearComboBox.addActionListener(e -> {
            Integer selectedYear = (Integer) yearComboBox.getSelectedItem();
            updateStatisticsPanel(selectedYear);
        });

        pnl_Main.add(pnl_top, BorderLayout.NORTH);
        pnl_Main.add(scrollPane, BorderLayout.CENTER);

        return pnl_Main;
    }

    private JPanel createStatsPanel(ThongTinThongKe thongKe) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2d.setColor(new Color(106, 85, 85));
                g2d.fillRoundRect(0, 0, w, h, 20, 20);
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);
        JLabel lblThangNam = new JLabel(String.format("%02d/%d", thongKe.getThang(), Year.now().getValue()));
        lblThangNam.setFont(new Font("Arial", Font.BOLD, 14));
        lblThangNam.setForeground(Color.BLACK);
        lblThangNam.setBackground(Color.WHITE);
        lblThangNam.setOpaque(true);
        lblThangNam.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.add(lblThangNam);
        card.add(headerPanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblDocGiaMoi = new JLabel("Số lượng khách hàng đăng ký mới:");
        lblDocGiaMoi.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDocGiaMoi.setForeground(Color.WHITE);
        statsPanel.add(lblDocGiaMoi, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel valDocGiaMoi = new JLabel(String.valueOf(thongKe.getSoDocGiaMoi()));
        valDocGiaMoi.setFont(new Font("Arial", Font.BOLD, 14));
        valDocGiaMoi.setForeground(Color.WHITE);
        statsPanel.add(valDocGiaMoi, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblTraSachTre = new JLabel("Số lượng sách quá hạn trả:");
        lblTraSachTre.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTraSachTre.setForeground(Color.WHITE);
        statsPanel.add(lblTraSachTre, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel valTraSachTre = new JLabel(String.valueOf(thongKe.getSoLuongTraSachTre()));
        valTraSachTre.setFont(new Font("Arial", Font.BOLD, 14));
        valTraSachTre.setForeground(Color.WHITE);
        statsPanel.add(valTraSachTre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblMatSach = new JLabel("Số lượng sách mất:");
        lblMatSach.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMatSach.setForeground(Color.WHITE);
        statsPanel.add(lblMatSach, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel valMatSach = new JLabel(String.valueOf(thongKe.getSoLuongMatSach()));
        valMatSach.setFont(new Font("Arial", Font.BOLD, 14));
        valMatSach.setForeground(Color.WHITE);
        statsPanel.add(valMatSach, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblHuSach = new JLabel("Số lượng sách hư hỏng:");
        lblHuSach.setFont(new Font("Arial", Font.PLAIN, 14));
        lblHuSach.setForeground(Color.WHITE);
        statsPanel.add(lblHuSach, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel valHuSach = new JLabel(String.valueOf(thongKe.getSoLuongLamHuSach()));
        valHuSach.setFont(new Font("Arial", Font.BOLD, 14));
        valHuSach.setForeground(Color.WHITE);
        statsPanel.add(valHuSach, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblDoanhThu = new JLabel("Tổng thu:");
        lblDoanhThu.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDoanhThu.setForeground(Color.WHITE);
        statsPanel.add(lblDoanhThu, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel valDoanhThu = new JLabel(String.valueOf(thongKe.getDoanhThu()));
        valDoanhThu.setFont(new Font("Arial", Font.BOLD, 14));
        valDoanhThu.setForeground(Color.WHITE);
        statsPanel.add(valDoanhThu, gbc);

        card.add(statsPanel, BorderLayout.CENTER);
        return card;
    }

    private void loadTableData(String keyword) {
        tableModel.setRowCount(0);
        try {
            DocGiaDao docGiaDao = DocGiaDao.getInstance();
            if (docGiaDao == null) {
                throw new Exception("DocGiaDao instance is null");
            }
            docGiaList.clear();
            if (keyword.isEmpty()) {
                docGiaList = docGiaDao.layDanhSach();
            } else {
                docGiaList = docGiaDao.timKiem(keyword);
            }
            if (docGiaList == null) {
                throw new Exception("DocGia list is null");
            }

            Collections.reverse(docGiaList);

            userTotalRecords = docGiaList.size();
            int totalPages = (int) Math.ceil((double) userTotalRecords / userPageSize);

            if (userCurrentPage > totalPages && totalPages > 0) {
                userCurrentPage = totalPages;
            } else if (userCurrentPage < 1) {
                userCurrentPage = 1;
            }

            int start = (userCurrentPage - 1) * userPageSize;
            int end = Math.min(start + userPageSize, userTotalRecords);
            List<DocGia> paginatedList = new ArrayList<>();
            for (int i = start; i < end; i++) {
                paginatedList.add(docGiaList.get(i));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (DocGia docGia : paginatedList) {
                Object[] row = {
                    docGia.getMaNguoiDung(),
                    docGia.getTenNguoiDung(),
                    docGia.getTaiKhoan(),
                    docGia.getEmail(),
                    docGia.getSoDienThoai(),
                    sdf.format(docGia.getNgayTao())
                };
                tableModel.addRow(row);
            }

            lbl_pageInfo.setText("Trang " + userCurrentPage + "/" + (totalPages == 0 ? 1 : totalPages));
            user_btn_previous.setEnabled(userCurrentPage > 1);
            user_btn_next.setEnabled(userCurrentPage < totalPages);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu bảng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatisticsPanel(int year) {
        pnl_cards.removeAll();
        List<ThongTinThongKe> ds = DAO.ThongTinThongKeDao.getInstance().layDanhSachTheoNam(year);
        
        for (ThongTinThongKe tt : ds) {
            JPanel statsPanel = createStatsPanel(tt);
            pnl_cards.add(statsPanel);
        }
        
        pnl_cards.revalidate();
        pnl_cards.repaint();
    }
}