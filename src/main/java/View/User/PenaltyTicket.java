package View.User;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import DAO.Ls_Dg_sachDao;
import DAO.PhieuPhatDao;
import DAO.SachDao;
import Model.Ls_Dg_sach;
import Model.PhieuPhat;
import Model.Sach;
import Model.TheLoai;
import View.Login.Login;


public class PenaltyTicket extends JPanel {

	    private static final long serialVersionUID = 1L;
	    private String maDG;
		private JPanel contentPane;
	    private String fullName;
	    private JPanel pnl_content;

	    public PenaltyTicket(String fullName, String maDG) {
	        this.fullName = fullName;
	        this.maDG = maDG;
	        initializeUI();
	    }

	    public PenaltyTicket() {
	        this.fullName = "Guest";
	        this.maDG=null;
	        initializeUI();
	    }

	    private void initializeUI() {
    		setLayout(new BorderLayout()); // Set layout for PenaltyTicket panel
    		contentPane = new JPanel(new BorderLayout());
    		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

    		JPanel pnl_Main = new JPanel(new BorderLayout());

   			JPanel pnl_Header = createHeader();
    		pnl_Main.add(pnl_Header, BorderLayout.NORTH);

    		pnl_content = createContent();
    		pnl_Main.add(pnl_content, BorderLayout.CENTER);

    		contentPane.add(pnl_Main, BorderLayout.CENTER);
    		add(contentPane); // Add contentPane to this panel
		}

