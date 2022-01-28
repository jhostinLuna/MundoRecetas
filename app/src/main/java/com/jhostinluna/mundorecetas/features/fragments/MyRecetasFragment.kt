package com.jhostinluna.mundorecetas.features.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jhostinluna.mundorecetas.R
import com.jhostinluna.mundorecetas.core.exception.Failure
import com.jhostinluna.mundorecetas.core.extensions.failure
import com.jhostinluna.mundorecetas.core.extensions.observe
import com.jhostinluna.mundorecetas.core.functional.DialogCallback
import com.jhostinluna.mundorecetas.core.plataform.BaseFragment
import kotlinx.android.synthetic.main.fragment_my_recetas.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.getSharedViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyRecetasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyRecetasFragment : BaseFragment() {
    private val myRecetasFragmentAdapter by currentScope.inject<MyRecetasFragmentAdapter>()
    private val getRecetasViewModel: SaredViewModel by lazy { getSharedViewModel() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        with(getRecetasViewModel) {
            observe(recetas,::renderRecetasList)
            failure(failure, ::handleFailure)

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }
    private fun initializeView(){
        recetasList.layoutManager = LinearLayoutManager(activity)
        recetasList.adapter = myRecetasFragmentAdapter
    }

    override fun layoutId(): Int = R.layout.fragment_my_recetas

    private fun renderRecetasList(recetas: List<Receta>?) {
        myRecetasFragmentAdapter.collection = recetas.orEmpty()
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
                loadRecetas()
            }

            override fun onDecline() {
                onBackPressed()
            }
        })
    }
    private fun loadRecetas(){
        val aux = getRecetasViewModel.loadRecetas()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyRecetasFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyRecetasFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}