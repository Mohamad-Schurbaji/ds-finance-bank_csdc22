package net.froihofer.data;

public class BankPersistenceException extends Exception {
    private static final long serialVersionUID = 849019531212010274L;
    private final PersistenceFaultCode faultCode;

    public BankPersistenceException(PersistenceFaultCode faultCode) {
        this.faultCode = faultCode;
    }

    public BankPersistenceException(PersistenceFaultCode faultCode, String msg) {
        super(msg);
        this.faultCode = faultCode;
    }

    public BankPersistenceException(PersistenceFaultCode faultCode, String msg, Throwable cause) {
        super(msg, cause);
        this.faultCode = faultCode;
    }

    public PersistenceFaultCode getFaultCode() {
        return faultCode;
    }
}