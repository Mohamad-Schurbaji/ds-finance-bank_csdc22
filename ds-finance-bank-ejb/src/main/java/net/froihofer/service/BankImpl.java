package net.froihofer.service;

import net.froihofer.common.data.UserRole;
import net.froihofer.common.data.dto.CustomerDTO;
import net.froihofer.common.data.dto.StockDTO;
import net.froihofer.common.exception.BankException;
import net.froihofer.common.remote.RemoteBank;
import net.froihofer.data.BankPersistenceException;
import net.froihofer.data.dao.BankVolumeDAO;
import net.froihofer.data.dao.CustomerDAO;
import net.froihofer.data.dao.StockDAO;
import net.froihofer.data.entity.Customer;
import net.froihofer.data.entity.Stock;
import net.froihofer.dsfinance.ws.trading.TradingWebService;
import net.froihofer.dsfinance.ws.trading.TradingWebServiceService;
import net.froihofer.util.Mapper;
import net.froihofer.util.jboss.WildflyAuthDBHelper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.ws.BindingProvider;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Stateless(name = "MyBank")
@DeclareRoles({"customer", "employee"})
public class BankImpl implements RemoteBank {
    @Inject
    private BankVolumeDAO bankVolumeDAO;
    @Inject
    private CustomerDAO customerDAO;
    @Inject
    private StockDAO stockDAO;
    @Resource
    private SessionContext sessionContext;

    private TradingWebService tradingWebService;


    @PostConstruct
    public void initializeWebService() {
        tradingWebService = new TradingWebServiceService().getTradingWebServicePort();
        BindingProvider bindingProvider = (BindingProvider) tradingWebService;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "https://edu.dedisys.org/ds-finance/ws/TradingService");
        //TODO: Add credentials
        bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "");
        bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "");
    }

    @Override
    public BigDecimal buyStock(Long customerId, String stockSymbol, int amount) throws BankException {
        return null;
    }

    @Override
    public BigDecimal sellStock(Long customerId, String stockSymbol, int amount) throws BankException {
        return null;
    }

    @Override
    public List<StockDTO> findStocksByCompany(String companyName) throws BankException {
        return null;
    }

    @Override
    public CustomerDTO findCustomerById(Long customerId) throws BankException {
        return null;
    }

    @Override
    public List<CustomerDTO> findCustomerByLastName(String lastName) throws BankException {
        return null;
    }

    @Override
    public List<StockDTO> listCustomerStockPortfolio(Long customerId) throws BankException {
        try {
            List<Stock> stocks = stockDAO.getCustomerPortfolio(customerId);
            return Mapper.convertStockListToStockDtoList(stocks);
        } catch (BankPersistenceException e) {
            throw new BankException("Could not retrieve customer's portfolio", e);
        }
    }

    @Override
    public List<StockDTO> retrieveStockQuotes(List<String> symbols) throws BankException {
        return null;
    }

    @Override
    public BigDecimal retrieveBankAvailableVolume() throws BankException {
        try {
            return bankVolumeDAO.retrieveBankVolume();
        } catch (BankPersistenceException e) {
            throw new BankException("Could not retrieve Bank volume", e);
        }
    }

    @Override
    public Long addCustomer(String firstName, String lastName, String address, String password) throws BankException {
        if (firstName == null || lastName == null || address == null)
            throw new BankException("Customer infos can not be null");
        if (firstName.isBlank() || lastName.isBlank() || address.isBlank())
            throw new BankException("Customer infos can not be empty");
        WildflyAuthDBHelper wildflyAuthDbHelper = null;
        Customer customer = new Customer(firstName, lastName, address);
        try {
            wildflyAuthDbHelper = new WildflyAuthDBHelper(new File(System.getProperty("jbossPath")));
            wildflyAuthDbHelper.addUser(String.valueOf(customer.getCustomerId()), password, new String[]{UserRole.CUSTOMER.getRoleName()});
            customerDAO.addCustomer(customer);
            return customer.getCustomerId();
        } catch (IOException e) {
            throw new BankException("Could not find JBoss property / home path", e);
        } catch (BankPersistenceException e) {
            wildflyAuthDbHelper.deleteUser(String.valueOf(customer.getCustomerId()), password, new String[]{UserRole.CUSTOMER.getRoleName()});
            throw new BankException("Failed to persist Customer!", e);
            //TODO Add logging
        }
    }

    @Override
    public UserRole retrieveUserRole() throws BankException {
        if (sessionContext.isCallerInRole(UserRole.CUSTOMER.getRoleName()))
            return UserRole.CUSTOMER;
        else if (sessionContext.isCallerInRole(UserRole.EMPLOYEE.getRoleName()))
            return UserRole.EMPLOYEE;
        else
            throw new BankException("Unauthorized role!");
    }
}
