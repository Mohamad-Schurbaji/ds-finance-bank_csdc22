package net.froihofer.data.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id", nullable = false)
    private Long stockId;


    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "stock_exchange", nullable = false)
    private String stockExchange;

    @Column(name = "shares_quantity", nullable = false)
    private int sharesAmount;

    //TODO: are the prices for all the shares or only for one share of a specific stock
    @Column(name = "buying_price", nullable = false)
    private BigDecimal buyingPrice;

    @Transient
    private BigDecimal currentTradingPrice;

    @Column(name = "last_trade_time", nullable = false)
    private LocalDateTime lastTradeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_customer_id")
    private Customer owner;

    public Stock() {
    }

    public Stock(Long stockId, String companyName, String symbol, String stockExchange,
                 int sharesAmount, BigDecimal buyingPrice, BigDecimal currentTradingPrice,
                 LocalDateTime lastTradeTime, Customer owner) {
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


    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public Long getStockId() {
        return stockId;
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
}
