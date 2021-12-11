package net.froihofer.common.exception;

public class BankException extends Exception{

    public BankException(String message){
        super(message);
    }
    public BankException(String message, Throwable cause){
        super(message, cause);
    }
}
