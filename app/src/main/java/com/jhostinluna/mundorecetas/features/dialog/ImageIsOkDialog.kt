package com.jhostinluna.mundorecetas.features.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.getIntent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.jhostinluna.mundorecetas.R
import com.jhostinluna.mundorecetas.features.fragments.PreviewCamaraDirections
import java.io.File

data class ImageIsOkDialog(val imagen:String): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val bitmapImage = BitmapFactory.decodeFile(imagen)
            val builder = AlertDialog.Builder(it,android.R.style.Theme_NoTitleBar_Fullscreen)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val view = inflater.inflate(R.layout.itemimageisok, null)
            val captura = view.findViewById<ImageView>(R.id.imageView_captura)
            captura.setImageBitmap(bitmapImage)
            builder.setView(view)
                // Add action buttons
                .setPositiveButton("aceptar",
                    DialogInterface.OnClickListener { dialog, id ->
                        val action = PreviewCamaraDirections.actionPreviewCamaraToCreateRecetaFragment(imagen)
                        Log.e("imageCaptura","exito al cargar imagen")
                        /*
                        getIntent().putExtra("TADA", imagen)
                        setResult(AppCompatActivity.RESULT_OK, getIntent())
                        finish();

                         */
                        requireActivity().intent.putExtra("currentPhotoPath",imagen)
                        requireActivity().setResult(AppCompatActivity.RESULT_OK,requireActivity().intent)
                        requireActivity().finish()
                    })
                .setNegativeButton("borrar",
                    DialogInterface.OnClickListener { dialog, id ->
                        val fileImage = File(imagen)
                        if (fileImage.exists()){
                            if (fileImage.delete())Log.d("imagen","Ha sido borrado!!") else Log.d("imagen","Sin borrar")

                        }else{
                            Log.d("imagen","No existe")
                        }

                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}