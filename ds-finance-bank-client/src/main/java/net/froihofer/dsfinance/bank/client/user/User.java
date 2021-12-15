package net.froihofer.dsfinance.bank.client.user;

import net.froihofer.common.data.dto.StockDTO;
import net.froihofer.common.exception.BankException;
import net.froihofer.common.remote.RemoteBank;
import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.ansi;

public abstract class User {
    private static final Logger log = LoggerFactory.getLogger(User.class);
    protected BufferedReader bufferedReader;
    protected RemoteBank myBank;

    protected User(BufferedReader bufferedReader, RemoteBank myBank){
        this.bufferedReader = bufferedReader;
        this.myBank = myBank;
    }

    public abstract void run();

    protected void searchStocks() {
        try {
            System.err.println(ansi().eraseScreen().fg(GREEN).a(Ansi.Attribute.INTENSITY_BOLD).a("Searching Stock by Company Name").reset());
            System.err.println(ansi().fg(RED).a("Company name: ").fg(MAGENTA));
            String companyName = bufferedReader.readLine();
            List<StockDTO> stocks = myBank.findStocksByCompany(companyName);
            if (stocks.isEmpty())
                System.err.println(ansi().fg(RED).a("No stocks were found for  " + companyName));
            else {
                //stock.getLastTradeTime().format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm"))
                final String leftAlignFormat = "| %-38s | %-15s | €%-20s |%n";
                System.err.printf("+----------------------------------------+-----------------+-----------------------+%n");
                System.err.printf("|              Company Name              |   Stock Symbol  |     Trading Price     |%n");
                System.err.printf("+----------------------------------------+-----------------+-----------------------+%n");
                for (StockDTO stock : stocks) {
                    System.err.printf(leftAlignFormat, stock.getCompanyName(), stock.getSymbol(), stock.getCurrentTradingPrice().doubleValue());
                    System.err.printf("+----------------------------------------+-----------------+-----------------------+%n");
                }
            }
        } catch (IOException e) {
            log.error("Could not read input from user", e);
            System.err.println(ansi().fg(RED).a("Could not read the input ..  pleas retry again!").reset());
        } catch (BankException ex) {
            log.error("Could not search for stocks", ex);
            System.err.println(ansi().fg(RED).a(ex.getLocalizedMessage()).reset());
        } catch (Exception exception) {
            log.error("Could not search for stocks", exception);
            System.err.println(ansi().fg(RED).a("Something went wrong! check logs for more info.").reset());
        }
    }
}
