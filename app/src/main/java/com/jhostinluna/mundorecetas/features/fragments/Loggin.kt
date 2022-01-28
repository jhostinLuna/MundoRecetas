package com.jhostinluna.mundorecetas.features.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import com.facebook.login.widget.LoginButton
import com.facebook.FacebookException

import com.facebook.login.LoginResult

import com.facebook.FacebookCallback
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhostinluna.mundorecetas.R
import com.facebook.login.LoginManager
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.SignInButton
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.scope.currentScope


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val RC_SIGN_IN = 101

/**
 * A simple [Fragment] subclass.
 * Use the [Loggin.newInstance] factory method to
 * create an instance of this fragment.
 */
class Loggin : androidx.fragment.app.Fragment() {
    private lateinit var callbackManager:CallbackManager
    private lateinit var buttonFacebookLogin:LoginButton
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient:GoogleSignInClient
    private lateinit var btnsalir:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth


        auth = Firebase.auth
        callbackManager = create()
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {

                override fun onCancel() {
                    Log.d("LoginManager","Cancelado popr el usuario")
                }

                override fun onError(exception: FacebookException) {
                    Log.d("LoginManager","Error: ${exception.message}")
                }

                override fun onSuccess(result: LoginResult?) {
                    Log.d("LoginManager","succes Facbook")
                    result?.let {
                        val token = result?.accessToken?.token
                        firebaseAuthWithFacebook(token)
                    }

                }
            })

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context,gso)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(com.jhostinluna.mundorecetas.R.layout.fragment_loggin, container, false)

        // Initialize Facebook Login button
        btnsalir = view.findViewById(R.id.btn_salir)
        callbackManager = CallbackManager.Factory.create()
        buttonFacebookLogin = view.findViewById(R.id.login_facebook_button)
        buttonFacebookLogin.setReadPermissions("email", "public_profile")
        // If using in a fragment
        buttonFacebookLogin.setFragment(this);
        buttonFacebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("prueba", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("prueba", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d("prueba", "facebook:onError", error)
            }
        })

        val btnGoogle = view.findViewById<SignInButton>(R.id.button_sign_with_google)
        btnGoogle.setSize(SignInButton.SIZE_WIDE);
        btnGoogle.setOnClickListener{
            signIn()
        }
        btnsalir.setOnClickListener{
            salir()
        }

        return view
    }
    fun salir (){
        context?.let {
            AuthUI.getInstance()
                .signOut(it)
                .addOnCompleteListener {
                    Toast.makeText(context,"salir con cuenta",Toast.LENGTH_LONG).show()
                }
        }
    }

    // ...
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("onActivityResult", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("onActivityResult", "Google sign in failed", e)
            }
        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("prueba", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("prueba", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("prueba", "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user!=null){
            val action = user?.let { LogginDirections.actionGlobalHomeRecetasFragment(it) }
            if (action != null) {
                findNavController().navigate(action)
            }
        }

    }

    //Acceso con Google
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    private fun firebaseAuthWithFacebook(idToken: String){
        val credential = FacebookAuthProvider.getCredential(idToken)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("prueba", "signInWithCredential:success FACEBOOK")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("prueba", "signInWithCredential:failure FACEBOOK", task.exception)
                    updateUI(null)
                }
            }
    }
}