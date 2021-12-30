package net.froihofer.dsfinance.bank.client.user;


import net.froihofer.common.data.dto.CustomerDTO;
import net.froihofer.common.data.dto.StockDTO;
import net.froihofer.common.exception.BankException;
import net.froihofer.common.remote.RemoteBank;
import net.froihofer.util.AuthCallbackHandler;
import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.ansi;

public class Customer extends User {
    private static final Logger log = LoggerFactory.getLogger(Customer.class);

    public Customer(BufferedReader bufferedReader,  RemoteBank myBank) {
        super(bufferedReader, myBank);
    }

    private void buyShares() {
        try {
            System.err.println(ansi().fg(GREEN).eraseScreen().a(Ansi.Attribute.INTENSITY_BOLD).a("Buying Shares").reset());
            System.err.println(ansi().fg(RED).a("Stock symbol: ").reset());
            String symbol = bufferedReader.readLine();
            System.err.println(ansi().fg(RED).a("Amount of shares: ").reset());
            int amount = Integer.parseInt(bufferedReader.readLine());
            BigDecimal cost = myBank.buyStock(Long.parseLong(AuthCallbackHandler.getUsername()), symbol, amount);
            System.err.println(ansi().fg(GREEN).a(String.format("%d shares were successfully bought for %f", amount, cost.doubleValue())).reset());
            log.info("{} shares were bought for {}", amount, cost);
        } catch (BankException e) {
            log.error("Could not buy stocks", e);
            System.err.print(ansi().fg(RED).a(Ansi.Attribute.ITALIC).a("Was not able to buy your stocks! see logs for more information").reset());
        } catch (Exception e) {
            log.error("Something went wrong while buying stocks", e);
            System.err.println(ansi().fg(RED).a(Ansi.Attribute.ITALIC).a("Something went wrong while buying stocks! see logs for more information").reset());
        }
    }

    private void sellShares() {
        try {
            System.err.println(ansi().fg(GREEN).eraseScreen().a(Ansi.Attribute.INTENSITY_BOLD).a("Selling Shares").reset());
            System.err.println(ansi().fg(RED).a("Stock symbol: ").reset());
            String symbol = bufferedReader.readLine();
            List<StockDTO> stocks = myBank.listCustomerStockPortfolio(Long.parseLong(AuthCallbackHandler.getUsername()));
            if (stocks == null || stocks.isEmpty()){
                System.err.println(ansi().fg(RED).a(Ansi.Attribute.ITALIC).a("Your portfolio is empty.").reset());
                return;
            }
            List<StockDTO> stockToSell = stocks.stream()
                            .filter(stock -> stock.getSymbol().equalsIgnoreCase(symbol))
                                    .collect(Collectors.toList());
            if (stockToSell.isEmpty()){
                System.err.println(ansi().fg(RED).a(Ansi.Attribute.ITALIC).a("You do not own shares of " + symbol).reset());
                return;
            }
            System.err.println(ansi().fg(RED).a("Amount of shares: ").reset());
            int amount = Integer.parseInt(bufferedReader.readLine());
            BigDecimal cost = myBank.sellStock(Long.parseLong(AuthCallbackHandler.getUsername()), symbol, amount);
            System.err.println(ansi().fg(GREEN).a(String.format("%d shares were successfully bought for %f", amount, cost.doubleValue())).reset());
            log.info("{} shares were bought for {}", amount, cost);
        } catch (BankException e) {
            log.error("Could not buy stocks", e);
            System.err.print(ansi().fg(RED).a(Ansi.Attribute.ITALIC).a("Was not able to buy your stocks! see logs for more information").reset());
        } catch (Exception e) {
            log.error("Something went wrong while buying stocks", e);
            System.err.println(ansi().fg(RED).a(Ansi.Attribute.ITALIC).a("Something went wrong while buying stocks! see logs for more information").reset());
        }
    }

    private void listPortfolio(){
        try {
            List<StockDTO> stocks = myBank.listCustomerStockPortfolio(Long.parseLong(AuthCallbackHandler.getUsername()));
            if (stocks == null || stocks.isEmpty()){
                System.err.println(ansi().fg(RED).a(Ansi.Attribute.ITALIC).a("Your portfolio is empty.").reset());
                return;
            }
            System.err.println(ansi().fg(GREEN).eraseScreen().a(Ansi.Attribute.INTENSITY_BOLD).a("Stocks Portfolio\n").reset());
            final String leftAlignFormat = "| %-38s | %-15s | %-11d | €%-17f | €%-16f |%n";
            System.err.printf("+----------------------------------------+-----------------+-------------+-------------------+------------------+%n");
            System.err.printf("|              Company Name              |   Stock Symbol  |    Amount   |    Share Price    |    Total Price   |%n");
            System.err.printf("+----------------------------------------+-----------------+-------------+-------------------+------------------+%n");
            int sharesAmount = 0;
            double sharePrice = 0;
            for (StockDTO stock : stocks) {
                sharesAmount = stock.getSharesAmount();
                sharePrice = stock.getCurrentTradingPrice().doubleValue();
                System.err.printf(leftAlignFormat, stock.getCompanyName(), stock.getSymbol(), sharesAmount , sharePrice, sharesAmount*sharePrice);
                System.err.printf("+----------------------------------------+-----------------+-------------+-------------------+------------------+%n");
            }

        } catch (BankException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        boolean exit = false;
        try {
            System.err.println(ansi().fg(GREEN).a(Ansi.Attribute.INTENSITY_BOLD).a("\n\tWelcome to FH Bank System\n").reset());
            while (!exit) {
                System.err.println(ansi().eraseScreen().fg(GREEN).a(Ansi.Attribute.INTENSITY_BOLD).a("\n\nCustomer Menu").reset());
                System.err.println(ansi().eraseScreen().fg(CYAN).a("Press 0 to exit").reset());
                System.err.println(ansi().fg(CYAN).a("Press 1 to buy shares").reset());
                System.err.println(ansi().fg(CYAN).a("Press 2 to sell shares").reset());
                System.err.println(ansi().fg(CYAN).a("Press 3 to search stocks").reset());
                System.err.println(ansi().fg(CYAN).a("Press 4 to list your portfolio").reset());
                String input = bufferedReader.readLine();
                switch (input) {
                    case "0":
                        exit = true;
                        break;
                    case "1":
                        buyShares();
                        break;
                    case "2":
                        searchStocks();
                        break;
                    case "3":
                        listPortfolio();
                        break;
                    default:
                        System.err.println(ansi().fg(RED).a("Wrong input!").reset());
                }
            }
        } catch (IOException ex) {
            log.error("Could not read input!", ex);
        }
    }
}
