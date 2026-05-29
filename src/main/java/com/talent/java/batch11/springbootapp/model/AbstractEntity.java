package com.talent.java.batch11.springbootapp.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
abstract  class AbstractEntity {

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private String createdBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    private String updatedBy;

    private LocalDateTime deletedAt;
    private String deletedBy;

}