package dev.avatsav.linkding.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

/**
 * Configuration for bottom sheet behavior.
 *
 * @param skipPartiallyExpanded Whether to skip the partially expanded state and expand fully.
 * @param skipHiddenState Whether the hidden state should be skipped.
 * @param modalBottomSheetProperties Properties for the modal bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
data class BottomSheetConfig(
  val skipPartiallyExpanded: Boolean = false,
  val skipHiddenState: Boolean = false,
  val modalBottomSheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
)

/** An [OverlayScene] that renders an [entry] within a [ModalBottomSheet]. */
@OptIn(ExperimentalMaterial3Api::class)
internal class BottomSheetScene<T : Any>(
  override val key: T,
  override val previousEntries: List<NavEntry<T>>,
  override val overlaidEntries: List<NavEntry<T>>,
  private val entry: NavEntry<T>,
  private val config: BottomSheetConfig,
  private val onBack: () -> Unit,
) : OverlayScene<T> {

  override val entries: List<NavEntry<T>> = listOf(entry)

  override val content: @Composable (() -> Unit) = {
    val sheetState =
      rememberModalBottomSheetState(
        skipPartiallyExpanded = config.skipPartiallyExpanded,
        confirmValueChange = { sheetValue ->
          if (config.skipHiddenState && sheetValue == SheetValue.Hidden) false else true
        },
      )
    ModalBottomSheet(
      onDismissRequest = onBack,
      sheetState = sheetState,
      properties = config.modalBottomSheetProperties,
    ) {
      entry.Content()
    }
  }
}

/**
 * A [SceneStrategy] that displays entries that have added [bottomSheet] to their
 * [NavEntry.metadata] within a [ModalBottomSheet] instance.
 *
 * This strategy should always be added before any non-overlay scene strategies.
 */
@OptIn(ExperimentalMaterial3Api::class)
class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

  override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
    val lastEntry = entries.lastOrNull()
    val config = lastEntry?.metadata?.get(BOTTOM_SHEET_KEY) as? BottomSheetConfig
    return config?.let {
      @Suppress("UNCHECKED_CAST")
      BottomSheetScene(
        key = lastEntry.contentKey as T,
        previousEntries = entries.dropLast(1),
        overlaidEntries = entries.dropLast(1),
        entry = lastEntry,
        config = it,
        onBack = onBack,
      )
    }
  }

  companion object {
    /**
     * Marks this entry to be displayed within a [ModalBottomSheet].
     *
     * @param config Configuration for the bottom sheet behavior.
     */
    fun bottomSheet(config: BottomSheetConfig = BottomSheetConfig()): Map<String, Any> =
      mapOf(BOTTOM_SHEET_KEY to config)

    /**
     * Marks this entry to be displayed within a [ModalBottomSheet] with default properties.
     *
     * @param modalBottomSheetProperties Properties for the modal bottom sheet.
     */
    fun bottomSheet(
      modalBottomSheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties()
    ): Map<String, Any> =
      mapOf(
        BOTTOM_SHEET_KEY to
          BottomSheetConfig(modalBottomSheetProperties = modalBottomSheetProperties)
      )

    /**
     * Marks this entry to be displayed within a fully expanded [ModalBottomSheet] that skips the
     * partially expanded state.
     *
     * @param modalBottomSheetProperties Properties for the modal bottom sheet.
     */
    fun bottomSheetExpanded(
      modalBottomSheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties()
    ): Map<String, Any> =
      mapOf(
        BOTTOM_SHEET_KEY to
          BottomSheetConfig(
            skipPartiallyExpanded = true,
            modalBottomSheetProperties = modalBottomSheetProperties,
          )
      )

    internal const val BOTTOM_SHEET_KEY = "bottomsheet"
  }
}
