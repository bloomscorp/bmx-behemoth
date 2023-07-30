package com.bloomscorp.behemoth.orm;

import com.bloomscorp.behemoth.contract.BehemothContract;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@MappedSuperclass
public class BehemothORM {

    @Id
    @Column(
            name = BehemothContract.ID,
            columnDefinition = "BIGSERIAL",
            nullable = false
    )
    @SequenceGenerator(
            name = "behemoth_id_sequence",
            sequenceName = "behemoth_id_sequence",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "behemoth_id_sequence"
    )
    public Long id;

    @Version
    @Setter(AccessLevel.PROTECTED)
    @Column(
            name = BehemothContract.VERSION,
            columnDefinition = "BIGSERIAL",
            nullable = false
    )
    public Long version;
}
