package bank_system;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class BankSystemTest {

    private CheckingAccount checking;
    private SavingsAccount savings;

    @BeforeEach
    void setUp() {
        // Khởi tạo tài khoản mẫu trước mỗi lần test
        checking = new CheckingAccount(12345, 1000.0);
        savings = new SavingsAccount(67890, 6000.0);
    }

    @Test
    @DisplayName("Kiểm tra nạp tiền vào tài khoản vãng lai")
    void testCheckingDeposit() {
        checking.deposit(500.0);
        assertEquals(1500.0, checking.getBalance(), "Số dư phải là 1500 sau khi nạp 500");
        assertEquals(1, checking.getTransactionList().size());
    }

    @Test
    @DisplayName("Kiểm tra rút tiền tài khoản vãng lai (Hợp lệ)")
    void testCheckingWithdrawSuccess() {
        checking.withdraw(400.0);
        assertEquals(600.0, checking.getBalance());
    }

    @Test
    @DisplayName("Kiểm tra rút tiền quá số dư tài khoản vãng lai (Lỗi)")
    void testCheckingWithdrawFail() {
        // Rút 2000 khi chỉ có 1000
        checking.withdraw(2000.0);
        assertEquals(1000.0, checking.getBalance(), "Số dư không được đổi nếu rút lỗi");
    }

    @Test
    @DisplayName("Kiểm tra giới hạn rút tiền tài khoản tiết kiệm (Max 1000)")
    void testSavingsWithdrawLimit() {
        savings.withdraw(1500.0); // Vượt hạn mức 1000
        assertEquals(6000.0, savings.getBalance(), "Không được rút quá 1000/lần");
    }

    @Test
    @DisplayName("Kiểm tra số dư tối thiểu tài khoản tiết kiệm (Min 5000)")
    void testSavingsMinBalance() {
        savings.withdraw(800.0); 
        // 6000 - 800 = 5200 (Vẫn > 5000 -> Thành công)
        assertEquals(5200.0, savings.getBalance());
        
        savings.withdraw(500.0); 
        // 5200 - 500 = 4700 (Vi phạm < 5000 -> Thất bại)
        assertEquals(5200.0, savings.getBalance(), "Số dư không được dưới 5000");
    }

    @Test
    @DisplayName("Kiểm tra nạp tiền số âm")
    void testInvalidDepositAmount() {
        checking.deposit(-100.0);
        assertEquals(1000.0, checking.getBalance(), "Số dư không đổi khi nạp tiền âm");
    }

    @Test
    @DisplayName("Kiểm tra đường dẫn file (Cố tình lỗi trên Linux/MacOS)")
    void testFilePathHardcoded() {
        String folder = "exports";
        String fileName = "transactions.csv";
        
        // Cố tình nối chuỗi bằng dấu gạch chéo cứng của Windows (\)
        String hardcodedPath = folder + "\\" + fileName;
        
        // Lấy định dạng chuẩn của hệ điều hành hiện tại đang chạy code để so sánh
        String expectedPath = folder + java.io.File.separator + fileName;
        
        // Test này sẽ PASS trên Windows, nhưng FAIL CHẮC CHẮN trên Ubuntu và macOS
        assertEquals(expectedPath, hardcodedPath, "Đường dẫn không tương thích đa nền tảng!");
    }
}