package com.barbzdev.f1elo.domain

import com.barbzdev.f1elo.domain.common.InfoUrl
import com.barbzdev.f1elo.domain.common.OccurredOn
import com.barbzdev.f1elo.domain.common.Season

class Race private constructor(
  private val season: Season,
  private val round: Round,
  private val infoUrl: InfoUrl,
  private val name: RaceName,
  private val circuit: Circuit,
  private val occurredOn: RaceDate,
  private val results: List<RaceResult>
) {

  fun season() = season

  fun round() = round

  fun infoUrl() = infoUrl

  fun name() = name

  fun circuit() = circuit

  fun occurredOn() = occurredOn

  fun results() = results

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Race

    if (season != other.season) return false
    if (round != other.round) return false
    if (infoUrl != other.infoUrl) return false
    if (name != other.name) return false
    if (circuit != other.circuit) return false
    if (occurredOn != other.occurredOn) return false
    if (results != other.results) return false

    return true
  }

  override fun hashCode(): Int {
    var result = season.hashCode()
    result = 31 * result + round.hashCode()
    result = 31 * result + infoUrl.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + circuit.hashCode()
    result = 31 * result + occurredOn.hashCode()
    result = 31 * result + results.hashCode()
    return result
  }

  override fun toString(): String =
    "Race(season=$season, round=$round, infoUrl=$infoUrl, name=$name, circuit=$circuit, occurredOn=$occurredOn, results=$results)"

  companion object {
    fun create(
      season: Int,
      round: Int,
      infoUrl: String,
      name: String,
      circuit: Circuit,
      occurredOn: String,
      results: List<RaceResult>
    ) = Race(
      season = Season(season),
      round = Round(round),
      infoUrl = InfoUrl(infoUrl),
      name = RaceName(name),
      circuit = circuit,
      occurredOn = RaceDate(occurredOn),
      results = results
    )
  }
}

data class Round(val value: Int) {
  init {
    require(value > 0) { "Round must be greater than 0" }
  }
}

data class RaceName(val value: String) {
  init {
    require(value.isNotBlank())
  }
}

data class RaceDate(val date: String) : OccurredOn(date)

data class RaceResult(
  val number: String,
  val driver: Driver,
  val position: Int,
  val points: Float,
  val constructor: Constructor,
  val grid: Int,
  val laps: Int,
  val status: RaceResultStatus,
  val timeInMillis: Long,
  val fastestLapInMillis: Long?,
  val averageSpeed: Float?,
  val averageSpeedUnit: String?,
)

enum class RaceResultStatus(val text: String) {
  FINISHED("Finished"),
  LAPS_1("+1 Lap"),
  LAPS_2("+2 Laps"),
  LAPS_3("+3 Laps"),
  LAPS_4("+4 Laps"),
  LAPS_5("+5 Laps"),
  LAPS_6("+6 Laps"),
  LAPS_7("+7 Laps"),
  LAPS_8("+8 Laps"),
  LAPS_9("+9 Laps"),
  DISQUALIFIED("Disqualified"),
  ACCIDENT("Accident"),
  ENGINE("Engine"),
  GEARBOX("Gearbox"),
  TRANSMISSION("Transmission"),
  CLUTCH("Clutch"),
  HYDRAULICS("Hydraulics"),
  ELECTRONICS("Electronics"),
  SUSPENSION("Suspension"),
  BRAKES("Brakes"),
  THROTTLE("Throttle"),
  DRIVESHAFT("Driveshaft"),
  TYRE("Tyre"),
  PUNCTURE("Puncture"),
  FUEL("Fuel"),
  OIL("Oil"),
  WATER("Water"),
  OVERHEATING("Overheating"),
  COLLISION("Collision"),
  SPIN("Spin"),
  RADIATOR("Radiator"),
  EXHAUST("Exhaust"),
  ELECTRICAL("Electrical"),
  MECHANICAL("Mechanical"),
  DAMAGE("Damage"),
  PNEUMATIC("Pneumatic"),
  SAFETY("Safety"),
  WITHDRAWN("Withdrawn"),
  NOT_CLASSIFIED("Not Classified"),
  DISQUALIFIED_PREVIOUS("Disqualified"),
  EXCLUDED("Excluded"),
  NOT_STARTED("Not Started"),
  RETIRED("Retired"),
  DNS("Did Not Start"),
  SUSPENDED("Suspended"),
  INJURY("Injury"),
  SPUN_OFF("Spun Off"),
  DIFFERENTIAL("Differential"),
  DRIVER_SEAT("Driver Seat"),
  UNKNOWN("Unknown");

  companion object {
    fun fromText(text: String): RaceResultStatus = RaceResultStatus.entries
      .firstOrNull { it.text.equals(text, ignoreCase = true) }
      ?: UNKNOWN
  }
}
