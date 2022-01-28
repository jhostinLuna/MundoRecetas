package com.jhostinluna.mundorecetas.features.fragments

import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.jhostinluna.mundorecetas.R
import com.jhostinluna.mundorecetas.core.extensions.getMediaDir
import com.jhostinluna.mundorecetas.core.extensions.loadFromUrl
import com.jhostinluna.mundorecetas.core.plataform.BaseFragment
import kotlinx.android.synthetic.main.fragment_detalle_receta.*
import kotlinx.android.synthetic.main.item_recetas.view.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetalleReceta.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetalleReceta : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val idRecetaArgs: DetalleRecetaArgs by navArgs()
    //private val sharedViewModel by sharedViewModel
    private val getRecetasViewModel: SaredViewModel by lazy { getSharedViewModel() }
    lateinit var actualReceta:Receta
    override fun layoutId(): Int = R.layout.fragment_detalle_receta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        actualReceta = getRecetasViewModel.getRecetaById(idRecetaArgs.idRecetaArgs)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val outputDirectory = context?.getMediaDir()
        val photoFile = File(outputDirectory,actualReceta.foto)
        actualReceta.foto?.let {
            imageViewRecetaDetalle.setImageURI(Uri.fromFile(photoFile))
        }?: imageViewRecetaDetalle.setImageResource(R.drawable.uploadfoto)
        textViewTituloDetalle.text = actualReceta.nombre
        textViewCategoriaDetalle.text = actualReceta.categoria
        textViewPreparacionDetalle.text = actualReceta.preparacion
        printIngredientes()
        buttonUpdateDetalle.setOnClickListener {
            val action = DetalleRecetaDirections.actionDetalleRecetaToCreateRecetaFragment("",actualReceta)
            view.findNavController().navigate(action)
        }
    }
    fun printIngredientes(){
        val spannable = SpannableStringBuilder("")
        actualReceta.ingredientes?.forEach {ingrediente->
            context?.let {
                val spannablecopy: Spannable = SpannableString(" "+ ingrediente+"\n")
                spannablecopy.setSpan(ImageSpan(it,R.drawable.ic_baseline_check_18),0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.insert(spannable.length,spannablecopy)
                textViewIngredientesLeft.setText(spannable)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetalleReceta.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetalleReceta().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}