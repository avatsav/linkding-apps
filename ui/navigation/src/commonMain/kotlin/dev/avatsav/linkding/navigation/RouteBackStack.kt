package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.StateObject
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Mutable backstack of [Route] elements with Compose state integration.
 *
 * ```kotlin
 * backStack += Route.Settings  // push
 * backStack.removeLast()       // pop
 * ```
 */
@Stable
class RouteBackStack internal constructor(internal val base: SnapshotStateList<Route>) :
  MutableList<Route> by base, StateObject by base, RandomAccess {

  constructor() : this(base = mutableStateListOf())

  constructor(vararg routes: Route) : this(base = mutableStateListOf(*routes))
}

/** Creates a [RouteBackStack] that survives process death and configuration changes. */
@Composable
fun rememberRouteBackStack(
  configuration: SavedStateConfiguration,
  vararg routes: Route,
): RouteBackStack {
  return rememberSerializable(
    configuration = configuration,
    serializer = RouteBackStackSerializer,
  ) {
    RouteBackStack(*routes)
  }
}

/** Serializer for [RouteBackStack] saved state persistence. */
internal object RouteBackStackSerializer : KSerializer<RouteBackStack> {

  private val elementSerializer = PolymorphicSerializer(Route::class)
  private val delegate = SnapshotStateListSerializer(elementSerializer)

  @OptIn(ExperimentalSerializationApi::class)
  override val descriptor: SerialDescriptor =
    SerialDescriptor("dev.avatsav.linkding.navigation.RouteBackStack", delegate.descriptor)

  override fun serialize(encoder: Encoder, value: RouteBackStack) {
    encoder.encodeSerializableValue(serializer = delegate, value = value.base)
  }

  override fun deserialize(decoder: Decoder): RouteBackStack {
    return RouteBackStack(base = decoder.decodeSerializableValue(deserializer = delegate))
  }
}
