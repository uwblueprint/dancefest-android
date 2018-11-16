class CritiqueForm (val danceId : Float, val danceTitle: String, val school: String,
                    val level: String, val levelOfCompetition: String, val danceStyle: String,
                    val groupSize: String, val performers: Array<String>) {

    // initialized later
    var artisticScore: Int = 0
    var technicalScore: Int = 0
    var notes: String = ""
    var audioFiles = emptyArray<String>()


}