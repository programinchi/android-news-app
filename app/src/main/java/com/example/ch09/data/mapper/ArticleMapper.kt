package com.example.ch09.data.mapper

import com.example.ch09.data.model.Article
import com.example.ch09.data.remote.dto.ArticleDto

// NewsAPI menandai artikel yang sudah dihapus penerbitnya dengan "[Removed]"
private const val REMOVED = "[Removed]"

// Potongan "… [+1234 chars]" di akhir content (batas paket gratis NewsAPI)
private val TRUNCATION_SUFFIX = Regex("""\s*\[\+\d+ chars]$""")

// DTO -> domain. Mengembalikan null untuk artikel yang tidak layak tampil
// (tanpa judul/url, atau sudah dihapus), sehingga dipakai dengan mapNotNull.
fun ArticleDto.toDomain(): Article? {
    val title = title?.trim().orEmpty()
    val url = url?.trim().orEmpty()
    if (title.isEmpty() || url.isEmpty() || title == REMOVED) return null

    return Article(
        title = title,
        author = author?.takeIf { it.isNotBlank() } ?: "Anonim",
        description = description?.trim().orEmpty(),
        content = content.orEmpty().replace(TRUNCATION_SUFFIX, "").trim(),
        url = url,
        imageUrl = imageUrl?.takeIf { it.isNotBlank() },
        publishedAt = publishedAt.orEmpty(),
        sourceName = source?.name?.takeIf { it.isNotBlank() } ?: "Tanpa sumber"
    )
}

// TODO [T4.4] Tambahkan mapper untuk cache Room:
//   fun Article.toEntity(category: String, position: Int, cachedAt: Long): ArticleEntity
//   fun ArticleEntity.toDomain(): Article
//   position menjaga urutan artikel seperti dari server.
