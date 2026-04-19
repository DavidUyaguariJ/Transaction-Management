package ec.novobanco.transaction.management.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 150)
    @NotNull
    @Nationalized
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Size(max = 150)
    @NotNull
    @Nationalized
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Size(max = 20)
    @NotNull
    @Nationalized
    @Column(name = "document_id", nullable = false, length = 20)
    private String documentId;

    @NotNull
    @ColumnDefault("sysutcdatetime()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}