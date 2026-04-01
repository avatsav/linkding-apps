package dev.avatsav.linkding.data.db.room.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import dev.avatsav.linkding.data.model.Bookmark
import kotlin.time.Instant

@Entity(tableName = "bookmarks", indices = [Index(value = ["linkding_id"], unique = true)])
data class BookmarkEntity(
  @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val localId: Long = 0,
  @ColumnInfo(name = "linkding_id") val linkdingId: Long,
  val url: String,
  val urlHost: String,
  val title: String,
  val description: String,
  val archived: Boolean = false,
  val unread: Boolean = false,
  val tags: Set<String> = emptySet(),
  val added: Instant? = null,
  val modified: Instant? = null,
)

fun BookmarkEntity.toModel(): Bookmark =
  Bookmark(
    localId = localId,
    id = linkdingId,
    url = url,
    urlHost = urlHost,
    title = title,
    description = description,
    archived = archived,
    unread = unread,
    tags = tags,
    added = added,
    modified = modified,
  )

fun Bookmark.toRoomEntity(): BookmarkEntity =
  BookmarkEntity(
    localId = localId,
    linkdingId = id,
    url = url,
    urlHost = urlHost,
    title = title,
    description = description,
    archived = archived,
    unread = unread,
    tags = tags,
    added = added,
    modified = modified,
  )
