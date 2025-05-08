package Controller;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DAO.DocGiaDao;
import DAO.ThuThuDao;
import Model.DocGia;
import Model.ThuThu;
import Util.JDBCUtil; 

public class LoginController {
    public interface LoginCallBack {
        void onSuccess(String username, boolean isAdmin);
        void onFailure(String message);
        void onError(String errorMessage);
    }

    public void login(String username, String password, LoginCallBack callBack) {
        System.out.println("LoginController.login() called");
        
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            System.out.println("Empty username or password");
            callBack.onFailure("Vui lòng nhập đầy đủ thông tin đăng nhập");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            System.out.println("Attempting to connect to database");
            String hashedPassword = JDBCUtil.hashPasswordSHA1(password);
            System.out.println("Password hashed successfully");
            
            conn = JDBCUtil.connect();
            System.out.println("Database connection established");

            String sql = "SELECT * FROM thuthu WHERE taiKhoan = ? AND matKhau = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            System.out.println("Checking thuthu table");
            rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Found in ThuThu table");
                callBack.onSuccess(username, true);
            } else {
                System.out.println("Not found in thuthu table, checking docgia table");
                sql = "SELECT * FROM docgia WHERE taiKhoan = ? AND matKhau = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                
                rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Found in docgia table");
                    callBack.onSuccess(username, false);
                } else {
                    System.out.println("Not found in either table");
                    callBack.onFailure("Tên đăng nhập hoặc mật khẩu không đúng");
                    //callBack.onSuccess(username, false); // debug
                }
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            callBack.onError("Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                JDBCUtil.closeConnection();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                System.out.println("Error closing database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public boolean checkUserExists(String username) throws Exception { // Kiểm tra khi thủ thư tạo tk mới cho độc giả, hoặc sửa username
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = Util.JDBCUtil.connect();

            String sql = "SELECT * FROM DocGia WHERE taiKhoan = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            rs = stmt.executeQuery();

            return rs.next();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
