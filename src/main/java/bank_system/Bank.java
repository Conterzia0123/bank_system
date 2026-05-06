package bank_system;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quản lý danh sách khách hàng và các thao tác liên quan.
 */
public class Bank {
    private static final Logger logger = LoggerFactory.getLogger(Bank.class);
    private List<Customer> customerList;

    public Bank() {
        this.customerList = new ArrayList<>();
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList) {
        if (customerList == null) {
            this.customerList = new ArrayList<>();
        } else {
            this.customerList = customerList;
        }
    }

    /**
     * Đọc danh sách khách hàng từ InputStream.
     *
     * @param inputStream Luồng dữ liệu đầu vào
     */
    public void readCustomerList(InputStream inputStream) {
        logger.debug("Bắt đầu đọc dữ liệu khách hàng từ InputStream...");
        if (inputStream == null) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            Customer current = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                current = parseLineAndAddCustomer(line, current);
            }
        } catch (Exception e) {
            logger.error("Lỗi khi đọc danh sách khách hàng: {}", e.getMessage(), e);
        }
    }

    private Customer parseLineAndAddCustomer(String line, Customer currentCustomer) {
        int lastSpaceIndex = line.lastIndexOf(' ');
        if (lastSpaceIndex <= 0) {
            return currentCustomer;
        }

        String token = line.substring(lastSpaceIndex + 1).trim();
        if (token.matches("\\d{9}")) {
            String name = line.substring(0, lastSpaceIndex).trim();
            Customer newCustomer = new Customer(Long.parseLong(token), name);
            customerList.add(newCustomer);
            logger.info("Thêm khách hàng thành công: {}", name);
            return newCustomer;
        }

        if (currentCustomer != null) {
            parseAccountInfo(line, currentCustomer);
        }
        return currentCustomer;
    }

    private void parseAccountInfo(String line, Customer currentCustomer) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 3) {
            long accountNumber = Long.parseLong(parts[0]);
            double balance = Double.parseDouble(parts[2]);
            if (Account.CHECKING_TYPE.equals(parts[1])) {
                currentCustomer.addAccount(new CheckingAccount(accountNumber, balance));
            } else if (Account.SAVINGS_TYPE.equals(parts[1])) {
                currentCustomer.addAccount(new SavingsAccount(accountNumber, balance));
            }
        }
    }

    public String getCustomersInfoByIdOrder() {
        customerList.sort(Comparator.comparingLong(Customer::getIdNumber));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < customerList.size(); i++) {
            sb.append(customerList.get(i).getCustomerInfo());
            if (i < customerList.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public String getCustomersInfoByNameOrder() {
        List<Customer> copy = new ArrayList<>(customerList);
        copy.sort((c1, c2) -> {
            int res = c1.getFullName().compareTo(c2.getFullName());
            return res != 0 ? res : Long.compare(c1.getIdNumber(), c2.getIdNumber());
        });

        StringBuilder sb = new StringBuilder();
        for (Customer c : copy) {
            sb.append(c.getCustomerInfo()).append("\n");
        }
        return sb.toString().trim();
    }
}