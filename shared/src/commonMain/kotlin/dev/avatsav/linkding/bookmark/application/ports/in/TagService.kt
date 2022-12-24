package dev.avatsav.linkding.bookmark.application.ports.`in`

import arrow.core.Either
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.Tag
import dev.avatsav.linkding.bookmark.domain.TagError
import dev.avatsav.linkding.bookmark.domain.TagList

interface TagService {
    suspend fun get(
        limit: Int = 50,
        offset: Int
    ): Either<BookmarkError, TagList>

    suspend fun save(tag: Tag): Either<TagError, Tag>

    suspend fun get(tagId: Long): Either<TagError, Tag>
}