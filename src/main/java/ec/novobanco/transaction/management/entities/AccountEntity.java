package ec.novobanco.transaction.management.entities;

import ec.novobanco.transaction.management.enumerables.AccountStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ColumnDefault("NEXT VALUE FOR [account_number_seq]")
    @Column(name = "account_number", nullable = false, insertable = false, updatable = false)
    private Long accountNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Size(max = 10)
    @NotNull
    @Nationalized
    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Size(max = 3)
    @NotNull
    @Nationalized
    @ColumnDefault("'USD'")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @NotNull
    @ColumnDefault("0.0000")
    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status;

    @NotNull
    @ColumnDefault("sysutcdatetime()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("sysutcdatetime()")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}