	    private JPanel createHeader() {
	        JPanel pnl_Header = new JPanel(new BorderLayout());
	        pnl_Header.setPreferredSize(new Dimension(0, 100));
	        pnl_Header.setBackground(new Color(107, 142, 35));

	        JPanel pnl_TopRow = new JPanel();
	        pnl_TopRow.setLayout(new BoxLayout(pnl_TopRow, BoxLayout.X_AXIS));
	        pnl_TopRow.setOpaque(false);
	        pnl_TopRow.setBorder(BorderFactory.createEmptyBorder(10, 35, 0, 20));

	        JButton btn_Avatar = createIconButton("pictures/profile.png", "👤");
	        JButton btn_Notification = createIconButton("pictures/bell.png", "🔔");

			btn_Avatar.setAlignmentY(Component.CENTER_ALIGNMENT);
			btn_Avatar.addActionListener(e -> showUserInfoDialog());

	        JLabel lbl_UserName = new JLabel(fullName);
	        lbl_UserName.setFont(new Font("SansSerif", Font.PLAIN, 14));
	        lbl_UserName.setForeground(Color.WHITE);

	        pnl_TopRow.add(btn_Avatar);
	        pnl_TopRow.add(Box.createHorizontalStrut(10));
	        pnl_TopRow.add(lbl_UserName);
	        pnl_TopRow.add(Box.createHorizontalGlue());
	        pnl_TopRow.add(btn_Notification);
	        pnl_Header.add(pnl_TopRow, BorderLayout.NORTH);
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
	    
	    private JPanel createContent() {
	        JPanel pnl_content = new JPanel();
	        pnl_content.setLayout(new BoxLayout(pnl_content, BoxLayout.Y_AXIS));
	        pnl_content.setBorder(new EmptyBorder(10, 20, 10, 20));
	        pnl_content.setBackground(Color.WHITE);
	        
	        if (this.maDG == null || this.maDG.isEmpty()) {
	            JLabel lbl_warning = new JLabel("Vui lòng đăng nhập để sử dụng tính năng này");
	            lbl_warning.setFont(new Font("SansSerif", Font.BOLD, 20));
	            lbl_warning.setForeground(Color.RED);
	            lbl_warning.setAlignmentX(Component.CENTER_ALIGNMENT);
	            
	            pnl_content.add(Box.createVerticalGlue());
	            pnl_content.add(lbl_warning);
	            pnl_content.add(Box.createVerticalGlue());
	            
	            return pnl_content;
	        }
	        
	        // Tạo panel tìm kiếm
	        JPanel pnl_search = new JPanel(new BorderLayout());
	        pnl_search.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
	        pnl_search.setBackground(Color.WHITE);
	        pnl_search.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
	        
	        JTextField txt_search = new JTextField();
	        txt_search.setFont(new Font("SansSerif", Font.PLAIN, 16));
	        txt_search.setPreferredSize(new Dimension(300, 30));
	        txt_search.setBorder(BorderFactory.createCompoundBorder(
	                BorderFactory.createLineBorder(Color.GRAY, 1),
	                BorderFactory.createEmptyBorder(5, 10, 5, 10)
	        ));
	        
	        // Placeholder cho JTextField
	        txt_search.setText("Nhập tên sách");
	        txt_search.setForeground(Color.GRAY);
	        txt_search.addFocusListener(new FocusAdapter() {
	            @Override
	            public void focusGained(FocusEvent e) {
	                if (txt_search.getText().equals("Nhập tên sách")) {
	                    txt_search.setText("");
	                    txt_search.setForeground(Color.BLACK);
	                }
	            }

	            @Override
	            public void focusLost(FocusEvent e) {
	                if (txt_search.getText().isEmpty()) {
	                    txt_search.setText("Nhập tên sách");
	                    txt_search.setForeground(Color.GRAY);
	                }
	            }
	        });
	        
	        pnl_search.add(txt_search, BorderLayout.CENTER);
	        pnl_content.add(pnl_search);
	        
	        // Tạo JScrollPane cho danh sách phiếu phạt
	        JPanel pnl_listContainer = new JPanel();
	        pnl_listContainer.setLayout(new BoxLayout(pnl_listContainer, BoxLayout.Y_AXIS));
	        pnl_listContainer.setBackground(Color.WHITE);
	        
	        JScrollPane scrollPane = new JScrollPane(pnl_listContainer);
	        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	        scrollPane.setBorder(BorderFactory.createEmptyBorder());
	        
	        pnl_content.add(scrollPane);
	        
	        // Tải danh sách phiếu phạt ban đầu (tất cả)
	        updatePenaltyList(pnl_listContainer, "");
	        
	        // Lọc tức thì khi nhập ký tự
	        txt_search.getDocument().addDocumentListener(new DocumentListener() {
	            @Override
	            public void insertUpdate(DocumentEvent e) {
	                filterPenaltyList(pnl_listContainer, txt_search.getText().trim());
	            }

	            @Override
	            public void removeUpdate(DocumentEvent e) {
	                filterPenaltyList(pnl_listContainer, txt_search.getText().trim());
	            }

	            @Override
	            public void changedUpdate(DocumentEvent e) {
	                filterPenaltyList(pnl_listContainer, txt_search.getText().trim());
	            }
	        });
	        
	        return pnl_content;
	    }

	    private void updatePenaltyList(JPanel pnl_listContainer, String keyword) {
	        pnl_listContainer.removeAll();
	        
	        PhieuPhatDao phieuPhatDao = new PhieuPhatDao();
	        List<PhieuPhat> dsPhieuPhat = phieuPhatDao.layDanhSachTheoDK(maDG);
	        SachDao sachDao = SachDao.getInstance();
	        
	        for (PhieuPhat phieu : dsPhieuPhat) {
	            List<Sach> dsSach = sachDao.layDanhSachTheoDK(phieu.getMaSach());
	            String tenSach = dsSach.isEmpty() ? "Không tìm thấy tên sách" : dsSach.get(0).getTenSach();
	            
	            // Lọc theo từ khóa
	            if (!keyword.equals("Nhập tên sách") && !keyword.isEmpty() && !tenSach.toLowerCase().contains(keyword.toLowerCase())) {
	                continue;
	            }
	            
	            JPanel pnl_item = new JPanel(new BorderLayout());
	            pnl_item.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	            pnl_item.setPreferredSize(new Dimension(0, 80));
	            pnl_item.setBackground(new Color(240, 233, 222));
	            pnl_item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
	            
	            JLabel lbl_tenSach = new JLabel(tenSach);
	            lbl_tenSach.setFont(new Font("SansSerif", Font.BOLD, 16));
	            pnl_item.add(lbl_tenSach, BorderLayout.NORTH);
	            
	            JLabel lbl_ngay = new JLabel("Date: " + phieu.getNgayPhieu());
	            lbl_ngay.setFont(new Font("SansSerif", Font.PLAIN, 12));
	            lbl_ngay.setForeground(Color.GRAY);
	            pnl_item.add(lbl_ngay, BorderLayout.CENTER);
	            
	            JLabel lbl_trangThai = new JLabel();
	            lbl_trangThai.setFont(new Font("SansSerif", Font.PLAIN, 14));
	            lbl_trangThai.setForeground(Color.RED);
	            lbl_trangThai.setText(phieu.getLoi().equals("Trả sách trễ") ? "Quá hạn" : "Làm mất sách");
	            pnl_item.add(lbl_trangThai, BorderLayout.EAST);
	            
	            // Sự kiện nhấn
	            pnl_item.addMouseListener(new MouseAdapter() {
	                @Override
	                public void mouseClicked(MouseEvent e) {
	                	 // Xóa toàn bộ nội dung của pnl_content (bao gồm thanh tìm kiếm và danh sách)
	                    pnl_content.removeAll();

	                    // Xóa cả các thành phần scrollPane và pnl_search nếu cần
	                    Component[] components = pnl_content.getComponents();
	                    for (Component component : components) {
	                        pnl_content.remove(component);
	                    }

	                    // Thêm panel chi tiết
	                    JPanel pnl_detail = createDetailPanel(phieu.getMaSach(), phieu.getLoi(), phieu.getGiaTien());
	                    pnl_content.add(pnl_detail);

	                    pnl_content.revalidate();
	                    pnl_content.repaint();
	                }

	                @Override
	                public void mouseEntered(MouseEvent e) {
	                    pnl_item.setBackground(new Color(245, 245, 245));
	                    pnl_item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	                }

	                @Override
	                public void mouseExited(MouseEvent e) {
	                    pnl_item.setBackground(new Color(240, 233, 222));
	                    pnl_item.setCursor(Cursor.getDefaultCursor());
	                }
	            });
	            
	            pnl_listContainer.add(pnl_item);
	            pnl_listContainer.add(Box.createVerticalStrut(10));  // Khoảng cách giữa các phiếu phạt
	        }
	        
	        pnl_listContainer.revalidate();
	        pnl_listContainer.repaint();
	    }


	    private void filterPenaltyList(JPanel pnl_listContainer, String keyword) {
	        pnl_listContainer.removeAll();
	        
	        PhieuPhatDao phieuPhatDao = new PhieuPhatDao();
	        List<PhieuPhat> dsPhieuPhat = phieuPhatDao.layDanhSachTheoDK(maDG);
	        SachDao sachDao = SachDao.getInstance();
	        
	        for (PhieuPhat phieu : dsPhieuPhat) {
	            List<Sach> dsSach = sachDao.layDanhSachTheoDK(phieu.getMaSach());
	            String tenSach = dsSach.isEmpty() ? "Không tìm thấy tên sách" : dsSach.get(0).getTenSach();
	            
	            // Lọc theo từ khóa
	            if (!keyword.equals("Nhập tên sách") && !keyword.isEmpty() && !tenSach.toLowerCase().contains(keyword.toLowerCase())) {
	                continue;
	            }
	            
	            JPanel pnl_item = new JPanel(new BorderLayout());
	            pnl_item.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	            pnl_item

	    .setPreferredSize(new Dimension(0, 80));
	            pnl_item.setBackground(new Color(240, 233, 222));
	            pnl_item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
	            
	            JLabel lbl_tenSach = new JLabel(tenSach);
	            lbl_tenSach.setFont(new Font("SansSerif", Font.BOLD, 16));
	            pnl_item.add(lbl_tenSach, BorderLayout.NORTH);
	            
	            JLabel lbl_ngay = new JLabel("Date: " + phieu.getNgayPhieu());
	            lbl_ngay.setFont(new Font("SansSerif", Font.PLAIN, 12));
	            lbl_ngay.setForeground(Color.GRAY);
	            pnl_item.add(lbl_ngay, BorderLayout.CENTER);
	            
	            JLabel lbl_trangThai = new JLabel();
	            lbl_trangThai.setFont(new Font("SansSerif", Font.PLAIN, 14));
	            lbl_trangThai.setForeground(Color.RED);
	            lbl_trangThai.setText(phieu.getLoi().equals("Trả sách trễ") ? "Quá hạn" : "Làm mất sách");
	            pnl_item.add(lbl_trangThai, BorderLayout.EAST);
	            
	            // Sự kiện nhấn
	            pnl_item.addMouseListener(new MouseAdapter() {
	                @Override
	                public void mouseClicked(MouseEvent e) {
	                    // Xóa toàn bộ nội dung của pnl_content
	                    pnl_content.removeAll();
	                    // Thêm panel chi tiết
	                    JPanel pnl_detail = createDetailPanel(phieu.getMaSach(), phieu.getLoi(), phieu.getGiaTien());
	                    pnl_content.add(pnl_detail);
	                    // Cập nhật giao diện
	                    pnl_content.revalidate();
	                    pnl_content.repaint();
	                }

	                @Override
	                public void mouseEntered(MouseEvent e) {
	                    pnl_item.setBackground(new Color(245, 245, 245));
	                    pnl_item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	                }

	                @Override
	                public void mouseExited(MouseEvent e) {
	                    pnl_item.setBackground(new Color(240, 233, 222));
	                    pnl_item.setCursor(Cursor.getDefaultCursor());
	                }
	            });
	            
	            pnl_listContainer.add(pnl_item);
	            pnl_listContainer.add(Box.createVerticalStrut(10)); // Khoảng cách giữa các phiếu phạt
	        }
	        
	        pnl_listContainer.revalidate();
	        pnl_listContainer.repaint();
	    }

	    
	    private JPanel createDetailPanel(String maSach, String loi, double tienPhat) {
	        JPanel pnl_detail = new JPanel(new BorderLayout(10, 10));
	        pnl_detail.setBorder(new EmptyBorder(10, 10, 10, 10));
	        pnl_detail.setBackground(Color.WHITE);

	        // Thêm thanh cuộn cho panel chi tiết
	        JScrollPane scrollPane = new JScrollPane();
	        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	        scrollPane.setBorder(BorderFactory.createEmptyBorder());

	        Ls_Dg_sachDao lsdgsachDao = new Ls_Dg_sachDao().getInstance();
	        List<Ls_Dg_sach> ds = lsdgsachDao.layDanhSachTheoMaDocGiaVaMaSach(maDG, maSach);
	        
	        if (ds.isEmpty()) {
	        	 // Tạo panel căn giữa cả thông báo và nút
	            JPanel pnl_noData = new JPanel(new GridBagLayout());
	            pnl_noData.setBackground(Color.WHITE);
	            
	            GridBagConstraints gbc = new GridBagConstraints();
	            gbc.gridx = 0;
	            gbc.gridy = 0;
	            gbc.insets = new Insets(10, 10, 10, 10);
	            gbc.anchor = GridBagConstraints.CENTER;

	            // Thông báo không có dữ liệu
	            JLabel lbl_empty = new JLabel("Không tìm thấy dữ liệu.");
	            lbl_empty.setFont(new Font("SansSerif", Font.BOLD, 20));
	            lbl_empty.setForeground(Color.GRAY);
	            lbl_empty.setHorizontalAlignment(SwingConstants.CENTER);
	            pnl_noData.add(lbl_empty, gbc);

	            // Nút quay lại
	            gbc.gridy = 1;
	            JButton btn_back = new JButton("Quay lại");
	            btn_back.setFont(new Font("SansSerif", Font.PLAIN, 16));
	            btn_back.setPreferredSize(new Dimension(150, 40));
	            btn_back.addActionListener(e -> {
	                pnl_content.removeAll();
	                pnl_content.add(createContent());
	                pnl_content.revalidate();
	                pnl_content.repaint();
	            });
	            pnl_noData.add(btn_back, gbc);

	            // Đặt panel này vào pnl_detail
	            pnl_detail.add(pnl_noData, BorderLayout.CENTER);
	            return pnl_detail;
	        }
	        
	        Ls_Dg_sach chiTiet = ds.get(0);
	        
	        // Panel chính chia đôi (Hình và thông tin)
	        JPanel pnl_mainContainer = new JPanel();
	        pnl_mainContainer.setLayout(new GridBagLayout());
	        pnl_mainContainer.setBackground(Color.WHITE);

	        // Phần ảnh sách + Tên sách + Tags (bên trái)
	        JPanel pnl_left = new JPanel();
	        pnl_left.setLayout(new BoxLayout(pnl_left, BoxLayout.Y_AXIS));
	        pnl_left.setBackground(Color.WHITE);

	        JLabel lbl_image = new JLabel();
	        if (maSach != null && !maSach.isEmpty()) {
	            ImageIcon imageIcon = new ImageIcon("Pictures/" + maSach + ".jpg");
	            Image image = imageIcon.getImage().getScaledInstance(180, 240, Image.SCALE_SMOOTH);
	            lbl_image.setIcon(new ImageIcon(image));
	        }
	        lbl_image.setAlignmentX(Component.CENTER_ALIGNMENT);
	        pnl_left.add(lbl_image);
	        pnl_left.add(Box.createVerticalStrut(10));

	        JLabel lbl_title = new JLabel(chiTiet.getTenSach());
	        lbl_title.setFont(new Font("SansSerif", Font.BOLD, 28));
	        lbl_title.setAlignmentX(Component.CENTER_ALIGNMENT);
	        pnl_left.add(lbl_title);
	        pnl_left.add(Box.createVerticalStrut(5));

	        JPanel pnl_tags = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
	        pnl_tags.setBackground(Color.WHITE);
	        SachDao sachDao = SachDao.getInstance();
	        List<Sach> dsSach = sachDao.layDanhSachTheoDK(maSach);
	        if (!dsSach.isEmpty()) {
	            Sach sach = dsSach.get(0);
	            for (TheLoai theLoai : sach.getDsTheLoai()) {
	                pnl_tags.add(createTag(theLoai.getTenTheLoai()));
	            }
	        }
	        pnl_tags.setAlignmentX(Component.CENTER_ALIGNMENT);
	        pnl_left.add(pnl_tags);

	        // Gói pnl_left để cố định vị trí bên trái
	        GridBagConstraints gbc_left = new GridBagConstraints();
	        gbc_left.gridx = 0;
	        gbc_left.gridy = 0;
	        gbc_left.anchor = GridBagConstraints.NORTHWEST;
	        gbc_left.insets = new Insets(0, 0, 0, 20); // Khoảng cách giữa ảnh và thông tin
	        pnl_mainContainer.add(pnl_left, gbc_left);

	        // Phần thông tin sách (bên phải, chỉ căn giữa theo chiều dọc)
	        JPanel pnl_info = new JPanel();
	        pnl_info.setLayout(new BoxLayout(pnl_info, BoxLayout.Y_AXIS));
	        pnl_info.setBackground(Color.WHITE);

	        // Ngày mượn, ngày trả, lỗi và số tiền phạt
	        JLabel lbl_ngayMuon = new JLabel("Ngày mượn: " + chiTiet.getNgayMuon());
	        lbl_ngayMuon.setFont(new Font("SansSerif", Font.PLAIN, 16));
	        pnl_info.add(lbl_ngayMuon);

	        JLabel lbl_ngayTra = new JLabel("Ngày trả: " + chiTiet.getNgayTra());
	        lbl_ngayTra.setFont(new Font("SansSerif", Font.PLAIN, 16));
	        pnl_info.add(lbl_ngayTra);
	        
	        pnl_info.add(Box.createVerticalStrut(5));

	        JLabel lbl_loi = new JLabel("Lỗi: " + loi);
	        lbl_loi.setFont(new Font("SansSerif", Font.BOLD, 18));
	        lbl_loi.setForeground(Color.RED);
	        pnl_info.add(lbl_loi);

	        JLabel lbl_tienPhat = new JLabel("Số tiền phạt: " + tienPhat + " VND");
	        lbl_tienPhat.setFont(new Font("SansSerif", Font.PLAIN, 16));
	        pnl_info.add(lbl_tienPhat);

	        // Gói pnl_info để căn giữa theo chiều dọc
	        GridBagConstraints gbc_right = new GridBagConstraints();
	        gbc_right.gridx = 1;
	        gbc_right.gridy = 0;
	        gbc_right.anchor = GridBagConstraints.CENTER;
	        pnl_mainContainer.add(pnl_info, gbc_right);

	        scrollPane.setViewportView(pnl_mainContainer);
	        pnl_detail.add(scrollPane, BorderLayout.CENTER);

	        // Nút quay lại (góc dưới cùng bên phải)
	        JPanel pnl_footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	        pnl_footer.setBackground(Color.WHITE);
	        JButton btn_back = new JButton("Quay lại");
	        btn_back.setPreferredSize(new Dimension(150, 40));
	        btn_back.addActionListener(e -> {
	            pnl_content.removeAll();
	            pnl_content.add(createContent());
	            pnl_content.revalidate();
	            pnl_content.repaint();
	        });
	        pnl_footer.add(btn_back);

	        pnl_detail.add(pnl_footer, BorderLayout.SOUTH);

	        return pnl_detail;
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

        UserInfoPanel userInfoPanel = new UserInfoPanel(maDG);
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

	    
	    private JLabel createTag(String tagName) {
	        JLabel lbl_tag = new JLabel(tagName);
	        lbl_tag.setFont(new Font("SansSerif", Font.PLAIN, 14));
	        lbl_tag.setOpaque(true);
	        lbl_tag.setBackground(Color.WHITE);
	        lbl_tag.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	        lbl_tag.setHorizontalAlignment(SwingConstants.CENTER);
	        lbl_tag.setPreferredSize(new Dimension(80, 30));
	        return lbl_tag;
	    }
	    
	}
