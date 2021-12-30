package net.froihofer.dsfinance.bank.client;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Pattern;

public class ClientGui {
    private static final Logger log = LoggerFactory.getLogger(ClientGui.class);

    public static void main(String[] args) {
        try(Terminal terminal = new DefaultTerminalFactory().createTerminal()) {
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            //panel
            Panel panel = new Panel();
            panel.setFillColorOverride(TextColor.ANSI.BLACK);

            GridLayout credentialsForm = new GridLayout(2);
            credentialsForm.setVerticalSpacing(2);
            credentialsForm.setRightMarginSize(0);
            panel.setLayoutManager(credentialsForm);

            final Label outputLabel = new Label("");

            Label userNameLabel = new Label("Username");
            userNameLabel.setBackgroundColor(TextColor.ANSI.BLACK);
            userNameLabel.setForegroundColor(TextColor.ANSI.CYAN);
            panel.addComponent(userNameLabel);
            TextBox textBox = new TextBox();
            textBox.setRenderer(new ComponentRenderer<TextBox>() {
                @Override
                public TerminalSize getPreferredSize(TextBox textBox) {
                    return null;
                }

                @Override
                public void drawComponent(TextGUIGraphics textGUIGraphics, TextBox textBox) {

                }
            });
            final TextBox usernameText = new TextBox().addTo(panel);

            panel.addComponent(new Label("Password"));
            final TextBox passwordText = new TextBox().addTo(panel);

            panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
            new Button("Login", new Runnable() {
                @Override
                public void run() {
                    String username = usernameText.getText();
                    String password = passwordText.getText();
                    User user = authorizeUser(username, password);
                    if (user == null){
                        outputLabel.setText("Wrong credentials!");
                    }else {
                        outputLabel.setText("Logged in successfully");
                    }
                }
            }).addTo(panel);

            panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
            panel.addComponent(outputLabel);

            // Create window to hold the panel
            BasicWindow window = new BasicWindow();
            window.setComponent(panel);

            // Create gui and start gui
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.CYAN));

            gui.addWindowAndWait(window);
        }catch (IOException exception){
            log.error("could not start GUI", exception);
        }
    }


    @SuppressWarnings("unchecked")
    private static <T extends User> T authorizeUser(String username, String password){
        try {
            AuthCallbackHandler.setUsername(username);
            AuthCallbackHandler.setPassword(password);
            Properties props = new Properties();
            props.put(Context.SECURITY_PRINCIPAL, AuthCallbackHandler.getUsername());
            props.put(Context.SECURITY_CREDENTIALS, AuthCallbackHandler.getPassword());
            JBoss7JndiLookupHelper jndiHelper = new JBoss7JndiLookupHelper(new InitialContext(props), "ds-finance-bank-ear", "ds-finance-bank-ejb", "");
            RemoteBank myBank = jndiHelper.lookupUsingJBossEjbClient("MyBank", RemoteBank.class, true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            return myBank.retrieveUserRole() == UserRole.CUSTOMER ?
                    (T) new Customer(bufferedReader, myBank) :
                    (T) new Employee(bufferedReader, myBank);
        } catch (Exception e) {
            return null;
        }
    }
}
