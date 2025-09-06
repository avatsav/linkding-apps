package dev.avatsav.linkding.inject

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.data.model.ApiConfig
import dev.zacsweers.metro.SingleIn

@SingleIn(UserScope::class)
interface UserComponent {

  val uiFactories: Set<Ui.Factory>

  val presenterFactories: Set<Presenter.Factory>

  interface Factory {
    fun create(apiConfig: ApiConfig): UserComponent
  }
}
