package dev.avatsav.linkding.internet.inject

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.getSystemService
import com.r0adkll.kimchi.annotations.ContributesTo
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import dev.avatsav.linkding.internet.AndroidNetworkMonitor
import dev.avatsav.linkding.internet.EmptyNetworkMonitor
import dev.avatsav.linkding.internet.NetworkMonitor
import me.tatarka.inject.annotations.Provides

@ContributesTo(AppScope::class)
interface AndroidNetworkMonitorComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideNetworkMonitor(application: Application): NetworkMonitor {
        val context = application.applicationContext
        val connectivityManager: ConnectivityManager? = context.getSystemService()
        if (connectivityManager == null || !context.isPermissionGranted(ACCESS_NETWORK_STATE)) {
            return EmptyNetworkMonitor()
        }
        return AndroidNetworkMonitor(connectivityManager)
    }
}

internal fun Context.isPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED