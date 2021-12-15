package net.froihofer.common.data.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockDTO implements Serializable {
    private Long stockId;
    private String companyName;
    private String symbol;
    private String stockExchange;
    private int sharesAmount;
    private BigDecimal buyingPrice;
    private BigDecimal currentTradingPrice;
    private LocalDateTime lastTradeTime;
    private CustomerDTO owner;

    public StockDTO() {
    }

    public StockDTO(String companyName, String symbol, String stockExchange,
                    BigDecimal currentTradingPrice,
                    LocalDateTime lastTradeTime) {
        this.companyName = companyName;
        this.symbol = symbol;
        this.stockExchange = stockExchange;
        this.currentTradingPrice = currentTradingPrice;
        this.lastTradeTime = lastTradeTime;
    }

    public StockDTO(Long stockId, String companyName, String symbol, String stockExchange,
                    int sharesAmount, BigDecimal buyingPrice, BigDecimal currentTradingPrice,
                    LocalDateTime lastTradeTime, CustomerDTO owner) {
        this.stockId = stockId;
        this.companyName = companyName;
        this.symbol = symbol;
        this.stockExchange = stockExchange;
        this.sharesAmount = sharesAmount;
        this.buyingPrice = buyingPrice;
        this.currentTradingPrice = currentTradingPrice;
        this.lastTradeTime = lastTradeTime;
        this.owner = owner;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    public int getSharesAmount() {
        return sharesAmount;
    }

    public void setSharesAmount(int sharesAmount) {
        this.sharesAmount = sharesAmount;
    }

    public BigDecimal getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(BigDecimal buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public BigDecimal getCurrentTradingPrice() {
        return currentTradingPrice;
    }

    public void setCurrentTradingPrice(BigDecimal currentTradingPrice) {
        this.currentTradingPrice = currentTradingPrice;
    }

    public LocalDateTime getLastTradeTime() {
        return lastTradeTime;
    }

    public void setLastTradeTime(LocalDateTime lastTradeTime) {
        this.lastTradeTime = lastTradeTime;
    }

    public CustomerDTO getOwner() {
        return owner;
    }

    public void setOwner(CustomerDTO owner) {
        this.owner = owner;
    }
}
