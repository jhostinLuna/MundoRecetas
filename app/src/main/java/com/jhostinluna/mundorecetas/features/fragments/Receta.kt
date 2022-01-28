package com.jhostinluna.mundorecetas.features.fragments

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.Gson

data class Receta(
    val id:String="",
    val nombre:String?="",
    val categoria:String?="",
    val ingredientes:List<String>?= emptyList(),
    val preparacion:String?="",
    val date:String?="",
    val foto:String?=""): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    fun toRecetaEntity():RecetaEntity = RecetaEntity(
        id,
        nombre,
        categoria,
        Gson().toJson(ingredientes),
        preparacion,
        date,
        foto)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nombre)
        parcel.writeString(categoria)
        parcel.writeStringList(ingredientes)
        parcel.writeString(preparacion)
        parcel.writeString(date)
        parcel.writeString(foto)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Receta> {
        override fun createFromParcel(parcel: Parcel): Receta {
            return Receta(parcel)
        }

        override fun newArray(size: Int): Array<Receta?> {
            return arrayOfNulls(size)
        }
    }
}