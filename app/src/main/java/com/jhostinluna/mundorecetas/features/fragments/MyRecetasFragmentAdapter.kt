package com.jhostinluna.mundorecetas.features.fragments

import android.content.Context
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.facebook.AccessToken
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhostinluna.mundorecetas.R
import com.jhostinluna.mundorecetas.core.extensions.inflate
import com.jhostinluna.mundorecetas.core.extensions.loadFromUrl
import kotlinx.android.synthetic.main.item_my_recetas_adapter.view.*
import kotlinx.android.synthetic.main.item_recetas.view.*
import kotlin.properties.Delegates
import com.facebook.GraphResponse

import com.facebook.GraphRequest
import com.jhostinluna.mundorecetas.core.extensions.getMediaDir
import org.json.JSONObject
import java.io.File


class MyRecetasFragmentAdapter(val context: Context): RecyclerView.Adapter<MyRecetasFragmentAdapter.ViewHolder>() {
    internal var collection: List<Receta> by Delegates.observable(emptyList()) {
            _, _, _ -> notifyDataSetChanged()
    }
    internal var clickListener: (Receta) -> Unit = { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_recetas))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(collection.get(position),clickListener,context)
    }

    override fun getItemCount() = collection.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(receta: Receta,clickListener:(Receta)-> Unit,context: Context){
            val urlPhoto = Firebase.auth.currentUser?.providerData?.get(0)?.photoUrl

            if (!receta.foto.isNullOrEmpty()){
                val outputDirectory = context.getMediaDir()
                val filePhoto = File(outputDirectory,receta.foto)
                itemView.imageViewItemRecetas.setImageURI(Uri.fromFile(filePhoto))
            }else{
                itemView.imageViewItemRecetas.setImageURI(urlPhoto)
            }
            itemView.textViewNombreItemRecetas.text = receta.nombre
            itemView.textViewCategoriaItemRecetas.text = receta.categoria
            itemView.textViewAutorItemRecetas.text = Firebase.auth.currentUser?.displayName
            Glide.with(context)
                .load(urlPhoto)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.imageViewFotoPerfilItemRecetas)
            //itemView.imageViewFotoPerfilItemRecetas.setImageURI(urlPhoto)
            addIngredientes(receta,context)
            itemView.setOnClickListener {
                val safeArgs = MyRecetasFragmentDirections.actionMyRecetasFragmentToDetalleReceta(receta.id)
                itemView.findNavController().navigate(safeArgs)
            }
        }

        private fun addIngredientes(receta:Receta,context:Context) {
            val spannable = SpannableStringBuilder("")
            if (!receta.ingredientes.isNullOrEmpty()){
                receta.ingredientes.forEach {ingrediente ->
                    context.let {

                        val spannablecopy: Spannable = SpannableString(" "+ ingrediente)
                        spannablecopy.setSpan(ImageSpan(it,R.drawable.ic_baseline_check_18),0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.insert(spannable.length,spannablecopy)
                        itemView.textViewIngredientesItemRecetas.setText(spannable)
                    }
                }
            }


        }

    }
}