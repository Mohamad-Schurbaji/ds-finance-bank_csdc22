package net.froihofer.data.entity;

import javax.enterprise.inject.Default;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "bank_volume")
public class BankVolume {
    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal volume = BigDecimal.valueOf(10000000000L);

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
