package net.froihofer.dsfinance.bank.client;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import net.froihofer.util.AuthCallbackHandler;
import net.froihofer.util.JBoss7JndiLookupHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for starting the bank client.
 *
 */
public class BankClient {
  private static Logger log = LoggerFactory.getLogger(BankClient.class);
  
  public static void main(String[] args) {
    AuthCallbackHandler.setUsername("customer");
    AuthCallbackHandler.setPassword("customerpass");
    Properties props = new Properties();
    props.put(Context.SECURITY_PRINCIPAL,AuthCallbackHandler.getUsername());
    props.put(Context.SECURITY_CREDENTIALS,AuthCallbackHandler.getPassword());
    try {
      JBoss7JndiLookupHelper jndiHelper = new JBoss7JndiLookupHelper(new InitialContext(props), "ds-finance-bank-ear", "ds-finance-bank-ejb", "");
      //TODO: Implement the rest of the functionality
    }
    catch (NamingException e) {
      log.error("Failed to initialize InitialContext.",e);
    }
  }
}
