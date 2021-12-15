package net.froihofer.dsfinance.bank.client;

import org.fusesource.jansi.Ansi;

import java.io.*;

public class Test {

    public static final String EXIT_COMMAND = "exit";

    public static void main(final String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintStream printStream = System.err;
        printStream.println("Enter some text, or '" + EXIT_COMMAND + "' to quit");
        printStream.println("hallo");
        printStream.println("mohamd");
        printStream.println("hallo");
        printStream.println("momo");
        printStream.println("hallo");
        printStream.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("Press 0 to exit").reset());
        printStream.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("Press 1 to buy shares").reset());
/*        while (true) {
            printStream.println(Ansi.ansi().fg(Ansi.Color.CYAN).a(">>>>> "));
            String input = br.readLine();
            printStream.println(input);

            if (input.length() == EXIT_COMMAND.length() && input.toLowerCase().equals(EXIT_COMMAND)) {
                System.out.println("Exiting.");
                return;
            }
            printStream.println("...response goes here...");
        }*/
    }
}
