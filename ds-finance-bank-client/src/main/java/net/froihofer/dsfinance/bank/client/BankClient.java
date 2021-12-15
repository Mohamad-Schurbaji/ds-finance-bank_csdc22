package net.froihofer.dsfinance.bank.client;

import net.froihofer.common.data.UserRole;
import net.froihofer.common.exception.BankException;
import net.froihofer.common.remote.RemoteBank;
import net.froihofer.dsfinance.bank.client.user.Customer;
import net.froihofer.dsfinance.bank.client.user.Employee;
import net.froihofer.dsfinance.bank.client.user.User;
import net.froihofer.util.AuthCallbackHandler;
import net.froihofer.util.JBoss7JndiLookupHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.*;
import java.util.Properties;

import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Main class for starting the bank client.
 */
public class BankClient {
    //TODO: Use lanterna for the GUI
    //+ https://github.com/mabe02/lanterna
    private static final Logger log = LoggerFactory.getLogger(BankClient.class);

    public static void main(String[] args) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.err.println(ansi().eraseScreen().fg(RED).a("Enter yor customer's id: ").reset());
            String username = bufferedReader.readLine();
            log.debug("Username: {} ", username);
            System.err.println(ansi().fg(RED).a("Enter yor password: ").reset());
            String password = bufferedReader.readLine();
            log.debug("Password: {}", password);
            AuthCallbackHandler.setUsername(username);
            AuthCallbackHandler.setPassword(password);
            Properties props = new Properties();
            props.put(Context.SECURITY_PRINCIPAL, AuthCallbackHandler.getUsername());
            props.put(Context.SECURITY_CREDENTIALS, AuthCallbackHandler.getPassword());
            JBoss7JndiLookupHelper jndiHelper = new JBoss7JndiLookupHelper(new InitialContext(props), "ds-finance-bank-ear", "ds-finance-bank-ejb", "");
            RemoteBank myBank = jndiHelper.lookupUsingJBossEjbClient("MyBank", RemoteBank.class, true);
            log.trace("Bank instance was loaded {}", myBank);
            User user = myBank.retrieveUserRole() == UserRole.CUSTOMER ?
                    new Customer(bufferedReader, myBank) :
                    new Employee(bufferedReader, myBank);
            user.run();
        } catch (IOException e) {
            System.err.println(ansi().fg(RED).a("Failed to read input\n" + e.getMessage()).reset());
            log.error("Failed to read input", e);
        } catch (NamingException e) {
            System.err.println(ansi().fg(RED).a("Failed to initialize InitialContext\n" + e.getMessage()).reset());
            log.error("Failed to initialize InitialContext.", e);
        } catch (BankException e) {
            System.err.println(ansi().fg(RED).a("Could not retrieve user role from bank!\n" + e.getMessage()).reset());
            log.error("Could not retrieve user role from bank", e);
        }catch (Exception e){
            System.err.println(ansi().fg(RED).a("Could not log in! check your credentials\n" + e.getMessage() + "\n\n\n").reset());
            log.error("Could not log in!", e);
            main(null);
        }
    }
}
