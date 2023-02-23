package dev.avatsav.linkding.data.tags

import arrow.core.Either
import arrow.core.left
import dev.avatsav.linkding.data.configuration.ConfigurationStore
import dev.avatsav.linkding.domain.Tag
import dev.avatsav.linkding.domain.TagError
import dev.avatsav.linkding.domain.TagList

interface TagsRepository {

    suspend fun get(
        offset: Int = 0,
        limit: Int = 50,
        query: String = "",
    ): Either<TagError, TagList>

    suspend fun save(tag: Tag): Either<TagError, Tag>

    suspend fun get(tagId: Long): Either<TagError, Tag>
}

class LinkdingTagsRepository(
    private val tagsDataSource: TagsDataSource,
    private val configurationStore: ConfigurationStore,
) : TagsRepository {

    override suspend fun get(
        offset: Int,
        limit: Int,
        query: String,
    ): Either<TagError, TagList> {
        return configurationStore.get().toEither().map { credentials ->
            return tagsDataSource.fetch(
                credentials.url,
                credentials.apiKey,
                offset,
                limit,
                query,
            )
        }.mapLeft { return@get TagError.ConfigurationNotSetup.left() }
    }

    override suspend fun get(tagId: Long): Either<TagError, Tag> {
        TODO("Not yet implemented")
    }

    override suspend fun save(tag: Tag): Either<TagError, Tag> {
        TODO("Not yet implemented")
    }
}
