package com.barbzdev.f1elo.infrastructure.jpa

import com.barbzdev.f1elo.domain.Driver
import com.barbzdev.f1elo.domain.DriverId
import com.barbzdev.f1elo.domain.repository.DriverRepository
import com.barbzdev.f1elo.infrastructure.mapper.DomainToEntityMapper.toEntity
import com.barbzdev.f1elo.infrastructure.mapper.EntityToDomainMapper.toDomain
import com.barbzdev.f1elo.infrastructure.spring.repository.jpa.JpaDriverDatasource
import com.barbzdev.f1elo.infrastructure.spring.repository.jpa.JpaDriverEloHistoryDatasource
import kotlin.jvm.optionals.getOrNull

class JpaDriverRepository(
  private val driverDatasource: JpaDriverDatasource,
  private val eloHistoryDatasource: JpaDriverEloHistoryDatasource
) : DriverRepository {
  override fun findBy(id: DriverId): Driver? =
    driverDatasource.findById(id.value).getOrNull()?.let {
      val eloRecordEntity = eloHistoryDatasource.findAllByDriver(it)
      it.toDomain(eloRecordEntity)
    }

  override fun save(driver: Driver) {
    driverDatasource.save(driver.toEntity())
    eloHistoryDatasource.saveAll(driver.eloRecord().map { it.toEntity(driver) })
  }
}