package net.froihofer.dsfinance.bank.client.user;


import net.froihofer.common.exception.BankException;
import net.froihofer.common.remote.RemoteBank;
import net.froihofer.util.AuthCallbackHandler;
import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;


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

    @Override
    public void run() {
        boolean exit = false;
        try {
            while (!exit) {
                System.err.println(ansi().eraseScreen().fg(GREEN).a(Ansi.Attribute.INTENSITY_BOLD).a("\n\nCustomer Menu").reset());
                System.err.println(ansi().eraseScreen().fg(CYAN).a("Press 0 to exit").reset());
                System.err.println(ansi().fg(CYAN).a("Press 1 to buy shares").reset());
                System.err.println(ansi().fg(CYAN).a("Press 2 to search stocks").reset());
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
                    default:
                        System.err.println(ansi().fg(RED).a("Wrong input!").reset());
                }
            }
        } catch (IOException ex) {
            log.error("Could not read input!", ex);
        }
    }
}
