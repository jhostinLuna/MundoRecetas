package com.jhostinluna.mundorecetas.features.fragments

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.jhostinluna.mundorecetas.R
import com.jhostinluna.mundorecetas.core.extensions.getMediaDir
import com.jhostinluna.mundorecetas.core.extensions.loadFromUrl
import com.jhostinluna.mundorecetas.core.navigation.PreviewCameraX
import com.jhostinluna.mundorecetas.core.plataform.BaseFragment
import kotlinx.android.synthetic.main.fragment_create_receta.*
import kotlinx.android.synthetic.main.fragment_create_receta.view.*
import kotlinx.android.synthetic.main.fragment_detalle_receta.*
import kotlinx.android.synthetic.main.item_recetas.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*



/**
 * A simple [Fragment] subclass.
 * Use the [CreateRecetaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateRecetaFragment : BaseFragment(), View.OnClickListener {
    val spannable:SpannableStringBuilder = SpannableStringBuilder("")
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    val REQUEST_CODE_PERMISSIONS = 10
    val REQUEST_TAKE_PHOTO = 1
    var currentPhotoPath: String = ""
    private var listIngredients: ArrayList<String>? = arrayListOf()
    //private val getRecetasViewModel:AddRecetaViewModel by currentScope.inject()
    private val getRecetasViewModel: SaredViewModel by lazy { getSharedViewModel() }
    private lateinit var recetaEntity:RecetaEntity
    private val storage = Firebase.storage
    private var storageRef = storage.reference
    private val auth = Firebase.auth
    val safeArgs : CreateRecetaFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receta = safeArgs.recetaArgs
        if (receta == null) {
            recetaEntity = RecetaEntity()
        }else{
            receta.ingredientes?.forEach {
                listIngredients?.add(it)
            }
            recetaEntity = safeArgs.recetaArgs!!.toRecetaEntity()
        }

    }

    override fun layoutId() = R.layout.fragment_create_receta

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val iconoCamera = context?.getDrawable(R.drawable.ic_baseline_photo_camera_50)
        if (iconoCamera != null) {
            context?.resources?.let { iconoCamera.mutate().setColorFilter(it.getColor(R.color.colorAccent),PorterDuff.Mode.SRC_IN) }
        }
        imageView_foto.setImageDrawable(iconoCamera)
        initSpinner()
        addIngredientswithEnter()
        attach()
        if (safeArgs.recetaArgs != null) {
            loadSafeArgs()
        }
    }
    fun loadSafeArgs(){
        val outputDirectory = context?.getMediaDir()
        val photoFile = File(outputDirectory,recetaEntity.foto)
        recetaEntity.foto?.let {
            imageView_foto.setImageURI(Uri.fromFile(photoFile))
        }?: imageView_foto.setImageResource(R.drawable.uploadfoto)
        editText_titulo.setText(recetaEntity.nombre)
        ediText_preparacion.setText(recetaEntity.preparacion)
        printIngredientes()
    }
    fun printIngredientes(){
        val spannable = SpannableStringBuilder("")
        val receta = recetaEntity.toReceta()
        if (!receta.ingredientes.isNullOrEmpty()){
            receta.ingredientes?.forEach {ingrediente->
                context?.let {
                    val spannablecopy: Spannable = SpannableString(" "+ ingrediente)
                    spannablecopy.setSpan(ImageSpan(it,R.drawable.ic_baseline_check_18),0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.insert(spannable.length,spannablecopy)
                    textViewIngredientes.setText(spannable)
                }
            }
        }

    }
    private fun attach(){
        imageView_foto.setOnClickListener(this)
        btnFoto.setOnClickListener(this)
        btnGuardarReceta.setOnClickListener(this)
        btn_nueva_categoria.setOnClickListener(this)
    }
    private fun addIngredientswithEnter() {
        edText_ingredientes.setOnKeyListener { view, keycode, kEvent ->
            if (kEvent != null) {
                if (kEvent.action == KeyEvent.ACTION_DOWN) {
                    if (keycode == KeyEvent.KEYCODE_ENTER) {
                        listIngredients?.add(edText_ingredientes.text.toString())
                        context?.let {

                            val spannablecopy:Spannable = SpannableString(" "+edText_ingredientes.text)
                            spannablecopy.setSpan(ImageSpan(it,R.drawable.ic_baseline_check_18),0,1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            spannable.insert(spannable.length,spannablecopy)
                            textViewIngredientes.setText(spannable)
                        }
                        //ingredienteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_18,0,0,0)
                        edText_ingredientes.text.clear()

                    }
                }
            }
            true
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it)
        } == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {

            } else {
                Toast.makeText(context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = BitmapFactory.decodeFile(currentPhotoPath)

            //imageView_foto.setImageBitmap(imageBitmap)
            imageView_foto.loadFromUrl(currentPhotoPath)

        }
         */
         if (requestCode == REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {

             if (data != null) {
                 data.extras?.getString("currentPhotoPath")?.let {
                     imageView_foto.loadFromUrl(it)
                     currentPhotoPath = it
                 }
             }

        }
    }
    fun initSpinner(){
        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.categorias_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinnerCategoria.adapter = adapter
                if (safeArgs.recetaArgs != null) {
                    val initPosition = adapter.getPosition(recetaEntity.categoria)
                    spinnerCategoria.setSelection(initPosition)
                }
            }
        }
    }



    override fun onClick(v: View?) {
        if (v != null) {
            if (v.id == R.id.imageView_foto || v.id == R.id.btnFoto){
                //v.findNavController().navigate(R.id.action_createRecetaFragment_to_previewCamara2)
                val i = Intent(context, PreviewCameraX::class.java)
                startActivityForResult(i, REQUEST_TAKE_PHOTO,
                    ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
            }else if (v.id == R.id.btnGuardarReceta){
                if (guardarReceta())
                    if (safeArgs.recetaArgs == null){
                        addReceta(recetaEntity)
                    }else{
                        updateReceta(recetaEntity)
                    }
                else
                    Toast.makeText(context,"¡Título y Preparación no pueden estar vacios!",Toast.LENGTH_LONG).show()

            }


        }
    }

    private fun updateReceta(recetaEntity: RecetaEntity) {
        showProgress()
        getRecetasViewModel.updateReceta(recetaEntity)
    }

    private fun guardarReceta(): Boolean {
        if (ediText_preparacion.text.isNotEmpty() && editText_titulo.text.isNotEmpty()){
            recetaEntity.nombre  = "${editText_titulo.text}"
            recetaEntity.preparacion = "${ediText_preparacion.text}"
            if (spinnerCategoria.selectedItem != null){
                recetaEntity.categoria = spinnerCategoria.selectedItem as String

            }
            recetaEntity.date = System.currentTimeMillis().toString()
            listIngredients?.let {
                recetaEntity.ingredientes = Gson().toJson(listIngredients)
            }
            if (currentPhotoPath.isNotEmpty()){
                subirFotoStorage()
                recetaEntity.foto = getNamePhoto(currentPhotoPath)
            }

            return true

        }
        return false
    }
    fun getNamePhoto(direccion:String) = direccion.split("/").last()

    private fun subirFotoStorage(){
        val userId = auth.currentUser?.uid

        var file = Uri.fromFile(File(currentPhotoPath))
        val fotoRef = storageRef.child("$userId/${file.lastPathSegment}")
        val uploadTask = fotoRef.putFile(file)
        uploadTask.addOnSuccessListener {
            Log.d("imagen","Exito al subir imagen a storage")
        }.addOnFailureListener{
            Log.d("imagen",it.message.toString())
            it.printStackTrace()
        }
    }

    private fun addReceta(recetaEntity: RecetaEntity) {
        showProgress()
        getRecetasViewModel.addReceta(recetaEntity)
    }
}