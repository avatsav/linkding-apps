package dev.avatsav.linkding.internet

import com.r0adkll.kimchi.annotations.ContributesBinding
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_get_main_queue

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class IosNetworkMonitor : NetworkMonitor {

    private val monitor = nw_path_monitor_create()

    private var listener: NetworkMonitor.Listener? = null

    init {
        nw_path_monitor_set_update_handler(monitor) { path ->
            val pathStatus = nw_path_get_status(path)
            _isOnline = pathStatus == nw_path_status_satisfied
            listener?.onConnectivityChange(_isOnline)
        }

        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_start(monitor)
    }

    private var _isOnline = true
    override val isOnline: Boolean
        get() = _isOnline

    override fun close() {
        nw_path_monitor_cancel(monitor)
    }

    override fun setListener(listener: NetworkMonitor.Listener) {
        this.listener = listener
    }
}
