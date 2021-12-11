package net.froihofer.data.dao;

import net.froihofer.data.BankPersistenceException;
import net.froihofer.data.PersistenceFaultCode;
import net.froihofer.data.entity.Stock;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;

public class StockDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Stock> getCustomerPortfolio(Long customerId) throws BankPersistenceException {
        try {
            final String query = "SELECT s FROM Stock as s WHERE s.owner.customerId = :customerId";
            TypedQuery<Stock> typedQuery = entityManager.createQuery(query, Stock.class);
            typedQuery.setParameter("customerId", customerId);
            return typedQuery.getResultList();
        } catch (NoResultException noResultException) {
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_FIND,
                    String.format("No stocks were found for customer %d", customerId), noResultException);
        } catch (Exception exception) {
            throw new BankPersistenceException(PersistenceFaultCode.SQL_ERROR, "Something went wrong!", exception);
        }
    }

    public void addShare(Stock stock) throws BankPersistenceException {
        if (stock == null)
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_INSERT, "Stock can not be null");
        entityManager.persist(stock);
    }

    public BigDecimal deleteShare(String symbol, Long customerId, int sharesAmount) throws BankPersistenceException {
        if (symbol == null || customerId == null)
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_DELETE, "Symbol and name can not be null");
        if (symbol.isBlank())
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_DELETE, "Symbol can not be empty");
        if (sharesAmount <= 0)
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_DELETE, "The amount of shares to delete must be a positive integer");
        try {
            List<Stock> customerStocks = retrieveStockOfCustomer(customerId, symbol);
            int totalSharesAmount = customerStocks.stream().mapToInt(Stock::getSharesAmount).sum();
            int sharesToSell = 0;
            if (totalSharesAmount < sharesAmount)
                throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_DELETE, String.format("Current amount of shares [%d shares] does not " +
                        "cover the amount to sell [%d shares]", totalSharesAmount, sharesAmount));
            for (int i = 0; i < customerStocks.size() && sharesAmount > 0; i++) {
                Stock currentStock = customerStocks.get(i);
                //TODO: if current stock has now 0 shares, should it be deleted?
                if (currentStock.getSharesAmount() > sharesToSell) {
                    sharesToSell = sharesAmount;
                    sharesAmount = 0;
                    currentStock.setSharesAmount(currentStock.getSharesAmount() - sharesToSell);
                    entityManager.merge(currentStock);
                } else {
                    sharesAmount = sharesAmount - currentStock.getSharesAmount();
                    sharesToSell = sharesToSell + currentStock.getSharesAmount();
                    currentStock.setSharesAmount(0);
                    entityManager.remove(currentStock);
                }
                entityManager.flush();
            }
            return customerStocks.get(0).getBuyingPrice().multiply(BigDecimal.valueOf(sharesToSell));
        } catch (NoResultException noResultException) {
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_DELETE,
                    String.format("No %s stock was found for customer %d", symbol, customerId), noResultException);
        } catch (Exception exception) {
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_DELETE,
                    "Something went wrong when trying to sell shares", exception);
        }
    }

    private List<Stock> retrieveStockOfCustomer(Long customerId, String symbol) {
        final String query = "SELECT s FROM Stock as s WHERE s.owner.customerId = :customerId AND s.symbol = :symbol ORDER BY s.sharesAmount";
        TypedQuery<Stock> typedQuery = entityManager.createQuery(query, Stock.class);
        typedQuery.setParameter("customerId", customerId).setParameter("symbol", symbol);
        return typedQuery.getResultList();
    }
}
