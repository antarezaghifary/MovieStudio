package com.antareza.movieholic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.antareza.movieholic.ui.detail.MovieDetailScreen
import com.antareza.movieholic.ui.genre.GenreScreen
import com.antareza.movieholic.ui.movie.MovieScreen
import com.antareza.movieholic.ui.theme.MovieStudioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieStudioTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "movie?genreId={genreId}") {
                        composable("genre") {
                            GenreScreen(navController)
                        }
                        composable(
                            route = "movie?genreId={genreId}",
                            arguments = listOf(navArgument("genreId") { 
                                type = NavType.IntType 
                                defaultValue = 0 
                            })
                        ) {
                            MovieScreen(navController)
                        }
                        composable(
                            "movie/detail/{movieId}",
                            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                        ) {
                            MovieDetailScreen(navController)
                        }
                        composable(
                            "review/{movieId}",
                            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                        ) {
                            com.antareza.movieholic.ui.review.ReviewScreen(navController)
                        }
                    }
                }
            }
        }
    }
}