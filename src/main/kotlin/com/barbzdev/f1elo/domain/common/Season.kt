package com.barbzdev.f1elo.domain.common

data class Season(val value: Int) {
  init {
    require(value >= 1950) { "Season must be greater or equals than 1950, year were Formula 1 officially starts" }
  }
}