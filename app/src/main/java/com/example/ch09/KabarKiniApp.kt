package com.example.ch09

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ch09.ui.news.NewsRoute
import com.example.ch09.ui.openUrl

@Composable
fun KabarKiniApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.News.route
    ) {
        composable(Routes.News.route) {
            val context = LocalContext.current
            NewsRoute(
                // TODO [T2.5] Jangan langsung membuka browser: navigasi ke layar detail
                //   navController.navigate(Routes.Detail.createRoute(article.url))
                onArticleClick = { article -> openUrl(context, article.url) }
            )
        }

        // TODO [T2.2] composable(Routes.Detail.route, arguments = listOf(navArgument(...) {
        //   type = NavType.StringType })) { entry -> ArticleDetailRoute(articleUrl = ...,
        //   onNavigateBack = { navController.popBackStack() }) }
    }
}
