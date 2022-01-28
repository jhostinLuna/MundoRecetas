package com.jhostinluna.mundorecetas.features.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jhostinluna.mundorecetas.BuildConfig
import com.jhostinluna.mundorecetas.R
import com.jhostinluna.mundorecetas.core.exception.Failure
import com.jhostinluna.mundorecetas.core.extensions.failure
import com.jhostinluna.mundorecetas.core.extensions.getMediaDir
import com.jhostinluna.mundorecetas.core.extensions.observe
import com.jhostinluna.mundorecetas.core.functional.DialogCallback
import com.jhostinluna.mundorecetas.core.navigation.PreviewCameraX
import com.jhostinluna.mundorecetas.core.plataform.BaseFragment
import kotlinx.android.synthetic.main.fragment_home_recetas.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homeRecetasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class homeRecetasFragment : BaseFragment(),CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private val storage = Firebase.storage
    private var storageRef = storage.reference
    private val getRecetasViewModel: SaredViewModel by lazy { getSharedViewModel() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        with(getRecetasViewModel) {
                observe(recetas,::renderRecetasList)
                failure(failure, ::handleFailure)
        }

    }
    fun loadImgStorage(recetas: List<Receta>?){
        val outputDirectory = context?.getMediaDir()
        launch {
            recetas?.forEach {
                if (it.foto?.isNotEmpty() == true){
                    val photoFile = File(outputDirectory,it.foto)
                    val fotoStorage = storageRef.child(Firebase.auth.currentUser?.uid+"/${it.foto}")
                    if (outputDirectory != null) {
                        val task = fotoStorage.getFile(photoFile)
                        if (task.isSuccessful){
                            Log.d("foto","EXITO!!")
                        }
                    }
                }
            }
        }

    }
    private fun getOutputDirectory(): File? {
        val mediaDir = context?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else context?.filesDir
    }
    override fun layoutId() = R.layout.fragment_home_recetas

    private fun renderRecetasList(recetas: List<Receta>?) {
        if (getFirstTimeRun() == 0 && recetas?.isNotEmpty() == true){
            loadImgStorage(recetas)
        }

        hideProgress()
    }
    private fun handleFailure(failure: Failure?) {
        when(failure) {
            is Failure.CustomError -> renderFailure(failure.errorCode, failure.errorMessage)
            else -> renderFailure(0, "")
        }
    }
    private fun renderFailure(errorCode: Int, errorMessage: String?) {
        hideProgress()
        showError(errorCode, errorMessage, object : DialogCallback {
            override fun onAccept() {
            }

            override fun onDecline() {
                onBackPressed()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.invalidateOptionsMenu()
        btn_crear.setOnClickListener{
            view.findNavController().navigate(R.id.createRecetaFragment)
        }
        btn_mis_recetas.setOnClickListener{
            view.findNavController().navigate(R.id.myRecetasFragment)
        }
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        val itemFavorite = menu.findItem(R.id.favorite)
        itemFavorite.setVisible(false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment homeRecetasFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            homeRecetasFragment().apply {
                arguments = Bundle().apply {}
            }
    }


    private fun getFirstTimeRun(): Int {
        val sp = context?.getSharedPreferences("MYAPP", AppCompatActivity.MODE_PRIVATE)
        val result: Int
        val currentVersionCode: Int = BuildConfig.VERSION_CODE
        val lastVersionCode = sp?.getInt("FIRSTTIMERUN", -1)
        result = if (lastVersionCode == -1) 0
        else if (lastVersionCode == currentVersionCode) 1
        else 2
        sp?.edit()?.putInt("FIRSTTIMERUN", currentVersionCode)?.apply()
        return result
    }
}