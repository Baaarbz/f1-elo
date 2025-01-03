package com.barbzdev.f1elo.infrastructure.spring.configuration

import com.barbzdev.f1elo.domain.service.EloCalculator
import com.barbzdev.f1elo.domain.service.IRatingCalculator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration {
  @Bean fun eloCalculator() = EloCalculator()

  @Bean fun iRatingCalculator() = IRatingCalculator()
}
