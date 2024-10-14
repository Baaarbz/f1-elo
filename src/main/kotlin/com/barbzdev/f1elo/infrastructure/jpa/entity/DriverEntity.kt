package com.barbzdev.f1elo.infrastructure.jpa.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "drivers")
data class DriverEntity(
  @Id
  val id: UUID,
  @Column(name = "full_name")
  val fullName: String,
  val code: String?,
  @Column(name = "permanent_number")
  val permanentNumber: String?,
  @Column(name = "birth_date")
  val birthDate: LocalDate,
  val nationality: String,
  @Column(name = "info_url")
  val infoUrl: String,
  @Column(name = "current_elo")
  val currentElo: Int,
  @Column(name = "current_elo_occurred_on")
  val currentEloOccurredOn: LocalDate,
  @OneToMany(mappedBy = "driver", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
  val eloHistory: List<DriverEloHistoryEntity> = emptyList()
)
