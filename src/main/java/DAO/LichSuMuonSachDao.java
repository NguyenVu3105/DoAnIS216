package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.LichSuMuonSach;
import Util.JDBCUtil;

public class LichSuMuonSachDao implements InterfaceDao<LichSuMuonSach> {

    public static LichSuMuonSachDao getInstance() {
        return new LichSuMuonSachDao();
    }

    @Override
    public int themDoiTuong(LichSuMuonSach t) {
        String sql = "INSERT INTO lichsumuonsach (maLichSu, ngayMuon, ngayTra, trangThai, maSach, maThuThu, maDocGia) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getMaLichSu());
            stmt.setDate(2, t.getNgayMuon());
            stmt.setDate(3, t.getNgayTra());
            stmt.setString(4, t.getTrangThai());
            stmt.setString(5, t.getMaSach());
            stmt.setString(6, t.getMaThuThu());
            stmt.setString(7, t.getMaDocGia());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int xoaDoiTuong(LichSuMuonSach t) {
        String sql = "DELETE FROM lichsumuonsach WHERE maLichSu = ?";
        try (Connection conn = JDBCUtil.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getMaLichSu());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int capNhatDoiTuong(LichSuMuonSach t) {
        // TODO: Implement if needed
        return 0;
    }

    @Override
    public List<LichSuMuonSach> layDanhSach() {
        List<LichSuMuonSach> list = new ArrayList<>();
        String sql = "SELECT * FROM lichsumuonsach";
        try (Connection conn = JDBCUtil.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LichSuMuonSach ls = new LichSuMuonSach(
                        rs.getString("maLichSu"),
                        rs.getDate("ngayMuon"),
                        rs.getDate("ngayTra"),
                        rs.getString("trangThai"),
                        rs.getString("maSach"),
                        rs.getString("maThuThu"),
                        rs.getString("maDocGia")
                    );
                    list.add(ls);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<LichSuMuonSach> layDanhSachTheoDK(String dk) {
        // TODO: Implement if needed
        return null;
    }

    public String generateMaLichSu() {
        String prefix = "LS";
        int startNumber = 0; // Bắt đầu từ LS201 (200 bản ghi hiện có)
        String maLichSu;

        try (Connection conn = JDBCUtil.connect()) {
            // Lặp để tìm mã chưa tồn tại
            for (int i = startNumber; i <= 999; i++) {
                maLichSu = String.format("%s%03d", prefix, i); // Định dạng LSxxx (ví dụ: LS201)
                String sql = "SELECT COUNT(*) FROM lichsumuonsach WHERE maLichSu = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maLichSu);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        return maLichSu; // Trả về mã chưa tồn tại
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Trả về null nếu không tìm được mã hợp lệ
    }
}