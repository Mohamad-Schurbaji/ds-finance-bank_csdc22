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
import net.froihofer.dsfinance.ws.trading.PublicStockQuote;
import net.froihofer.dsfinance.ws.trading.TradingWSException_Exception;
import net.froihofer.dsfinance.ws.trading.TradingWebService;
import net.froihofer.dsfinance.ws.trading.TradingWebServiceService;
import net.froihofer.util.Mapper;
import net.froihofer.util.jboss.WildflyAuthDBHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.ws.BindingProvider;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

//TODO Add logging

@Stateless(name = "MyBank")
@DeclareRoles({"customer", "employee"})
public class BankImpl implements RemoteBank {
    private static final Logger log = LoggerFactory.getLogger(BankImpl.class);
    private static final String WEB_SERVICE_URL = "https://edu.dedisys.org/ds-finance/ws/TradingService";
    private static final String WEB_SERVICE_USERNAME = "csdc22bb_01";
    private static final String WEB_SERVICE_PASSWORD = "daaG2poh5";

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
    public void init() {
        initWebService();
        initBankVolume();
    }

    @RolesAllowed({"employee", "customer"})
    @Override
    public BigDecimal buyStock(Long customerId, String stockSymbol, int amount) throws BankException {
        throw new UnsupportedOperationException();
    }

    @RolesAllowed({"employee", "customer"})
    @Override
    public BigDecimal sellStock(Long customerId, String stockSymbol, int amount) throws BankException {
        throw new UnsupportedOperationException();
    }

    @RolesAllowed({"employee", "customer"})
    @Override
    public List<StockDTO> findStocksByCompany(String companyName) throws BankException {
        try {
            List<PublicStockQuote> stocks = tradingWebService.findStockQuotesByCompanyName(companyName);
            if (stocks == null)
                throw new BankException("Could not retrieve stocks information");
            return Mapper.convertPublicStockQuoteListToStockDtoList(stocks);
        } catch (TradingWSException_Exception e) {
            throw new BankException("Could not retrieve stocks information.\n" + e.getMessage(), e);
        }
    }

    @Override
    public CustomerDTO findCustomerById(Long customerId) throws BankException {
        throw new UnsupportedOperationException();
    }

    @RolesAllowed({"employee"})
    @Override
    public List<CustomerDTO> findCustomerByLastName(String lastName) throws BankException {
        if (lastName == null)
            throw new BankException("last name can't be null");
        if (lastName.isBlank())
            throw new BankException("last name can't be empty");
        try {
            List<Customer> customers = customerDAO.getCustomerByLastName(lastName);
            return Mapper.convertCustomerListToCustomerDtoList(customers);
        } catch (BankPersistenceException e) {
            throw new BankException("No results were found!", e);
        }
    }

    @RolesAllowed({"employee", "customer"})
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
        throw new UnsupportedOperationException();
    }

    @RolesAllowed({"employee", "customer"})
    @Override
    public BigDecimal retrieveBankAvailableVolume() throws BankException {
        try {
            return bankVolumeDAO.retrieveBankVolume();
        } catch (BankPersistenceException e) {
            throw new BankException("Could not retrieve Bank volume", e);
        }
    }

    @RolesAllowed({"employee"})
    @Override
    public Long addCustomer(String firstName, String lastName, String address, String password) throws BankException {
        if (firstName == null || lastName == null || address == null)
            throw new BankException("Customer infos can not be null");
        if (firstName.isBlank() || lastName.isBlank() || address.isBlank())
            throw new BankException("Customer infos can not be empty");
        WildflyAuthDBHelper wildflyAuthDbHelper;
        Customer customer;
        try {
            customer = customerDAO.addCustomer(firstName, lastName, address);
            wildflyAuthDbHelper = new WildflyAuthDBHelper(new File(System.getenv("JBOSS_HOME")));
            wildflyAuthDbHelper.addUser(String.valueOf(customer.getCustomerId()), password, new String[]{UserRole.CUSTOMER.getRoleName()});
            return customer.getCustomerId();
        } catch (IOException e) {
            //TODO Delete customer when AuthDB fails!
            throw new BankException("Could not find JBoss property / home path", e);
        } catch (BankPersistenceException e) {
            throw new BankException("Failed to persist Customer!", e);
        }
    }

    @RolesAllowed({"employee", "customer"})
    @Override
    public UserRole retrieveUserRole() throws BankException {
        if (sessionContext.isCallerInRole(UserRole.CUSTOMER.getRoleName()))
            return UserRole.CUSTOMER;
        else if (sessionContext.isCallerInRole(UserRole.EMPLOYEE.getRoleName()))
            return UserRole.EMPLOYEE;
        else
            throw new BankException("Unauthorized role!");
    }

    private void initWebService(){
        tradingWebService = new TradingWebServiceService().getTradingWebServicePort();
        BindingProvider bindingProvider = (BindingProvider) tradingWebService;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WEB_SERVICE_URL);
        bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, WEB_SERVICE_USERNAME);
        bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, WEB_SERVICE_PASSWORD);
        log.info("Initialized Web Service {}", tradingWebService);
    }

    private void initBankVolume(){
        this.bankVolumeDAO.initBankVolume();
    }
}
