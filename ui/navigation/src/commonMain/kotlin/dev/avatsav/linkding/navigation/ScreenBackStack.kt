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
 * A mutable back stack of [Screen] elements that integrates with Compose state.
 *
 * This class wraps a [SnapshotStateList] so that updates to the stack automatically trigger
 * recomposition in any observing Composables. It also implements [StateObject], which allows it to
 * participate in Compose's snapshot system directly.
 *
 * Use [rememberScreenBackStack] to create a backstack that is automatically saved and restored
 * across process death and configuration changes.
 *
 * ## Example
 *
 * ```kotlin
 * val backStack = rememberScreenBackStack(savedStateConfig, Screen.Home())
 * backStack += Screen.Details(itemId)  // pushes onto stack
 * backStack.removeLast()               // pops stack
 * ```
 *
 * @see rememberScreenBackStack
 * @see Screen
 */
@Stable
class ScreenBackStack internal constructor(internal val base: SnapshotStateList<Screen>) :
  MutableList<Screen> by base, StateObject by base, RandomAccess {

  constructor() : this(base = mutableStateListOf())

  constructor(vararg screens: Screen) : this(base = mutableStateListOf(*screens))
}

/**
 * Creates and remembers a [ScreenBackStack] that survives process death and configuration changes.
 *
 * This function handles serialization of the backstack using the provided
 * [SavedStateConfiguration]. The configuration must include a [SerializersModule] that registers
 * all [Screen] subtypes for polymorphic serialization.
 *
 * ## Example
 *
 * ```kotlin
 * val backStack = rememberScreenBackStack(savedStateConfiguration, Screen.Home())
 *
 * NavDisplay(
 *   backStack = backStack,
 *   onBack = { navigator.pop() },
 *   ...
 * )
 * ```
 *
 * @param configuration The [SavedStateConfiguration] with a [SerializersModule] configured for
 *   [Screen] polymorphism
 * @param screens The initial screens for the backstack
 * @return A [ScreenBackStack] that survives process death and configuration changes
 * @see ScreenBackStack
 */
@Composable
fun rememberScreenBackStack(
  configuration: SavedStateConfiguration,
  vararg screens: Screen,
): ScreenBackStack {
  return rememberSerializable(
    configuration = configuration,
    serializer = ScreenBackStackSerializer,
  ) {
    ScreenBackStack(*screens)
  }
}

/**
 * Serializer for [ScreenBackStack].
 *
 * Handles serialization and deserialization of the screen backstack for saved state persistence.
 * Uses polymorphic serialization to handle different [Screen] subtypes.
 */
internal object ScreenBackStackSerializer : KSerializer<ScreenBackStack> {

  private val elementSerializer = PolymorphicSerializer(Screen::class)
  private val delegate = SnapshotStateListSerializer(elementSerializer)

  @OptIn(ExperimentalSerializationApi::class)
  override val descriptor: SerialDescriptor =
    SerialDescriptor("dev.avatsav.linkding.navigation.ScreenBackStack", delegate.descriptor)

  override fun serialize(encoder: Encoder, value: ScreenBackStack) {
    encoder.encodeSerializableValue(serializer = delegate, value = value.base)
  }

  override fun deserialize(decoder: Decoder): ScreenBackStack {
    return ScreenBackStack(base = decoder.decodeSerializableValue(deserializer = delegate))
  }
}
