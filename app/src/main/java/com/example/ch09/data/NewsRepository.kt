package com.example.ch09.data

import com.example.ch09.data.mapper.toDomain
import com.example.ch09.data.model.Article
import com.example.ch09.data.model.NewsCategory
import com.example.ch09.data.model.NewsFeed
import com.example.ch09.data.remote.NewsApiService
import com.example.ch09.data.remote.dto.NewsResponseDto
import kotlin.coroutines.cancellation.CancellationException

interface NewsRepository {
    suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed>
    suspend fun searchNews(query: String): Result<NewsFeed>
    // Untuk layar detail (Tugas 2): artikel apa pun yang pernah dimuat
    suspend fun getArticle(url: String): Article?
}

// TODO [T4.1] Buat data/local/ArticleEntity.kt   (tabel "articles", primary key (category, url))
// TODO [T4.2] Buat data/local/ArticleDao.kt      (abstract class + @Transaction replaceCategory)
// TODO [T4.3] Buat data/local/NewsDatabase.kt    (singleton, version = 1, exportSchema = true)
// TODO [T4.5] Tambahkan parameter `dao: ArticleDao` dan
//   `clock: () -> Long = System::currentTimeMillis` (waktu cache, mudah diuji).
class NewsRepositoryImpl(
    private val api: NewsApiService
) : NewsRepository {

    // TODO [T2.1] Simpan artikel yang terakhir dimuat agar bisa dicari lewat URL,
    //   mis. `private val loaded = mutableMapOf<String, Article>()`, diisi di fetch().
    //   Setelah Tugas 4 selesai, ganti dengan Room (dao.getArticleByUrl).

    override suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed> =
        fetch { api.getTopHeadlines(category = category.apiValue) }

    override suspend fun searchNews(query: String): Result<NewsFeed> =
        fetch { api.searchNews(query = query) }

    // TODO [T2.1] Kembalikan artikel yang URL-nya cocok (sekarang selalu null,
    //   sehingga layar detail akan menampilkan "Artikel tidak ditemukan").
    override suspend fun getArticle(url: String): Article? = null

    private suspend fun fetch(remote: suspend () -> NewsResponseDto): Result<NewsFeed> {
        val result = runCatching {
            remote().articles
                .mapNotNull { it.toDomain() }
                // NewsAPI bisa mengirim url ganda; LazyColumn(key = url) akan crash
                .distinctBy { it.url }
        }

        // runCatching juga menangkap CancellationException (mis. pengguna sudah
        // pindah kategori). Itu BUKAN error jaringan: lempar ulang.
        result.exceptionOrNull()?.let { if (it is CancellationException) throw it }

        // TODO [T4.5] Strategi cache:
        //   - berhasil -> simpan ke Room (dao.replaceCategory(cacheKey, ...)), lalu kembalikan
        //   - gagal    -> ambil cache; jika ada, Result.success(NewsFeed(cache, offline =
        //                 OfflineInfo(cachedAt, error.toUserMessage()))); jika kosong, failure.
        //   fetch() perlu tahu kunci cache-nya (nama kategori, atau "__search__" untuk
        //   pencarian) dan cara membaca cache (getArticles / searchCached).
        return result.map { articles -> NewsFeed(articles) }
    }
}
