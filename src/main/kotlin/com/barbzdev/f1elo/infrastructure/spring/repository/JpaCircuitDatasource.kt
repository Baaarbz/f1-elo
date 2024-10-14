package com.barbzdev.f1elo.infrastructure.spring.repository

import com.barbzdev.f1elo.infrastructure.jpa.entity.CircuitEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaCircuitDatasource : JpaRepository<CircuitEntity, UUID>
