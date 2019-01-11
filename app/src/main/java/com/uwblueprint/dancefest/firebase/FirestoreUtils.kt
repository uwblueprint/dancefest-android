package com.uwblueprint.dancefest.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class FirestoreUtils() {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FirestoreUtils"

    init {
        FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
    }

    fun addData(collectionName: String, data: HashMap<String, Any?>) : Unit {
        db.collection(collectionName)
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.id)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
    }

    fun updateData(collectionName: String, docName: String, data: HashMap<String, Any?>) : Unit {
        db.collection(collectionName).document(docName)
                .set(data)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing document", e)
                }
    }
}