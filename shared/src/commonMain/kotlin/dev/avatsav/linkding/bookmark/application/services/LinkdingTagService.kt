package dev.avatsav.linkding.bookmark.application.services

import arrow.core.Either
import dev.avatsav.linkding.bookmark.application.ports.`in`.TagService
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.Tag
import dev.avatsav.linkding.bookmark.domain.TagError
import dev.avatsav.linkding.bookmark.domain.TagList

class LinkdingTagService: TagService {
    override suspend fun get(limit: Int, offset: Int): Either<BookmarkError, TagList> {
        TODO("Not yet implemented")
    }

    override suspend fun get(tagId: Long): Either<TagError, Tag> {
        TODO("Not yet implemented")
    }

    override suspend fun save(tag: Tag): Either<TagError, Tag> {
        TODO("Not yet implemented")
    }
}