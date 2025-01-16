package com.technicaltest.bankingapp.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder(toBuilder = true)
public abstract class Entity {

    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
}
