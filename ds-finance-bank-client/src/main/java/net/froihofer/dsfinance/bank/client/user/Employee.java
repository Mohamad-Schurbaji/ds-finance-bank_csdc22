package net.froihofer.dsfinance.bank.client.user;

import net.froihofer.common.data.dto.CustomerDTO;

import net.froihofer.common.exception.BankException;
import net.froihofer.common.remote.RemoteBank;
import org.fusesource.jansi.Ansi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;

import java.util.List;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class Employee extends User {
    private static final Logger log = LoggerFactory.getLogger(Employee.class);

    public Employee(BufferedReader bufferedReader, RemoteBank myBank) {
        super(bufferedReader, myBank);
    }

    private void buyShares() {
        try {
            System.err.println(ansi().eraseScreen().fg(GREEN).a(Ansi.Attribute.INTENSITY_BOLD).a("Buying Shares For a Customer").reset());
            System.err.println(ansi().fg(RED).a("Stock symbol: "));
            String symbol = bufferedReader.readLine();
            System.err.println(ansi().fg(RED).a("Amount of shares: "));
            int amount = Integer.parseInt(bufferedReader.readLine());
            System.err.println(ansi().fg(RED).a("Enter customer's id or press [s] to search for customers ids using lastname: "));
            String input = bufferedReader.readLine();
            long customerId = -1L;
            while (customerId == -1L) {
                if (input.equalsIgnoreCase("s"))
                    customerId = retrieveCustomerIdByName();
                else
                    customerId = Long.parseLong(input);
            }
            BigDecimal cost = myBank.buyStock(customerId, symbol, amount);
            System.err.println(ansi().fg(GREEN).a(String.format("%d shares were successfully bought for %f", amount, cost.doubleValue())).reset());
            log.info("{} shares were bought for {}", amount, cost);
        } catch (BankException e) {
            System.err.println(ansi().fg(RED).a(Ansi.Attribute.ITALIC).a("Was not able to buy your stocks! see logs for more information").reset());
            log.error("Could not buy stocks", e);
        } catch (Exception e) {
            System.err.println(ansi().fg(RED).a(Ansi.Attribute.ITALIC).a("Something went wrong while buying stocks! see logs for more information").reset());
            log.error("Something went wrong while buying stocks", e);
        }
    }

    private Long retrieveCustomerIdByName() {
        try {
            System.err.println(ansi().fg(RED).a("Customer's lastname: ").fg(MAGENTA));
            String lastname = bufferedReader.readLine();
            List<CustomerDTO> customers = myBank.findCustomerByLastName(lastname);
            if (customers != null && !customers.isEmpty()){
                final String leftAlignFormat = "| %-15d | %-15s | %-15s |%n";
                System.err.printf("+-----------------+-----------------+-----------------+%n");
                System.err.printf("|   Customer ID   |    First name   |    Last name    |%n");
                System.err.printf("+-----------------+-----------------+-----------------+%n");
                for (CustomerDTO customer : customers) {
                    System.err.printf(leftAlignFormat, customer.getCustomerId(), customer.getFirstName(), customer.getLastName());
                    System.err.printf("+-----------------+-----------------+-----------------+%n");
                }
            }else {
                System.err.println(ansi().fg(RED).a("No entries were found for  " + lastname).reset());
            }
            System.err.println(ansi().fg(RED).a("\nCustomer's id: ").reset());
            return Long.parseLong(bufferedReader.readLine());
        } catch (BankException e) {
            log.error("Could not retrieve customer's lastname", e);
            System.err.println(ansi().fg(RED).a("Could not retrieve customer's lastname").reset());
        } catch (IOException ex) {
            log.error("Could not read input from user", ex);
            System.err.println(ansi().fg(RED).a("Could not read the input ..  pleas retry again!").reset());
        }
        return -1L;
    }

    private void addCustomer() {
        try {
            System.err.println(ansi().eraseScreen().fg(GREEN).a(Ansi.Attribute.INTENSITY_BOLD).a("Adding a new customer").reset());
            System.err.println(ansi().fg(RED).a("First name: "));
            String firstName = bufferedReader.readLine();
            System.err.println(ansi().fg(RED).a("Last name: "));
            String lastName = bufferedReader.readLine();
            System.err.println(ansi().fg(RED).a("Address: "));
            String address = bufferedReader.readLine();
            System.err.println(ansi().fg(RED).a("Password: "));
            String password = bufferedReader.readLine();
            Long customerId = myBank.addCustomer(firstName, lastName, address, password);
            if (customerId != null && customerId > -1) {
                System.err.println(ansi().fg(GREEN).a(String.format("%s %s was successfully added!", firstName, lastName)).reset());
                System.err.println(ansi().fg(GREEN).a(String.format("The ID which can be used for logging in is: %d", customerId)).reset());
            }
        } catch (IOException e) {
            log.error("Could not read input from user", e);
            System.err.println(ansi().fg(RED).a("Could not read the input ..  pleas retry again!").reset());
        } catch (BankException ex) {
            log.error("Could not add a new customer.", ex);
            System.err.println(ansi().fg(RED).a("Customer could not be added!\n" + ex.getMessage()).reset());
        }
    }

    @Override
    public void run() {
        boolean exit = false;
        try {
            System.err.println(ansi().fg(GREEN).a(Ansi.Attribute.INTENSITY_BOLD).a("\n\tWelcome to FH Bank System\n").reset());
            while (!exit) {
                System.err.println(ansi().eraseScreen().fg(GREEN).a(Ansi.Attribute.INTENSITY_BOLD).a("\n\nEmployee Menu").reset());
                System.err.println(ansi().eraseScreen().fg(CYAN).a("Press 0 to exit").reset());
                System.err.println(ansi().fg(CYAN).a("Press 1 to add customer").reset());
                System.err.println(ansi().fg(CYAN).a("Press 2 to search stocks").reset());
                System.err.println(ansi().fg(CYAN).a("Press 3 to buy shares").reset());

                String input = bufferedReader.readLine();
                switch (input) {
                    case "0":
                        exit = true;
                        break;
                    case "1":
                        addCustomer();
                        break;
                    case "2":
                        searchStocks();
                        break;
                    case "3":
                        buyShares();
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
