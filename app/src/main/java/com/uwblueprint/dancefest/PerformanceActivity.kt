package com.uwblueprint.dancefest

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Event
import com.uwblueprint.dancefest.models.Performance
import kotlinx.android.synthetic.main.activity_performance.*

class PerformanceActivity : FragmentActivity() {

    private lateinit var event: Event
    private lateinit var pagerAdapter: FragmentPagerAdapter

    companion object {
        const val ARG_ACADEMIC_LEVEL = "academicLevel"
        const val ARG_ARTISTIC_MARK = "artisticMark"
        const val ARG_CHOREOGRAPHERS = "choreographers"
        const val ARG_COMPETITION_LEVEL = "competitionLevel"
        const val ARG_CUMULATIVE_SCORE = "cumulativeScore"
        const val ARG_DANCE_ENTRY = "danceEntry"
        const val ARG_DANCE_STYLE = "danceStyle"
        const val ARG_DANCE_TITLE = "danceTitle"
        const val ARG_NOTES = "notes"
        const val ARG_PERFORMERS = "performers"
        const val ARG_SCHOOL = "school"
        const val ARG_SIZE = "size"
        const val ARG_TECHNICAL_MARK = "technicalMark"
        const val COLLECTION_ADJUDICATIONS = "adjudications"
        const val COLLECTION_EVENTS = "events"
        const val COLLECTION_PERFORMANCES = "performances"
        const val DEFAULT = "N/A"
        const val NUM_ITEMS = 2
        const val TAG = "PERFORMANCES_ACTIVITY"
        const val TAG_TITLE = "TAG_TITLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        performance_toolbar.setTitle(R.string.title_performances)

        if (intent != null) {
            event = intent.getSerializableExtra(EventsAdapter.TAG_EVENT) as Event
        }

        var performances: ArrayList<Performance>
        var adjudications: ArrayList<Adjudication>

        FirebaseFirestore.getInstance()
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

                performances = arrayListOf()
                adjudications = arrayListOf()

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

                    performances.add(
                        Performance(
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
                    )

                    FirebaseFirestore.getInstance()
                        .collection(COLLECTION_EVENTS)
                        .document(event.eventId)
                        .collection(COLLECTION_PERFORMANCES)
                        .document(performanceDoc.id)
                        .collection(COLLECTION_ADJUDICATIONS)
                        .whereEqualTo("tabletID", "test1")
                        .get()
                        .addOnSuccessListener {
                            2
                        }
                        .addOnFailureListener {
                            2
                        }
                }
            }

        // TODO: move to firebase getter
        pagerAdapter = PerformancePagerAdapter(event, supportFragmentManager)
        view_pager.adapter = pagerAdapter
        // TODO
        view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                view_pager.currentItem = tab.position
            }
        })
    }

    class PerformancePagerAdapter(private val event: Event, fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount() = NUM_ITEMS
        override fun getItem(position: Int): Fragment {
            val fragment = PerformanceFragment()
            fragment.arguments = Bundle().apply {
                putString(TAG_TITLE, event.name)
            }
            return fragment
        }
    }
}