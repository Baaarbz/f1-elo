package com.barbzdev.f1elo.domain.repository

import com.barbzdev.f1elo.domain.Season
import com.barbzdev.f1elo.domain.SeasonYear

interface SeasonRepository {
  fun getLastSeasonLoaded(): Season?

  fun save(season: Season)

  fun findBy(year: SeasonYear): Season?
}
