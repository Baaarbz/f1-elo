package com.barbzdev.f1elo.testcases

import com.barbzdev.f1elo.AcceptanceTestConfiguration
import com.barbzdev.f1elo.domain.Constructor
import com.barbzdev.f1elo.domain.ConstructorPerformance
import com.barbzdev.f1elo.domain.Season
import com.barbzdev.f1elo.domain.TheoreticalPerformance
import com.barbzdev.f1elo.factory.ConstructorFactory.aConstructor
import com.barbzdev.f1elo.factory.SeasonFactory.aSeason
import com.barbzdev.f1elo.infrastructure.jpa.JpaTheoreticalPerformanceRepository
import com.barbzdev.f1elo.infrastructure.mapper.DomainToEntityMapper.toEntity
import com.barbzdev.f1elo.infrastructure.spring.repository.jpa.constructor.JpaConstructorDatasource
import com.barbzdev.f1elo.infrastructure.spring.repository.jpa.season.JpaSeasonDatasource
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

abstract class DeleteTheoreticalPerformanceBySeasonYearShould : AcceptanceTestConfiguration() {
  @Autowired private lateinit var seasonDatasource: JpaSeasonDatasource

  @Autowired private lateinit var constructorDatasource: JpaConstructorDatasource

  @Autowired private lateinit var theoreticalPerformanceRepository: JpaTheoreticalPerformanceRepository

  @Test
  fun `add theoretical performance to the database`() {
    val aSeasonInDatabase = givenASeasonInDatabase()
    val aConstructorInDatabase = givenAConstructorInDatabase()
    givenATheoreticalPerformanceInDatabase(aSeasonInDatabase, aConstructorInDatabase)

    whenDeleteTheoreticalPerformanceRequest(aSeasonInDatabase)

    verifyTheoreticalPerformanceWasDeleted(aSeasonInDatabase)
  }

  private fun givenASeasonInDatabase(): Season = aSeason().also { seasonDatasource.save(it.toEntity()) }

  private fun givenAConstructorInDatabase(): Constructor =
    aConstructor().also { constructorDatasource.save(it.toEntity()) }

  private fun givenATheoreticalPerformanceInDatabase(
    aSeason: Season,
    aConstructor: Constructor
  ): TheoreticalPerformance {
    return TheoreticalPerformance.create(
        seasonYear = aSeason.year().value,
        isAnalyzedSeason = true,
        constructorsPerformance = listOf(ConstructorPerformance(aConstructor, 0f)),
        dataOriginUrl = "https://x.com/DeltaData_",
        dataOriginSource = "DeltaData",
      )
      .also { theoreticalPerformanceRepository.save(it) }
  }

  private fun whenDeleteTheoreticalPerformanceRequest(season: Season) {
    given()
      .port(port.toInt())
      .contentType(ContentType.JSON)
      .auth()
      .basic("local", "local")
      .`when`()
      .delete("/api/v1/theoretical-performance/${season.year().value}")
      .then()
      .statusCode(200)
  }

  private fun verifyTheoreticalPerformanceWasDeleted(season: Season) {
    val theoreticalPerformanceInDatabase = theoreticalPerformanceRepository.findBy(season.year())
    assertThat(theoreticalPerformanceInDatabase).isNull()
  }
}
