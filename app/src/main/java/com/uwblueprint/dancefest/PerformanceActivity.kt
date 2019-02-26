package com.uwblueprint.dancefest

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.uwblueprint.dancefest.firebase.FirestoreUtils
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Event
import com.uwblueprint.dancefest.models.Performance
import kotlinx.android.synthetic.main.activity_performance.*
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class PerformanceActivity : AppCompatActivity() {

    private lateinit var event: Event
    private lateinit var pagerAdapter: FragmentPagerAdapter
    private lateinit var database: FirebaseFirestore
    private val adjKeys = Adjudication.adjKeys
    private val perfKeys = Performance.perfKeys

    companion object {
        const val COLLECTION_ADJUDICATIONS = "adjudications"
        const val COLLECTION_EVENTS = "events"
        const val COLLECTION_PERFORMANCES = "performances"
        const val DEFAULT = "N/A"
        const val NUM_ITEMS = 2
        const val TAG = "PERFORMANCES_ACTIVITY"
        const val TAG_ADJUDICATIONS = "TAG_ADJUDICATIONS"
        const val TAG_PERFORMANCES = "TAG_PERFORMANCES"
        const val TAG_TABLET_ID = "tabletID"
        const val TAG_TITLE = "TAG_TITLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        performance_toolbar.setTitle(R.string.title_performances)
        setSupportActionBar(performance_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        database = FirebaseFirestore.getInstance()

        if (intent != null) {
            event = intent.getSerializableExtra(EventActivity.TAG_EVENT) as Event
        }

        var adjudications: HashMap<String, Adjudication>
        var completePerformances: ArrayList<Performance>
        var incompletePerformances: ArrayList<Performance>

        val idFetcher = IDFetcher()
        idFetcher.registerCallback(database) { tabletID ->
            database
                .collection(COLLECTION_EVENTS)
                .document(event.eventId)
                .collection(COLLECTION_PERFORMANCES)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "Listen failed", error)
                        return@addSnapshotListener
                    }
                    if (value == null) {
                        Log.e(TAG, "Null QuerySnapshot")
                        return@addSnapshotListener
                    }

                    completePerformances = arrayListOf()
                    incompletePerformances = arrayListOf()
                    adjudications = hashMapOf()

                    val countDownLatch = CountDownLatch(value.size())

                    for (performanceDoc in value) {
                        val academicLevel = performanceDoc.data[perfKeys.ARG_ACADEMIC_LEVEL]
                        val choreographers = performanceDoc.data[perfKeys.ARG_CHOREOGRAPHERS]
                        val competitionLevel = performanceDoc.data[perfKeys.ARG_COMPETITION_LEVEL]
                        val danceEntry = performanceDoc.data[perfKeys.ARG_DANCE_ENTRY]
                        val danceStyle = performanceDoc.data[perfKeys.ARG_DANCE_STYLE]
                        val danceTitle = performanceDoc.data[perfKeys.ARG_DANCE_TITLE]
                        val performers = performanceDoc.data[perfKeys.ARG_PERFORMERS]
                        val school = performanceDoc.data[perfKeys.ARG_SCHOOL]
                        val size = performanceDoc.data[perfKeys.ARG_SIZE]

                        val newPerformance = Performance(
                            performanceId = performanceDoc.id,
                            academicLevel = FirestoreUtils.getVal(academicLevel, DEFAULT),
                            choreographers = FirestoreUtils.getVal(choreographers, DEFAULT),
                            competitionLevel = FirestoreUtils.getVal(competitionLevel, DEFAULT),
                            danceEntry = FirestoreUtils.getVal(danceEntry, DEFAULT),
                            danceStyle = FirestoreUtils.getVal(danceStyle, DEFAULT),
                            danceTitle = FirestoreUtils.getVal(danceTitle, DEFAULT),
                            performers = FirestoreUtils.getVal(performers, DEFAULT),
                            school = FirestoreUtils.getVal(school, DEFAULT),
                            size = FirestoreUtils.getVal(size, DEFAULT)
                        )

                        database
                            .collection("/$COLLECTION_EVENTS/${event.eventId}" +
                            "/$COLLECTION_PERFORMANCES/${performanceDoc.id}" +
                            "/$COLLECTION_ADJUDICATIONS")
                            .whereEqualTo(TAG_TABLET_ID, tabletID)
                            .get()
                            .addOnSuccessListener {
                                if (it.size() == 0) {
                                    incompletePerformances.add(newPerformance)
                                } else {
                                    completePerformances.add(newPerformance)
                                    val adjDocData = it.documents[0].data
                                    val artisticMark = adjDocData?.get(adjKeys.ARG_ARTISTIC_MARK)
                                    val audioURL = adjDocData?.get(adjKeys.ARG_AUDIO_URL)
                                    val choreoAward = adjDocData?.get(adjKeys.ARG_CHOREO_AWARD)
                                    val cumulativeMark =
                                        adjDocData?.get(adjKeys.ARG_CUMULATIVE_MARK)
                                    val judgeName = adjDocData?.get(adjKeys.ARG_JUDGE_NAME)
                                    val notes = adjDocData?.get(adjKeys.ARG_NOTES)
                                    val specialAward = adjDocData?.get(adjKeys.ARG_SPECIAL_AWARD)
                                    val technicalMark = adjDocData?.get(adjKeys.ARG_TECHNICAL_MARK)

                                    adjudications[performanceDoc.id] = Adjudication(
                                        adjudicationId = it.documents[0].id,
                                        artisticMark = FirestoreUtils.getVal(artisticMark, -1),
                                        audioURL = FirestoreUtils.getVal(audioURL, DEFAULT),
                                        choreoAward = FirestoreUtils.getVal(choreoAward, false),
                                        cumulativeMark = FirestoreUtils.getVal(cumulativeMark, -1),
                                        judgeName = FirestoreUtils.getVal(judgeName, DEFAULT),
                                        notes = FirestoreUtils.getVal(notes, DEFAULT),
                                        specialAward = FirestoreUtils.getVal(specialAward, false),
                                        technicalMark = FirestoreUtils.getVal(technicalMark, -1)
                                    )
                                }
                                countDownLatch.countDown()
                            }
                            .addOnFailureListener {
                                Log.e(TAG, "Failed to get adjudication for tabletID: $tabletID")
                                countDownLatch.countDown()
                            }
                    }

                    thread {
                        countDownLatch.await()
                        runOnUiThread {
                            pagerAdapter = PerformancePagerAdapter(
                                adjudications,
                                completePerformances,
                                event,
                                incompletePerformances,
                                supportFragmentManager
                            )
                            view_pager.adapter = pagerAdapter
                            view_pager.addOnPageChangeListener(
                                TabLayout.TabLayoutOnPageChangeListener(tab_layout))
                            tab_layout.addOnTabSelectedListener(
                                object : TabLayout.OnTabSelectedListener {
                                override fun onTabReselected(tab: TabLayout.Tab) {}
                                override fun onTabUnselected(tab: TabLayout.Tab) {}
                                override fun onTabSelected(tab: TabLayout.Tab) {
                                    view_pager.currentItem = tab.position
                                }
                            })
                        }
                    }
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        onBackPressed()
        return true
    }
}
