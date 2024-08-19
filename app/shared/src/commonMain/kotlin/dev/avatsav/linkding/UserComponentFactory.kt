package dev.avatsav.linkding

import dev.avatsav.linkding.data.model.ApiConfig

interface UserComponentFactory {
    fun create(apiConfig: ApiConfig): SharedUserComponent
}
