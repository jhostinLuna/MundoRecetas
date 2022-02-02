package com.jhostinluna.mundorecetas.core.navigation

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.firebase.ui.auth.AuthUI
import com.jhostinluna.mundorecetas.R
import com.jhostinluna.mundorecetas.core.functional.DialogCallback
import com.jhostinluna.mundorecetas.features.fragments.SaredViewModel
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), PopUpDelegator {

    private val viewModel: SaredViewModel by viewModel()
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAppCenter()
        setSupportActionBar(toolbar)
        // Muestra el nombre de la app en toolbar si es true
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.cerrar_session -> Log.d("onOptionsItemSelected","CERRRAR SESSION")
                R.id.favorite ->        Log.d("onOptionsItemSelected","FAVORITE")
                else ->             Log.d("onOptionsItemSelected","LO QUE SEA")

            }
            if (progress.visibility == View.VISIBLE) progress.visibility = View.GONE
        }
    }
    fun initAppCenter() {
        Distribute.setEnabledForDebuggableBuild(true)
        AppCenter.start(
            application, "ee7611be-73e5-4c26-8270-af6c55c41909",
            Analytics::class.java, Crashes::class.java, Distribute::class.java
        )
    }
    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.btn_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.cerrar_session -> {
                salir()
                Log.d("onOptionsItemSelected","CERRAR SESSION")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun salir (){
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                navController.navigate(R.id.logginGraph)
                Log.d("onOptionsItemSelected","SALIR CUENTA")
            }

    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    override fun showErrorWithRetry(
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        callback: DialogCallback
    ) {
        TODO("Not yet implemented")
    }

}