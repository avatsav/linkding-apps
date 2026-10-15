package dev.avatsav.linkding.data.db.room.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveBookmark
import kotlin.time.Instant

@Entity(tableName = "pending_bookmarks")
data class PendingBookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: String,
    val url: String,
    val title: String?,
    val description: String?,
    val notes: String?,
    val tags: Set<String>,
    val archived: Boolean,
    val unread: Boolean,
    val shared: Boolean,
    val createdAt: Instant,
    val state: String,
    val lastError: String?,
)

fun PendingBookmarkEntity.toModel() =
    PendingBookmark(
        id = id,
        bookmark = SaveBookmark(url, title, description, notes, tags, archived, unread, shared),
        createdAt = createdAt,
        state = PendingBookmark.State.valueOf(state),
        lastError = lastError,
    )

fun PendingBookmark.toRoomEntity(accountId: String) =
    PendingBookmarkEntity(
        id = id,
        accountId = accountId,
        url = bookmark.url,
        title = bookmark.title,
        description = bookmark.description,
        notes = bookmark.notes,
        tags = bookmark.tags,
        archived = bookmark.archived,
        unread = bookmark.unread,
        shared = bookmark.shared,
        createdAt = createdAt,
        state = state.name,
        lastError = lastError,
    )
