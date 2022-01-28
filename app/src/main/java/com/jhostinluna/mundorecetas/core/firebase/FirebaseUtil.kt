package com.jhostinluna.mundorecetas.core.firebase

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jhostinluna.mundorecetas.BuildConfig

class FirebaseUtil {



    private var AUTH: FirebaseAuth? = null
    private var AUTH_UI: AuthUI? = null
    companion object{
        private var FIRESTORE: FirebaseFirestore? = null
        private val sUseEmulators: Boolean = BuildConfig.DEBUG
        fun getFirestore(): FirebaseFirestore? {
            if (FIRESTORE == null) {
                FIRESTORE = FirebaseFirestore.getInstance()

                // Connect to the Cloud Firestore emulator when appropriate. The host '10.0.2.2' is a
                // special IP address to let the Android emulator connect to 'localhost'.
                if (sUseEmulators) {
                    //FIRESTORE.useEmulator("10.0.2.2", 8080);
                    FIRESTORE!!.useEmulator("10.0.2.2", 8080)
                }
            }
            return FIRESTORE
        }
    }
    fun getFirestore(): FirebaseFirestore? {
        if (FIRESTORE == null) {
            FIRESTORE = FirebaseFirestore.getInstance()

            // Connect to the Cloud Firestore emulator when appropriate. The host '10.0.2.2' is a
            // special IP address to let the Android emulator connect to 'localhost'.
            if (sUseEmulators) {
                //FIRESTORE.useEmulator("10.0.2.2", 8080);
                FIRESTORE!!.useEmulator("10.0.2.2", 8080)
            }
        }
        return FIRESTORE
    }

    fun getAuth(): FirebaseAuth? {
        if (AUTH == null) {
            AUTH = FirebaseAuth.getInstance()

            // Connect to the Firebase Auth emulator when appropriate. The host '10.0.2.2' is a
            // special IP address to let the Android emulator connect to 'localhost'.
            if (sUseEmulators) {
                AUTH!!.useEmulator("10.0.2.2", 9099)
            }
        }
        return AUTH
    }

    fun getAuthUI(): AuthUI? {
        if (AUTH_UI == null) {
            AUTH_UI = AuthUI.getInstance()

            // Connect to the Firebase Auth emulator when appropriate. The host '10.0.2.2' is a
            // special IP address to let the Android emulator connect to 'localhost'.
            if (sUseEmulators) {
                AUTH_UI!!.useEmulator("10.0.2.2", 9099)
            }
        }
        return AUTH_UI
    }
}