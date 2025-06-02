package View.Librarian;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import DAO.LichSuMuonSachDao;
import DAO.Ls_Dg_sachDao;
import Model.LichSuMuonSach;
import Model.Ls_Dg_sach;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MuonSach extends JPanel {
    private JPanel pnl_Content;
    private JTextField txt_Search;
    private JPopupMenu suggestionsPopup;
    private JList<String> suggestionList;
    private JPanel pnl_MainContent;
    private JScrollPane scrollPane;
    private List<Ls_Dg_sach> ds;
    private List<Ls_Dg_sach> filteredDs;
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 20;
    private JButton btn_prev;
    private JButton btn_next;
    private final String PLACEHOLDER_TEXT = "Tìm mã KH, tên sách";
    private JPanel pnl_ListContent;

    public MuonSach() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 5, 5, 5));
        String dbg = LichSuMuonSachDao.getInstance().generateMaLichSu();
        System.out.println(dbg);
        pnl_Content = new JPanel(new BorderLayout());
        pnl_Content.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(pnl_Content, BorderLayout.CENTER);

        //JPanel pnl_Sidebar = createSidebar();
        //pnl_Content.add(pnl_Sidebar, BorderLayout.WEST);

        //JPanel pnl_Header = createHeader();
        //pnl_Content.add(pnl_Header, BorderLayout.NORTH);

        pnl_ListContent = createContent();
        pnl_Content.add(pnl_ListContent, BorderLayout.CENTER);
    }

    /*
    private JPanel createSidebar() {
        JPanel pnl_Sidebar = new JPanel();
        pnl_Sidebar.setLayout(new BoxLayout(pnl_Sidebar, BoxLayout.Y_AXIS));
        pnl_Sidebar.setPreferredSize(new Dimension(200, 600));
        pnl_Sidebar.setBackground(new Color(106, 85, 85));

        String[] buttonNames = {
            "Sách", "Độc giả", "Mượn sách", "Thông tin phiếu phạt", "Thống kê", "Đăng xuất"
        };

        for (String name : buttonNames) {
            JButton btn = new JButton(name);
            btn.setPreferredSize(new Dimension(200, 50));
            btn.setMaximumSize(new Dimension(200, 50));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            if (name.equals("Mượn sách")) {
                btn.setBackground(new Color(182, 162, 162));
                btn.setOpaque(true);
            }
            pnl_Sidebar.add(btn);
            if (name.equals("Thống kê")) {
                pnl_Sidebar.add(Box.createVerticalGlue());
            }
        }
        return pnl_Sidebar;
    }
    */
    /*
    private JPanel createHeader() {
        JPanel pnl_Header = new JPanel();
        pnl_Header.setLayout(new BoxLayout(pnl_Header, BoxLayout.X_AXIS));
        pnl_Header.setPreferredSize(new Dimension(0, 100));
        pnl_Header.setBackground(new Color(106, 85, 85));

        ImageIcon icon_Book = new ImageIcon("pictures/book.png");
        if (icon_Book.getIconWidth() == -1) {
            JLabel lbl_icon = new JLabel("BOOK");
            lbl_icon.setFont(new Font("Arial", Font.BOLD, 20));
            lbl_icon.setForeground(Color.WHITE);
            lbl_icon.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 60));
            pnl_Header.add(lbl_icon);
        } else {
            Image scaledImage = icon_Book.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel lbl_icon = new JLabel(new ImageIcon(scaledImage));
            lbl_icon.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 60));
            pnl_Header.add(lbl_icon);
        }

        JLabel lbl_Title = new JLabel("THƯ VIỆN MINI");
        lbl_Title.setFont(new Font("Arial", Font.BOLD, 24));
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
        if (icon_Bell.getIconWidth() != -1) {
            Image scaledImage = icon_Bell.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            btn_Notification = new JButton(new ImageIcon(scaledImage));
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
            btn_Profile = new JButton(new ImageIcon(scaledImage));
        } else {
            btn_Profile = new JButton("👤");
        }
        btn_Profile.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        btn_Profile.setPreferredSize(size);
        btn_Profile.setMaximumSize(size);
        btn_Profile.setMinimumSize(size);
        pnl_Header.add(btn_Profile);

        return pnl_Header;
    }*/

    private JPanel createContent() {
        JPanel panelMain = new JPanel(new BorderLayout());

        JPanel pnl_top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl_top.setBackground(Color.WHITE);

        txt_Search = new JTextField(25);
        txt_Search.setPreferredSize(new Dimension(300, 40));
        txt_Search.setMaximumSize(new Dimension(300, 40));
        txt_Search.setText(PLACEHOLDER_TEXT);
        txt_Search.setForeground(Color.GRAY);

        suggestionsPopup = new JPopupMenu();
        suggestionList = new JList<>();
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionsPopup.setFocusable(false);
        suggestionsPopup.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        suggestionsPopup.add(new JScrollPane(suggestionList));

        txt_Search.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt_Search.getText().equals(PLACEHOLDER_TEXT)) {
                    txt_Search.setText("");
                    txt_Search.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt_Search.getText().isEmpty()) {
                    txt_Search.setText(PLACEHOLDER_TEXT);
                    txt_Search.setForeground(Color.GRAY);
                }
            }
        });

        txt_Search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { showSuggestions(); }
            public void removeUpdate(DocumentEvent e) { showSuggestions(); }
            public void changedUpdate(DocumentEvent e) { showSuggestions(); }

            private void showSuggestions() {
                String input = txt_Search.getText().trim().toLowerCase();
                if (input.isEmpty() || input.equals(PLACEHOLDER_TEXT.toLowerCase())) {
                    suggestionsPopup.setVisible(false);
                    return;
                }

                List<String> matches = new ArrayList<>();
                for (Ls_Dg_sach ls : ds) {
                    if (ls.getMaNguoiDung().toLowerCase().contains(input) && !matches.contains(ls.getMaNguoiDung())) {
                        matches.add(ls.getMaNguoiDung());
                    }
                    if (ls.getTenSach().toLowerCase().contains(input) && !matches.contains(ls.getTenSach())) {
                        matches.add(ls.getTenSach());
                    }
                }

                if (matches.isEmpty()) {
                    suggestionsPopup.setVisible(false);
                    return;
                }

                suggestionList.setListData(matches.toArray(new String[0]));
                suggestionList.setSelectedIndex(0);
                suggestionsPopup.setPopupSize(txt_Search.getWidth(), 150);
                suggestionsPopup.show(txt_Search, 0, txt_Search.getHeight());
            }
        });

        txt_Search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && suggestionsPopup.isVisible()) {
                    String selected = suggestionList.getSelectedValue();
                    if (selected != null) {
                        txt_Search.setText(selected);
                        suggestionsPopup.setVisible(false);
                        updateDisplayedBooksByKeyword(selected);
                    }
                }
            }
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String selected = suggestionList.getSelectedValue();
                    if (selected != null) {
                        txt_Search.setText(selected);
                        suggestionsPopup.setVisible(false);
                        updateDisplayedBooksByKeyword(selected);
                    }
                }
            }
        });

        pnl_top.add(txt_Search);

        JButton btn_search = new JButton("Tìm Kiếm");
        btn_search.setPreferredSize(new Dimension(120, 40));
        btn_search.addActionListener(e -> {
            String keyword = txt_Search.getText().trim();
            if (keyword.equals(PLACEHOLDER_TEXT) || keyword.isEmpty()) {
                filteredDs = new ArrayList<>(ds);
                currentPage = 1;
                updatePageContent();
            } else {
                updateDisplayedBooksByKeyword(keyword);
            }
        });
        pnl_top.add(btn_search);

        JButton btn_add = new JButton("Thêm");
        btn_add.setPreferredSize(new Dimension(80, 40));
        btn_add.addActionListener(e -> showAddForm());
        pnl_top.add(btn_add);

        JButton btn_filter = new JButton("Lọc");
        btn_filter.setPreferredSize(new Dimension(80, 40));

        JPopupMenu filterMenu = new JPopupMenu();
        JMenuItem itemChuaTra = new JMenuItem("Chưa trả");
        JMenuItem itemDaTra = new JMenuItem("Đã trả");
        JMenuItem itemTatCa = new JMenuItem("Tất cả");
        filterMenu.add(itemChuaTra);
        filterMenu.add(itemDaTra);
        filterMenu.add(itemTatCa);

        itemChuaTra.addActionListener(e -> {
            filteredDs = new ArrayList<>();
            for (Ls_Dg_sach ls : ds) {
                if (ls.getTrangThai().equals("Chưa trả")) {
                    filteredDs.add(ls);
                }
            }
            currentPage = 1;
            updatePageContent();
        });

        itemDaTra.addActionListener(e -> {
            filteredDs = new ArrayList<>();
            for (Ls_Dg_sach ls : ds) {
                if (ls.getTrangThai().equals("Đã trả")) {
                    filteredDs.add(ls);
                }
            }
            currentPage = 1;
            updatePageContent();
        });

        itemTatCa.addActionListener(e -> {
            filteredDs = new ArrayList<>(ds);
            currentPage = 1;
            updatePageContent();
        });

        btn_filter.addActionListener(e -> filterMenu.show(btn_filter, 0, btn_filter.getHeight()));
        pnl_top.add(btn_filter);

        panelMain.add(pnl_top, BorderLayout.NORTH);

        pnl_MainContent = new JPanel();
        pnl_MainContent.setLayout(new BoxLayout(pnl_MainContent, BoxLayout.Y_AXIS));
        pnl_MainContent.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(pnl_MainContent);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(900, 550));

        ds = Ls_Dg_sachDao.getInstance().layDanhSach();
        filteredDs = ds != null ? new ArrayList<>(ds) : new ArrayList<>();
        updatePageContent();

        panelMain.add(scrollPane, BorderLayout.CENTER);

        JPanel pnl_pagination = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnl_pagination.setBackground(Color.WHITE);
        pnl_pagination.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btn_prev = new JButton("Trước");
        btn_prev.setPreferredSize(new Dimension(80, 40));
        btn_prev.setVisible(currentPage > 1);
        btn_prev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updatePageContent();
            }
        });
        pnl_pagination.add(btn_prev);

        btn_next = new JButton("Sau");
        btn_next.setPreferredSize(new Dimension(80, 40));
        btn_next.setVisible(ds != null && ds.size() > ITEMS_PER_PAGE);
        btn_next.addActionListener(e -> {
            if ((currentPage * ITEMS_PER_PAGE) < filteredDs.size()) {
                currentPage++;
                updatePageContent();
            }
        });
        pnl_pagination.add(btn_next);

        panelMain.add(pnl_pagination, BorderLayout.SOUTH);

        return panelMain;
    }

    private void updateDisplayedBooksByKeyword(String keyword) {
        filteredDs = new ArrayList<>();
        String lowerQuery = keyword.toLowerCase();
        for (Ls_Dg_sach ls : ds) {
            if (ls.getMaNguoiDung().toLowerCase().contains(lowerQuery) ||
                ls.getTenSach().toLowerCase().contains(lowerQuery)) {
                filteredDs.add(ls);
            }
        }
        currentPage = 1;
        updatePageContent();
    }

    private void showAddForm() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new GridBagLayout());
        addPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblTitle = new JLabel("Thêm Lịch Sử Mượn Sách");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        addPanel.add(new JLabel("Mã lịch sử:"), gbc);
        gbc.gridx = 1;
        String maLichSu = LichSuMuonSachDao.getInstance().generateMaLichSu();
        JTextField txtMaLichSu = new JTextField(maLichSu, 20);
        txtMaLichSu.setEditable(false);
        addPanel.add(txtMaLichSu, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addPanel.add(new JLabel("Mã sách:"), gbc);
        gbc.gridx = 1;
        JTextField txtMaSach = new JTextField(20);
        addPanel.add(txtMaSach, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addPanel.add(new JLabel("Mã thủ thư:"), gbc);
        gbc.gridx = 1;
        JTextField txtMaThuThu = new JTextField(Session.LoginSession.getInstance().getMaNguoiDung(), 20);
        txtMaThuThu.setEditable(false);
        addPanel.add(txtMaThuThu, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addPanel.add(new JLabel("Mã độc giả:"), gbc);
        gbc.gridx = 1;
        JTextField txtMaDocGia = new JTextField(20);
        addPanel.add(txtMaDocGia, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addPanel.add(new JLabel("Ngày mượn (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JTextField txtNgayMuon = new JTextField(sdf.format(new Date()), 20);
        addPanel.add(txtNgayMuon, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addPanel.add(new JLabel("Ngày trả (yyyy-MM-dd, để trống nếu chưa trả):"), gbc);
        gbc.gridx = 1;
        JTextField txtNgayTra = new JTextField(20);
        addPanel.add(txtNgayTra, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        JTextField txtTrangThai = new JTextField("Chưa trả", 20);
        txtTrangThai.setEditable(false);
        addPanel.add(txtTrangThai, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        JButton btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> {
            try {
                String maSach = txtMaSach.getText().trim();
                String maThuThu = txtMaThuThu.getText().trim();
                String maDocGia = txtMaDocGia.getText().trim();
                String ngayMuonStr = txtNgayMuon.getText().trim();
                String ngayTraStr = txtNgayTra.getText().trim();
                String trangThai = txtTrangThai.getText().trim();

                if (maSach.isEmpty() || maThuThu.isEmpty() || maDocGia.isEmpty() || ngayMuonStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ mã sách, mã thủ thư, mã độc giả và ngày mượn!");
                    return;
                }

                if (maLichSu.length() > 5) {
                    JOptionPane.showMessageDialog(this, "Mã lịch sử quá dài, tối đa 5 ký tự!");
                    return;
                }

                SimpleDateFormat sdfParse = new SimpleDateFormat("yyyy-MM-dd");
                java.sql.Date ngayMuon;
                try {
                    ngayMuon = new java.sql.Date(sdfParse.parse(ngayMuonStr).getTime());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ngày mượn không đúng định dạng (yyyy-MM-dd)!");
                    return;
                }
                java.sql.Date ngayTra = null;
                if (!ngayTraStr.isEmpty()) {
                    try {
                        ngayTra = new java.sql.Date(sdfParse.parse(ngayTraStr).getTime());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Ngày trả không đúng định dạng (yyyy-MM-dd)!");
                        return;
                    }
                }

                LichSuMuonSach ls = new LichSuMuonSach(maLichSu, ngayMuon, ngayTra, trangThai, maSach, maThuThu, maDocGia);

                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn thêm lịch sử mượn sách với mã " + maLichSu + "?",
                    "Xác nhận thêm",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    int result = LichSuMuonSachDao.getInstance().themDoiTuong(ls);
                    pnl_Content.removeAll();
                    //pnl_Content.add(createSidebar(), BorderLayout.WEST);
                    //pnl_Content.add(createHeader(), BorderLayout.NORTH);
                    pnl_Content.add(pnl_ListContent, BorderLayout.CENTER);
                    if (result > 0) {
                        ds = Ls_Dg_sachDao.getInstance().layDanhSach();
                        filteredDs = ds != null ? new ArrayList<>(ds) : new ArrayList<>();
                        currentPage = 1;
                        updatePageContent();
                        JOptionPane.showMessageDialog(this, "Thêm lịch sử mượn sách thành công!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Thêm thất bại! Vui lòng kiểm tra lại dữ liệu.");
                    }
                    pnl_Content.revalidate();
                    pnl_Content.repaint();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });
        addPanel.add(btnSave, gbc);

        gbc.gridx = 1;
        JButton btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> {
            pnl_Content.removeAll();
            //pnl_Content.add(createSidebar(), BorderLayout.WEST);
            //pnl_Content.add(createHeader(), BorderLayout.NORTH);
            pnl_Content.add(pnl_ListContent, BorderLayout.CENTER);
            pnl_Content.revalidate();
            pnl_Content.repaint();
        });
        addPanel.add(btnCancel, gbc);

        pnl_Content.removeAll();
        //pnl_Content.add(createSidebar(), BorderLayout.WEST);
        //pnl_Content.add(createHeader(), BorderLayout.NORTH);
        pnl_Content.add(addPanel, BorderLayout.CENTER);
        pnl_Content.revalidate();
        pnl_Content.repaint();
    }

    private void showEditForm(Ls_Dg_sach lsDg) {
        LichSuMuonSach ls = LichSuMuonSachDao.getInstance().getByMaLichSu(lsDg.getMaLichSu());

        if (ls == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi lịch sử mượn sách!");
            return;
        }

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new GridBagLayout());
        editPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblTitle = new JLabel("Sửa Lịch Sử Mượn Sách");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        editPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        editPanel.add(new JLabel("Mã lịch sử:"), gbc);
        gbc.gridx = 1;
        JTextField txtMaLichSu = new JTextField(ls.getMaLichSu(), 20);
        txtMaLichSu.setEditable(false);
        editPanel.add(txtMaLichSu, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        editPanel.add(new JLabel("Mã sách:"), gbc);
        gbc.gridx = 1;
        JTextField txtMaSach = new JTextField(ls.getMaSach(), 20);
        editPanel.add(txtMaSach, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        editPanel.add(new JLabel("Mã thủ thư:"), gbc);
        gbc.gridx = 1;
        JTextField txtMaThuThu = new JTextField(ls.getMaThuThu(), 20);
        editPanel.add(txtMaThuThu, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        editPanel.add(new JLabel("Mã độc giả:"), gbc);
        gbc.gridx = 1;
        JTextField txtMaDocGia = new JTextField(ls.getMaDocGia(), 20);
        editPanel.add(txtMaDocGia, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        editPanel.add(new JLabel("Ngày mượn (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JTextField txtNgayMuon = new JTextField(sdf.format(ls.getNgayMuon()), 20);
        editPanel.add(txtNgayMuon, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        editPanel.add(new JLabel("Ngày trả (yyyy-MM-dd, để trống nếu chưa trả):"), gbc);
        gbc.gridx = 1;
        JTextField txtNgayTra = new JTextField(ls.getNgayTra() != null ? sdf.format(ls.getNgayTra()) : "", 20);
        editPanel.add(txtNgayTra, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        editPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        String[] trangThaiOptions = {"Chưa trả", "Đã trả"};
        JComboBox<String> cbTrangThai = new JComboBox<>(trangThaiOptions);
        cbTrangThai.setSelectedItem(ls.getTrangThai());
        cbTrangThai.setPreferredSize(new Dimension(200, 25));
        editPanel.add(cbTrangThai, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        JButton btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> {
            try {
                String maSach = txtMaSach.getText().trim();
                String maThuThu = txtMaThuThu.getText().trim();
                String maDocGia = txtMaDocGia.getText().trim();
                String ngayMuonStr = txtNgayMuon.getText().trim();
                String ngayTraStr = txtNgayTra.getText().trim();
                String trangThai = (String) cbTrangThai.getSelectedItem();

                if (maSach.isEmpty() || maThuThu.isEmpty() || maDocGia.isEmpty() || ngayMuonStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ mã sách, mã thủ thư, mã độc giả và ngày mượn!");
                    return;
                }

                SimpleDateFormat sdfUpdate = new SimpleDateFormat("yyyy-MM-dd");
                java.sql.Date ngayMuon;
                try {
                    ngayMuon = new java.sql.Date(sdfUpdate.parse(ngayMuonStr).getTime());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ngày mượn không đúng định dạng (yyyy-MM-dd)!");
                    return;
                }
                java.sql.Date ngayTra = null;
                if (!ngayTraStr.isEmpty()) {
                    try {
                        ngayTra = new java.sql.Date(sdfUpdate.parse(ngayTraStr).getTime());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Ngày trả không đúng định dạng (yyyy-MM-dd)!");
                        return;
                    }
                }

                LichSuMuonSach lsUpdated = new LichSuMuonSach(
                    ls.getMaLichSu(), ngayMuon, ngayTra, trangThai, maSach, maThuThu, maDocGia
                );

                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn sửa lịch sử mượn sách với mã " + ls.getMaLichSu() + "?",
                    "Xác nhận sửa",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    int result = LichSuMuonSachDao.getInstance().capNhatDoiTuong(lsUpdated);
                    pnl_Content.removeAll();
                    //pnl_Content.add(createSidebar(), BorderLayout.WEST);
                    //pnl_Content.add(createHeader(), BorderLayout.NORTH);
                    pnl_Content.add(pnl_ListContent, BorderLayout.CENTER);
                    if (result > 0) {
                        ds = Ls_Dg_sachDao.getInstance().layDanhSach();
                        filteredDs = ds != null ? new ArrayList<>(ds) : new ArrayList<>();
                        currentPage = 1;
                        updatePageContent();
                        JOptionPane.showMessageDialog(this, "Cập nhật lịch sử mượn sách thành công!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Cập nhật thất bại! Vui lòng kiểm tra lại dữ liệu.");
                    }
                    pnl_Content.revalidate();
                    pnl_Content.repaint();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });
        editPanel.add(btnSave, gbc);

        gbc.gridx = 1;
        JButton btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> {
            pnl_Content.removeAll();
            //pnl_Content.add(createSidebar(), BorderLayout.WEST);
            //pnl_Content.add(createHeader(), BorderLayout.NORTH);
            pnl_Content.add(pnl_ListContent, BorderLayout.CENTER);
            pnl_Content.revalidate();
            pnl_Content.repaint();
        });
        editPanel.add(btnCancel, gbc);

        pnl_Content.removeAll();
        //pnl_Content.add(createSidebar(), BorderLayout.WEST);
        //pnl_Content.add(createHeader(), BorderLayout.NORTH);
        pnl_Content.add(editPanel, BorderLayout.CENTER);
        pnl_Content.revalidate();
        pnl_Content.repaint();
    }

    private void updatePageContent() {
        pnl_MainContent.removeAll();

        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredDs.size());

        Font itemFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font itemBoldFont = new Font("Segoe UI", Font.BOLD, 14);

        for (int i = startIndex; i < endIndex; i++) {
            Ls_Dg_sach ls = filteredDs.get(i);
            pnl_MainContent.add(createBookItem(ls, itemFont, itemBoldFont));
            pnl_MainContent.add(Box.createVerticalStrut(10));
        }

        int itemHeight = 180;
        int gapHeight = 10;
        int itemCount = endIndex - startIndex;
        int totalHeight = (itemCount * itemHeight) + ((itemCount - 1) * gapHeight) + (itemCount > 0 ? 120 : 0);
        if (itemCount == 0) {
            totalHeight = 0;
        }
        pnl_MainContent.setPreferredSize(new Dimension(900, totalHeight));

        pnl_MainContent.revalidate();
        pnl_MainContent.repaint();

        scrollPane.getVerticalScrollBar().setValue(0);

        if (btn_prev != null) {
            btn_prev.setVisible(currentPage > 1);
        }
        if (btn_next != null) {
            btn_next.setVisible((currentPage * ITEMS_PER_PAGE) < filteredDs.size());
        }
    }

    private JPanel createBookItem(Ls_Dg_sach ls, Font itemFont, Font itemBoldFont) {
        RoundedPanel itemPanel = new RoundedPanel(20);
        itemPanel.setLayout(new BorderLayout());
        itemPanel.setBackground(new Color(182, 162, 162));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        itemPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        itemPanel.setBorder(new EmptyBorder(5, 10, 5, 15));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(240, 180));

        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.setOpaque(false);
        ImageIcon bookIcon = new ImageIcon("Pictures/" + ls.getAnh());
        if (bookIcon.getIconWidth() != -1) {
            Image scaledBook = bookIcon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
            JLabel lblBook = new JLabel(new ImageIcon(scaledBook));
            imagePanel.add(lblBook);
        }
        leftPanel.add(imagePanel, BorderLayout.NORTH);

        String bookName = "<html><div style='text-align: center; width: 220px; overflow-wrap: break-word; white-space: normal;'>" + 
                         ls.getTenSach() + "</div></html>";
        JLabel lblBookName = new JLabel(bookName);
        lblBookName.setFont(itemBoldFont);
        lblBookName.setForeground(Color.BLACK);
        lblBookName.setHorizontalAlignment(SwingConstants.CENTER);
        lblBookName.setVerticalAlignment(SwingConstants.TOP);
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        namePanel.setOpaque(false);
        namePanel.add(lblBookName);
        leftPanel.add(namePanel, BorderLayout.CENTER);

        itemPanel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 20, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 5, 1, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        rightPanel.add(createInfoRow("Tên:", ls.getTenNguoiDung(), itemFont, itemBoldFont), gbc);
        gbc.gridy++;
        rightPanel.add(createInfoRow("Mã KH:", ls.getMaNguoiDung(), itemFont, itemBoldFont), gbc);
        gbc.gridy++;
        rightPanel.add(createInfoRow("Ngày mượn:", ls.getNgayMuon().toString(), itemFont, itemBoldFont), gbc);
        gbc.gridy++;
        rightPanel.add(createInfoRow("Ngày trả:", ls.getNgayTra() == null ? "Chưa có" : ls.getNgayTra().toString(), itemFont, itemBoldFont), gbc);
        gbc.gridy++;
        rightPanel.add(createStatusRow("Trạng thái:", ls.getTrangThai(), itemFont, itemBoldFont), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 1, 5);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnEdit = new JButton("Sửa");
        btnEdit.setPreferredSize(new Dimension(70, 30));
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEdit.setBackground(new Color(204, 204, 204));
        btnEdit.setOpaque(true);
        btnEdit.setBorderPainted(true);
        btnEdit.addActionListener(e -> showEditForm(ls));
        actionPanel.add(btnEdit);

        JButton btnDelete = new JButton("Xóa");
        btnDelete.setPreferredSize(new Dimension(70, 30));
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setBackground(new Color(204, 204, 204));
        btnDelete.setOpaque(true);
        btnDelete.setBorderPainted(true);
        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa lịch sử mượn sách với mã " + ls.getMaLichSu() + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                LichSuMuonSach lsToDelete = new LichSuMuonSach(
                    ls.getMaLichSu(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                );
                int result = LichSuMuonSachDao.getInstance().xoaDoiTuong(lsToDelete);
                if (result > 0) {
                    ds = Ls_Dg_sachDao.getInstance().layDanhSach();
                    filteredDs = ds != null ? new ArrayList<>(ds) : new ArrayList<>();
                    currentPage = Math.min(currentPage, (filteredDs.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE);
                    updatePageContent();
                    JOptionPane.showMessageDialog(this, "Xóa lịch sử mượn sách thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Vui lòng thử lại.");
                }
            }
        });
        actionPanel.add(btnDelete);

        rightPanel.add(actionPanel, gbc);

        itemPanel.add(rightPanel, BorderLayout.CENTER);

        return itemPanel;
    }

    private JPanel createInfoRow(String label, String value, Font itemFont, Font itemBoldFont) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        panel.setOpaque(false);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(itemBoldFont);
        lblLabel.setForeground(Color.BLACK);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(itemFont);
        lblValue.setForeground(Color.BLACK);

        panel.add(lblLabel);
        panel.add(lblValue);
        return panel;
    }

    private JPanel createStatusRow(String label, String status, Font itemFont, Font itemBoldFont) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        panel.setOpaque(false);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(itemBoldFont);
        lblLabel.setForeground(Color.BLACK);

        JLabel lblStatus = new JLabel(status);
        lblStatus.setFont(itemBoldFont);
        lblStatus.setForeground(status.equals("Chưa trả") ? Color.RED : Color.GREEN);

        panel.add(lblLabel);
        panel.add(lblStatus);
        return panel;
    }

    class RoundedPanel extends JPanel {
        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}