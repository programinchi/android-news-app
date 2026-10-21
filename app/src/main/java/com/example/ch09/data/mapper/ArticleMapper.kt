package com.example.ch09.data.mapper

import com.example.ch09.data.local.ArticleEntity
import com.example.ch09.data.model.Article
import com.example.ch09.data.remote.dto.ArticleDto

private const val REMOVED = "[Removed]"

private val TRUNCATION_SUFFIX =
    Regex("""\s*\[\+\d+ chars]$""")

fun ArticleDto.toDomain(): Article? {

    val cleanTitle = title?.trim().orEmpty()
    val cleanUrl = url?.trim().orEmpty()

    if (
        cleanTitle.isEmpty() ||
        cleanUrl.isEmpty() ||
        cleanTitle == REMOVED
    ) {
        return null
    }

    return Article(
        title = cleanTitle,
        author = author?.takeIf { it.isNotBlank() } ?: "Anonim",
        description = description?.trim().orEmpty(),
        content = content.orEmpty()
            .replace(TRUNCATION_SUFFIX, "")
            .trim(),
        url = cleanUrl,
        imageUrl = imageUrl?.takeIf { it.isNotBlank() },
        publishedAt = publishedAt.orEmpty(),
        sourceName = source?.name
            ?.takeIf { it.isNotBlank() }
            ?: "Tanpa sumber"
    )
}

fun Article.toEntity(
    category: String,
    position: Int,
    cachedAt: Long
): ArticleEntity {

    return ArticleEntity(
        category = category,
        url = url,
        title = title,
        author = author,
        description = description,
        content = content,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        sourceName = sourceName,
        position = position,
        cachedAt = cachedAt
    )
}

fun ArticleEntity.toDomain(): Article {

    return Article(
        title = title,
        author = author,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        sourceName = sourceName
    )
}