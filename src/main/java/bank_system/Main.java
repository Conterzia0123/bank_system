package bank_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Hệ thống BankSystem đang khởi động...");
        
        // Tạo thử một tài khoản để kiểm tra log
        CheckingAccount acc = new CheckingAccount(12345, 1000.0);
        acc.deposit(500.0);
        
        System.out.println("SỐ DƯ HIỆN TẠI: " + acc.getBalance());
        logger.info("Chương trình kết thúc an toàn.");
    }
}