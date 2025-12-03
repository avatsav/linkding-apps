package dev.avatsav.linkding.navigation.impl

import dev.avatsav.linkding.navigation.ResultEventBus
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

internal class ResultEventBusImpl : ResultEventBus {

  private val channelMap: MutableMap<String, Channel<Any?>> = mutableMapOf()

  private fun getOrCreateChannel(resultKey: String): Channel<Any?> =
    channelMap.getOrPut(resultKey) {
      Channel(capacity = BUFFERED, onBufferOverflow = BufferOverflow.SUSPEND)
    }

  override fun getResultFlow(resultKey: String): Flow<Any?> =
    getOrCreateChannel(resultKey).receiveAsFlow()

  override fun sendResult(resultKey: String, result: Any?) {
    getOrCreateChannel(resultKey).trySend(result)
  }

  override fun removeResult(resultKey: String) {
    channelMap.remove(resultKey)?.close()
  }
}
