package net.froihofer.common.remote;

import net.froihofer.common.data.UserRole;
import net.froihofer.common.data.dto.CustomerDTO;
import net.froihofer.common.data.dto.StockDTO;
import net.froihofer.common.exception.BankException;

import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.List;

@Remote
public interface RemoteBank {
    BigDecimal buyStock(Long customerId, String stockSymbol, int amount) throws BankException;

    BigDecimal sellStock(Long customerId, String stockSymbol, int amount) throws BankException;

    List<StockDTO> findStocksByCompany(String companyName) throws BankException;

    CustomerDTO findCustomerById(Long customerId) throws BankException;

    List<CustomerDTO> findCustomerByLastName(String lastName) throws BankException;

    List<StockDTO> listCustomerStockPortfolio(Long customerId) throws BankException;

    BigDecimal retrieveBankAvailableVolume() throws BankException;

    Long addCustomer(String firstName, String lastName, String address, String password) throws BankException;

    UserRole retrieveUserRole() throws BankException;

}
