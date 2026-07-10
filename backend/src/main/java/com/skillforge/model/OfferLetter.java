package com.skillforge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "offer_letters", indexes = {
        @Index(name = "idx_offer_letters_student", columnList = "student_id"),
        @Index(name = "idx_offer_letters_job", columnList = "job_id")
})
@Getter
@Setter
@NoArgsConstructor
public class OfferLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "offer_title", nullable = false)
    private String offerTitle;

    @Column(name = "offer_description", columnDefinition = "TEXT")
    private String offerDescription;

    @Column(name = "salary_amount")
    private BigDecimal salaryAmount;

    @Column(name = "joining_date")
    private LocalDateTime joiningDate;

    @Column(name = "offer_file_path")
    private String offerFilePath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}