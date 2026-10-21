package com.example.ch09

sealed class Routes(val route: String) {
    data object News : Routes("news")

    // TODO [T2.2] Tambahkan route layar detail, mis. "detail/{articleUrl}", dengan
    //   fun createRoute(articleUrl: String) = "detail/${Uri.encode(articleUrl)}".
    //   URL artikel berisi "/", "?", "&": WAJIB di-encode (android.net.Uri.encode)
    //   agar tidak merusak pola route. Navigation men-decode-nya kembali otomatis.
}
