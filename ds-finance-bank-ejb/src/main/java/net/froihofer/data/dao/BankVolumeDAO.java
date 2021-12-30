package net.froihofer.data.dao;

import net.froihofer.data.BankPersistenceException;
import net.froihofer.data.PersistenceFaultCode;
import net.froihofer.data.entity.BankVolume;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

public class BankVolumeDAO {
    private static final Long MAX_VOLUME = 10000000000L;
    private static final Long BANK_VOLUME_ID = BankVolume.DEFAULT_BANK_VOLUME_ID;
    private BankVolume bankVolume;

    @PersistenceContext
    private EntityManager entityManager;

    public BigDecimal retrieveBankVolume() throws BankPersistenceException {
        if (bankVolume != null)
            return bankVolume.getVolume();
        else
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_FIND, "The bank volume is not initialized and does not exist");
    }

    public BigDecimal increaseVolume(BigDecimal amount) throws BankPersistenceException {
        if (amount.compareTo(BigDecimal.valueOf(0)) <= 0)
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_UPDATE, "Amount to increase must be a positive number!");
        try {
            BigDecimal newVolume = bankVolume.getVolume().add(amount);
            if (newVolume.compareTo(BigDecimal.valueOf(MAX_VOLUME)) > 0) {
                throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_UPDATE,
                        String.format("Can not increase the bank volume. The passed amount [%f] would " +
                                        "exceed the maximum allowed volume of [%d] if added to the current bank volume [%f]",
                                amount.doubleValue(), MAX_VOLUME, bankVolume.getVolume().doubleValue()));
            }
            bankVolume.setVolume(newVolume);
            return newVolume;
        } catch (Exception exception) {
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_UPDATE,
                    "Bank volume was not able to be increased!", exception);
        }
    }

    public BigDecimal decreaseVolume(BigDecimal amount) throws BankPersistenceException {
        if (amount.compareTo(BigDecimal.valueOf(0)) <= 0)
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_UPDATE, "Amount to decrease must be a positive number!");
        try {
            BigDecimal newVolume = bankVolume.getVolume().subtract(amount);
            if (newVolume.compareTo(BigDecimal.valueOf(MAX_VOLUME)) < 0) {
                throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_UPDATE,
                        String.format("Can not decrease the bank volume. The passed amount [%f] would " +
                                        "be negative, if subtracted from the current bank volume [%f]",
                                amount.doubleValue(), bankVolume.getVolume().doubleValue()));
            }
            bankVolume.setVolume(newVolume);
            return newVolume;
        } catch (Exception exception) {
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_UPDATE,
                    "Bank volume was not able to be decreased!", exception);
        }
    }

    public void initBankVolume() {
        bankVolume = entityManager.find(BankVolume.class, BANK_VOLUME_ID);
        if (bankVolume == null) { // No bank volume entry in the db
            bankVolume = new BankVolume();
            entityManager.persist(bankVolume);
            entityManager.flush();
        }
    }

}
