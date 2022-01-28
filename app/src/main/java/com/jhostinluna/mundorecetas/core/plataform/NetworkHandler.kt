package com.jhostinluna.mundorecetas.core.platform

import android.content.Context
import com.jhostinluna.mundorecetas.core.extensions.checkNetworkState


class NetworkHandler
(private val context: Context) {
    val isConnected get() = context.checkNetworkState()
}