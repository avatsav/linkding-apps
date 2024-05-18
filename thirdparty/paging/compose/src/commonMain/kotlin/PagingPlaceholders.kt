/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.paging.compose

import dev.avatsav.linkding.parcelize.Parcelable
import dev.avatsav.linkding.parcelize.Parcelize

internal fun getPagingPlaceholderKey(index: Int): Any {
    return PagingPlaceholderKey(index)
}

internal object PagingPlaceholderContentType

/**
 * TODO: Remove this module when androidx.paging.compose adds support for other platforms.
 * Using multiplatform parcelize instead to support all platforms
 */
@Parcelize
private data class PagingPlaceholderKey(private val index: Int) : Parcelable
