package dev.avatsav.linkding.data.tags

import arrow.core.Either
import dev.avatsav.linkding.domain.BookmarkError
import dev.avatsav.linkding.domain.Tag
import dev.avatsav.linkding.domain.TagError
import dev.avatsav.linkding.domain.TagList

interface TagService {
    suspend fun get(limit: Int = 50, offset: Int): Either<BookmarkError, TagList>

    suspend fun save(tag: Tag): Either<TagError, Tag>

    suspend fun get(tagId: Long): Either<TagError, Tag>
}

class LinkdingTagService : TagService {
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