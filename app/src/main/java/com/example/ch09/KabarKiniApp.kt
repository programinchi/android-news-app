package com.example.ch09

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ch09.ui.detail.ArticleDetailRoute
import com.example.ch09.ui.news.NewsRoute

@Composable
fun KabarKiniApp() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.News.route
    ) {

        composable(Routes.News.route) {
            NewsRoute(
                onArticleClick = { article ->
                    navController.navigate(
                        Routes.Detail.createRoute(article.url)
                    )
                }
            )
        }

        composable(
            route = Routes.Detail.route,
            arguments = listOf(
                navArgument("articleUrl") {
                    type = NavType.StringType
                }
            )
        ) { entry ->

            val articleUrl =
                entry.arguments?.getString("articleUrl").orEmpty()

            ArticleDetailRoute(
                articleUrl = articleUrl,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}