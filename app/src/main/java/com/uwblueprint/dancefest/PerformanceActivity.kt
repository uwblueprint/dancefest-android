package com.uwblueprint.dancefest

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
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

    companion object {
        const val ARG_ACADEMIC_LEVEL = "academicLevel"
        const val ARG_ARTISTIC_MARK = "artisticMark"
        const val ARG_AUDIO_URL = "audioURL"
        const val ARG_CHOREOGRAPHERS = "choreographers"
        const val ARG_CHOREO_AWARD = "choreoAward"
        const val ARG_COMPETITION_LEVEL = "competitionLevel"
        const val ARG_CUMULATIVE_MARK = "cumulativeMark"
        const val ARG_DANCE_ENTRY = "danceEntry"
        const val ARG_DANCE_STYLE = "danceStyle"
        const val ARG_DANCE_TITLE = "danceTitle"
        const val ARG_JUDGE_NAME = "judgeName"
        const val ARG_NOTES = "notes"
        const val ARG_PERFORMERS = "performers"
        const val ARG_SCHOOL = "school"
        const val ARG_SIZE = "size"
        const val ARG_SPECIAL_AWARD = "specialAward"
        const val ARG_TABLET_ID = "tabletID"
        const val ARG_TECHNICAL_MARK = "technicalMark"
        const val COLLECTION_ADJUDICATIONS = "adjudications"
        const val COLLECTION_EVENTS = "events"
        const val COLLECTION_PERFORMANCES = "performances"
        const val DEFAULT = "N/A"
        const val NUM_ITEMS = 2
        const val TAG = "PERFORMANCES_ACTIVITY"
        const val TAG_ADJUDICATIONS = "TAG_ADJUDICATIONS"
        const val TAG_PERFORMANCES = "TAG_PERFORMANCES"
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
            event = intent.getSerializableExtra(EventsAdapter.TAG_EVENT) as Event
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
                .addSnapshotListener { performanceValue, performanceE ->
                    if (performanceE != null) {
                        Log.e(TAG, "Listen failed", performanceE)
                        return@addSnapshotListener
                    }
                    if (performanceValue == null) {
                        Log.e(TAG, "Null QuerySnapshot")
                        return@addSnapshotListener
                    }

                    completePerformances = arrayListOf()
                    incompletePerformances = arrayListOf()
                    adjudications = hashMapOf()

                    val countDownLatch = CountDownLatch(performanceValue.size())

                    for (performanceDoc in performanceValue) {
                        val academicLevel = performanceDoc.data[ARG_ACADEMIC_LEVEL]
                        val choreographers = performanceDoc.data[ARG_CHOREOGRAPHERS]
                        val competitionLevel = performanceDoc.data[ARG_COMPETITION_LEVEL]
                        val danceEntry = performanceDoc.data[ARG_DANCE_ENTRY]
                        val danceStyle = performanceDoc.data[ARG_DANCE_STYLE]
                        val danceTitle = performanceDoc.data[ARG_DANCE_TITLE]
                        val performers = performanceDoc.data[ARG_PERFORMERS]
                        val school = performanceDoc.data[ARG_SCHOOL]
                        val size = performanceDoc.data[ARG_SIZE]

                        val newPerformance = Performance(
                            performanceId = performanceDoc.id,
                            academicLevel =
                            if (academicLevel is String) academicLevel else DEFAULT,
                            choreographers =
                            if (choreographers is String) choreographers else DEFAULT,
                            competitionLevel =
                            if (competitionLevel is String) competitionLevel else DEFAULT,
                            danceEntry =
                            if (danceEntry is String) danceEntry else DEFAULT,
                            danceStyle =
                            if (danceStyle is String) danceStyle else DEFAULT,
                            danceTitle =
                            if (danceTitle is String) danceTitle else DEFAULT,
                            performers =
                            if (performers is String) performers else DEFAULT,
                            school =
                            if (school is String) school else DEFAULT,
                            size =
                            if (size is String) size else DEFAULT
                        )

                        database
                            .collection(COLLECTION_EVENTS)
                            .document(event.eventId)
                            .collection(COLLECTION_PERFORMANCES)
                            .document(performanceDoc.id)
                            .collection(COLLECTION_ADJUDICATIONS)
                            .whereEqualTo(ARG_TABLET_ID, tabletID)
                            .get()
                            .addOnSuccessListener {
                                if (it.size() == 0) {
                                    incompletePerformances.add(newPerformance)
                                } else {
                                    completePerformances.add(newPerformance)
                                    val adjDoc = it.documents[0]
                                    val artisticMark = adjDoc.data?.get(ARG_ARTISTIC_MARK)
                                    val audioURL = adjDoc.data?.get(ARG_AUDIO_URL)
                                    val choreoAward = adjDoc.data?.get(ARG_CHOREO_AWARD)
                                    val cumulativeMark = adjDoc.data?.get(ARG_CUMULATIVE_MARK)
                                    val judgeName = adjDoc.data?.get(ARG_JUDGE_NAME)
                                    val notes = adjDoc.data?.get(ARG_NOTES)
                                    val specialAward = adjDoc.data?.get(ARG_SPECIAL_AWARD)
                                    val technicalMark = adjDoc.data?.get(ARG_TECHNICAL_MARK)

                                    adjudications[performanceDoc.id] = Adjudication(
                                        artisticMark =
                                        if (artisticMark is Int) artisticMark else -1,
                                        audioURL =
                                        if (audioURL is String) audioURL else DEFAULT,
                                        choreoAward =
                                        if (choreoAward is Boolean) choreoAward else false,
                                        cumulativeMark =
                                        if (cumulativeMark is Int) cumulativeMark else -1,
                                        judgeName =
                                        if (judgeName is String) judgeName else DEFAULT,
                                        notes =
                                        if (notes is String) notes else DEFAULT,
                                        specialAward =
                                        if (specialAward is Boolean) specialAward else false,
                                        technicalMark =
                                        if (technicalMark is Int) technicalMark else -1
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
                            view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
                            tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

    class PerformancePagerAdapter(
        private val adjudications: HashMap<String, Adjudication>,
        private val completePerformances: ArrayList<Performance>,
        private val event: Event,
        private val incompletePerformances: ArrayList<Performance>,
        fm: FragmentManager
    ) : FragmentPagerAdapter(fm) {
        override fun getCount() = NUM_ITEMS
        override fun getItem(position: Int): Fragment {
            val fragment = PerformanceFragment()
            fragment.arguments = Bundle().apply {
                putSerializable(TAG_ADJUDICATIONS, adjudications)
                putString(TAG_TITLE, event.name)
                if (position == 0) {
                    putParcelableArrayList(TAG_PERFORMANCES, incompletePerformances)
                } else {
                    putParcelableArrayList(TAG_PERFORMANCES, completePerformances)
                }
            }
            return fragment
        }
    }
}