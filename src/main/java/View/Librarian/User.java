
package View.Librarian;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import DAO.DocGiaDao;
import Model.DocGia;

public class User extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel pnl_content;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txt_search;
    private JComboBox<Integer> cmb_pageSize;
    private JButton btn_previous, btn_next;
    private JLabel lbl_pageInfo;
    private int currentPage = 1;
    private int pageSize = 20;
    private int totalRecords = 0;
    private List<DocGia> docGiaList = new ArrayList<>();

    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern USERNAME_PASSWORD_PATTERN = Pattern.compile("^.{8,}$");

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                System.out.println("[DEBUG] Starting User frame creation");
                User frame = new User();
                System.out.println("[DEBUG] User frame created successfully");
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to create User frame: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public User() {
        System.out.println("[DEBUG] Initializing User constructor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setTitle("Thư viện mini - Quản lý độc giả");
        setLocationRelativeTo(null);

        pnl_content = new JPanel(new BorderLayout());
        pnl_content.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(pnl_content);

        try {
            JPanel pnl_Sidebar = createSidebar();
            pnl_content.add(pnl_Sidebar, BorderLayout.WEST);

            JPanel pnl_Main = createMain();
            pnl_Main.setBackground(Color.WHITE);
            pnl_content.add(pnl_Main);

            JPanel pnl_Header = createHeader();
            pnl_content.add(pnl_Header, BorderLayout.NORTH);
        } catch (Exception e) {
            System.err.println("[ERROR] Error in User constructor: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khởi tạo giao diện: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("[DEBUG] User constructor completed");
    }

    private JPanel createSidebar() {
        System.out.println("[DEBUG] Creating sidebar");
        JPanel pnl_Sidebar = new JPanel();
        pnl_Sidebar.setLayout(new BoxLayout(pnl_Sidebar, BoxLayout.Y_AXIS));
        pnl_Sidebar.setPreferredSize(new Dimension(200, 600));
        pnl_Sidebar.setBackground(new Color(106, 85, 85));

        JButton btn_Book = new JButton("Sách");
        btn_Book.setPreferredSize(new Dimension(200, 50));
        btn_Book.setMaximumSize(new Dimension(200, 50));
        btn_Book.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn_Book.setOpaque(true);
        pnl_Sidebar.add(btn_Book);

        JButton btn_User = new JButton("Độc giả");
        btn_User.setPreferredSize(new Dimension(200, 50));
        btn_User.setMaximumSize(new Dimension(200, 50));
        btn_User.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn_User.setBackground(new Color(150, 130, 130));
        btn_User.setOpaque(true);
        pnl_Sidebar.add(btn_User);

        JButton btn_Borrow = new JButton("Mượn sách");
        btn_Borrow.setPreferredSize(new Dimension(200, 50));
        btn_Borrow.setMaximumSize(new Dimension(200, 50));
        btn_Borrow.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn_Borrow.setOpaque(true);
        pnl_Sidebar.add(btn_Borrow);

        JButton btn_PenaltyTicketInfo = new JButton("Thông tin phiếu phạt");
        btn_PenaltyTicketInfo.setPreferredSize(new Dimension(200, 50));
        btn_PenaltyTicketInfo.setMaximumSize(new Dimension(200, 50));
        btn_PenaltyTicketInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn_PenaltyTicketInfo.setOpaque(true);
        pnl_Sidebar.add(btn_PenaltyTicketInfo);

        JButton btn_Statistics = new JButton("Thống kê");
        btn_Statistics.setPreferredSize(new Dimension(200, 50));
        btn_Statistics.setMaximumSize(new Dimension(200, 50));
        btn_Statistics.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn_Statistics.setOpaque(true);
        pnl_Sidebar.add(btn_Statistics);

        pnl_Sidebar.add(Box.createVerticalGlue());

        JButton btn_Logout = new JButton("Đăng xuất");
        btn_Logout.setPreferredSize(new Dimension(200, 50));
        btn_Logout.setMaximumSize(new Dimension(200, 50));
        btn_Logout.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn_Logout.addActionListener(e -> {
            System.out.println("[DEBUG] Logout button clicked");
            dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    View.Login.Login loginForm = new View.Login.Login();
                    loginForm.setVisible(true);
                } catch (Exception ex) {
                    System.err.println("[ERROR] Failed to open Login form: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi mở form đăng nhập: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
        pnl_Sidebar.add(btn_Logout);

        return pnl_Sidebar;
    }

    private JPanel createHeader() {
        System.out.println("[DEBUG] Creating header");
        JPanel pnl_Header = new JPanel();
        pnl_Header.setLayout(new BoxLayout(pnl_Header, BoxLayout.X_AXIS));
        pnl_Header.setPreferredSize(new Dimension(0, 100));
        pnl_Header.setBackground(new Color(106, 85, 85));

        ImageIcon icon_Book = new ImageIcon("Pictures/book.png");
        if (icon_Book.getIconWidth() == -1) {
            System.out.println("[DEBUG] book.png not found, using text fallback");
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
        lbl_Title.setBackground(new Color(106, 85, 85));
        lbl_Title.setForeground(Color.WHITE);
        lbl_Title.setBorder(null);
        pnl_Header.add(lbl_Title);
        pnl_Header.add(Box.createHorizontalGlue());

        JLabel lbl_LibrarianName = new JLabel("Thủ thư: Nguyễn Văn A");
        lbl_LibrarianName.setFont(new Font("Arial", Font.PLAIN, 16));
        lbl_LibrarianName.setForeground(Color.WHITE);
        lbl_LibrarianName.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        pnl_Header.add(lbl_LibrarianName);

        Dimension size = new Dimension(50, 50);

        ImageIcon icon_Bell = new ImageIcon("Pictures/bell.png");
        JButton btn_Notification;
        if (icon_Bell.getIconWidth() == -1) {
            System.out.println("[DEBUG] bell.png not found, using emoji fallback");
            btn_Notification = new JButton("🔔");
        } else {
            Image scaledImage = icon_Bell.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            icon_Bell = new ImageIcon(scaledImage);
            btn_Notification = new JButton(icon_Bell);
        }
        btn_Notification.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        btn_Notification.setPreferredSize(size);
        btn_Notification.setMaximumSize(size);
        btn_Notification.setMinimumSize(size);
        pnl_Header.add(btn_Notification);

        ImageIcon icon_Profile = new ImageIcon("Pictures/profile.png");
        JButton btn_Profile;
        if (icon_Profile.getIconWidth() == -1) {
            System.out.println("[DEBUG] profile.png not found, using emoji fallback");
            btn_Profile = new JButton("👤");
        } else {
            Image scaledImage = icon_Profile.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            icon_Profile = new ImageIcon(scaledImage);
            btn_Profile = new JButton(icon_Profile);
        }
        btn_Profile.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        btn_Profile.setPreferredSize(size);
        btn_Profile.setMaximumSize(size);
        btn_Profile.setMinimumSize(size);
        pnl_Header.add(btn_Profile);

        return pnl_Header;
    }

    private JPanel createMain() {
        System.out.println("[DEBUG] Creating main panel");
        JPanel pnl_Main = new JPanel(new BorderLayout());

        // Phần tìm kiếm và nút chức năng
        JPanel pnl_top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl_top.setBackground(Color.WHITE);

        // Ô tìm kiếm với placeholder
        txt_search = new JTextField("Tìm mã độc giả, tên, tài khoản...", 25);
        txt_search.setPreferredSize(new Dimension(0, 40));
        txt_search.setMaximumSize(new Dimension(300, 40));
        txt_search.setFont(new Font("Arial", Font.PLAIN, 14));
        txt_search.setForeground(Color.GRAY);

        // Xử lý placeholder
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

        JButton btn_search = new JButton("Tìm");
        btn_search.setPreferredSize(new Dimension(80, 40));
        btn_search.setMaximumSize(new Dimension(80, 40));
        btn_search.setMinimumSize(new Dimension(80, 40));
        btn_search.setFont(new Font("Arial", Font.BOLD, 14));
        btn_search.addActionListener(e -> {
            System.out.println("[DEBUG] Search button clicked");
            currentPage = 1; // Reset về trang 1 khi tìm kiếm
            String keyword = txt_search.getText().trim();
            if (!keyword.equals("Tìm mã độc giả, tên, tài khoản...")) {
                loadTableData(keyword);
            } else {
                loadTableData("");
            }
        });
        pnl_top.add(btn_search);

        JButton btn_add = new JButton("Thêm");
        btn_add.setPreferredSize(new Dimension(80, 40));
        btn_add.setMaximumSize(new Dimension(80, 40));
        btn_add.setMinimumSize(new Dimension(80, 40));
        btn_add.addActionListener(e -> {
            System.out.println("[DEBUG] Add button clicked");
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

                // Kiểm tra các trường bắt buộc không được để trống
                if (tenNguoiDung.isEmpty() || taiKhoan.isEmpty() || matKhauGoc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên, tài khoản và mật khẩu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra định dạng bằng regex
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

                    // Kiểm tra tài khoản đã tồn tại chưa
                    if (docGiaDao.kiemTraTaiKhoanTonTai(taiKhoan)) {
                        JOptionPane.showMessageDialog(this, "Tài khoản '" + taiKhoan + "' đã tồn tại! Vui lòng chọn tài khoản khác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String maNguoiDung = docGiaDao.layMaNguoiDungMoiNhat();
                    if (maNguoiDung == null) {
                        throw new Exception("Cannot generate new maNguoiDung");
                    }
                    Date ngayTao = new Date();
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
                    System.err.println("[ERROR] Error adding user: " + ex.getMessage());
                    ex.printStackTrace();
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
            System.out.println("[DEBUG] Delete button clicked");
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String maNguoiDung = (String) tableModel.getValueAt(selectedRow, 0);
                String tenNguoiDung = (String) tableModel.getValueAt(selectedRow, 1);
                System.out.println("[DEBUG] Selected user: " + maNguoiDung + " - " + tenNguoiDung);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa độc giả '" + tenNguoiDung + "' (Mã: " + maNguoiDung + ") không?\n" +
                        "Các thông tin liên quan đến độc giả này sẽ được xóa toàn bộ.",
                        "Xác nhận xóa độc giả",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                System.out.println("[DEBUG] Confirmation result: " + confirm + " (0 = Yes, 2 = No)");
                if (confirm == JOptionPane.YES_OPTION) {
                    System.out.println("[DEBUG] Deleting user: " + maNguoiDung);
                    try {
                        DocGiaDao docGiaDao = DocGiaDao.getInstance();
                        if (docGiaDao == null) {
                            throw new Exception("DocGiaDao instance is null");
                        }
                        DocGia tmp = new DocGia();
                        tmp.setMaNguoiDung(maNguoiDung);
                        int rowsAffected = docGiaDao.xoaDoiTuong(tmp);
                        System.out.println("[DEBUG] Rows affected: " + rowsAffected);
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Xóa độc giả thành công!");
                            String keyword = txt_search.getText().trim();
                            if (keyword.equals("Tìm mã độc giả, tên, tài khoản...")) {
                                keyword = "";
                            }
                            // Kiểm tra nếu trang hiện tại trống sau khi xóa
                            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                            if (currentPage > totalPages && totalPages > 0) {
                                currentPage = totalPages;
                            }
                            loadTableData(keyword);
                        } else {
                            JOptionPane.showMessageDialog(this, "Xóa độc giả thất bại! Không tìm thấy độc giả hoặc lỗi cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        System.err.println("[ERROR] Error deleting user: " + ex.getMessage());
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Lỗi khi xóa độc giả: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                System.out.println("[DEBUG] No row selected");
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một độc giả để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        pnl_top.add(btn_delete);

        // Bảng hiển thị danh sách độc giả
        String[] columnNames = {"Mã Người Dùng", "Tên Người Dùng", "Tài Khoản", "Email", "Số Điện Thoại", "Ngày Tạo"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        loadTableData("");

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Phần phân trang
        JPanel pnl_pagination = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnl_pagination.setBackground(Color.WHITE);

        JLabel lbl_pageSize = new JLabel("Số dòng/trang:");
        pnl_pagination.add(lbl_pageSize);

        Integer[] pageSizes = {20, 30, 40};
        cmb_pageSize = new JComboBox<>(pageSizes);
        cmb_pageSize.setSelectedItem(20);
        cmb_pageSize.setPreferredSize(new Dimension(60, 30));
        cmb_pageSize.addActionListener(e -> {
            System.out.println("[DEBUG] Page size changed to: " + cmb_pageSize.getSelectedItem());
            pageSize = (Integer) cmb_pageSize.getSelectedItem();
            currentPage = 1; // Reset về trang 1 khi thay đổi pageSize
            String keyword = txt_search.getText().trim();
            if (keyword.equals("Tìm mã độc giả, tên, tài khoản...")) {
                keyword = "";
            }
            loadTableData(keyword);
        });
        pnl_pagination.add(cmb_pageSize);

        btn_previous = new JButton("Previous");
        btn_previous.setPreferredSize(new Dimension(100, 30));
        btn_previous.addActionListener(e -> {
            System.out.println("[DEBUG] Previous button clicked");
            if (currentPage > 1) {
                currentPage--;
                String keyword = txt_search.getText().trim();
                if (keyword.equals("Tìm mã độc giả, tên, tài khoản...")) {
                    keyword = "";
                }
                loadTableData(keyword);
            }
        });
        pnl_pagination.add(btn_previous);

        lbl_pageInfo = new JLabel("Trang 1/1");
        lbl_pageInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        pnl_pagination.add(lbl_pageInfo);

        btn_next = new JButton("Next");
        btn_next.setPreferredSize(new Dimension(100, 30));
        btn_next.addActionListener(e -> {
            System.out.println("[DEBUG] Next button clicked");
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            if (currentPage < totalPages) {
                currentPage++;
                String keyword = txt_search.getText().trim();
                if (keyword.equals("Tìm mã độc giả, tên, tài khoản...")) {
                    keyword = "";
                }
                loadTableData(keyword);
            }
        });
        pnl_pagination.add(btn_next);

        pnl_Main.add(pnl_top, BorderLayout.NORTH);
        pnl_Main.add(scrollPane, BorderLayout.CENTER);
        pnl_Main.add(pnl_pagination, BorderLayout.SOUTH);

        return pnl_Main;
    }

    private void loadTableData(String keyword) {
        System.out.println("[DEBUG] Loading table data with keyword: " + keyword + ", page: " + currentPage + ", pageSize: " + pageSize);
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

            // Đảo ngược danh sách ngay sau khi lấy dữ liệu
            Collections.reverse(docGiaList);

            totalRecords = docGiaList.size();
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            // Điều chỉnh currentPage nếu vượt quá totalPages
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            } else if (currentPage < 1) {
                currentPage = 1;
            }

            // Cắt danh sách theo trang từ đầu danh sách đã đảo ngược
            int start = (currentPage - 1) * pageSize;
            int end = Math.min(start + pageSize, totalRecords);
            List<DocGia> paginatedList = new ArrayList<>();
            for (int i = start; i < end; i++) {
                paginatedList.add(docGiaList.get(i));
            }

            // Cập nhật bảng
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

            // Cập nhật thông tin phân trang
            if (lbl_pageInfo != null) {
                lbl_pageInfo.setText("Trang " + currentPage + "/" + (totalPages == 0 ? 1 : totalPages));
            } else {
                System.err.println("[ERROR] lbl_pageInfo is null in loadTableData");
            }
            if (btn_previous != null) {
                btn_previous.setEnabled(currentPage > 1);
            } else {
                System.err.println("[ERROR] btn_previous is null in loadTableData");
            }
            if (btn_next != null) {
                btn_next.setEnabled(currentPage < totalPages);
            } else {
                System.err.println("[ERROR] btn_next is null in loadTableData");
            }

        } catch (Exception ex) {
            System.err.println("[ERROR] Error loading table data: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu bảng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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
            System.err.println("[ERROR] Error hashing password: " + e.getMessage());
            throw new RuntimeException("Không thể hash mật khẩu", e);
        }
    }
}
