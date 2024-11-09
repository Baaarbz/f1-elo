package com.barbzdev.f1elo.infrastructure.com.barbzdev.f1elo

import com.barbzdev.f1elo.helper.DockerComposeHelper
import com.barbzdev.f1elo.testcases.CalculateEloOfDriversBySeasonShould
import org.junit.jupiter.api.Nested
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class ApplicationAcceptanceTest {

  companion object {
    @Container val dockerContainer = DockerComposeHelper.create()
  }

  @Nested inner class CalculateEloOfDriversBySeason : CalculateEloOfDriversBySeasonShould()
}