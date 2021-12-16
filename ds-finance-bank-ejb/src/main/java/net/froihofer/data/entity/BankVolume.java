package net.froihofer.data.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "bank_volume")
public class BankVolume implements Serializable {
    @Transient
    public static final Long DEFAULT_BANK_VOLUME_ID = 1L;
    @Id
    private Long id = DEFAULT_BANK_VOLUME_ID;
    private BigDecimal volume = BigDecimal.valueOf(10000000000L);

    public BankVolume(){}

    public BankVolume(BankVolume bankVolume){
        this.id = bankVolume.getId();
        this.volume = bankVolume.getVolume();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
}
