package com.antareza.movieholic.ui.genre

import com.antareza.movieholic.ui.components.ErrorView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.antareza.movieholic.domain.model.Genre
import com.antareza.movieholic.ui.genre.state.GenreState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreScreen(
    navController: NavController,
    viewModel: GenreViewModel = hiltViewModel()
) {
    val genreState = viewModel.genreState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Movie Genres") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (genreState) {
                is GenreState.Loading -> {
                    CircularProgressIndicator()
                }
                is GenreState.Success -> {
                    GenreList(
                        genres = genreState.genres,
                        onItemClick = { genreId ->
                            navController.navigate("movie?genreId=$genreId")
                        }
                    )
                }
                is GenreState.Error -> {
                    ErrorView(
                        message = genreState.message,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun GenreList(
    genres: List<Genre>,
    onItemClick: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(genres) { genre ->
            GenreItem(
                genre = genre,
                onItemClick = {
                    onItemClick(genre.id)
                }
            )
        }
    }
}

@Composable
fun GenreItem(
    genre: Genre,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Text(
            text = genre.name,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GenreScreenPreview() {
    val genres = listOf(
        Genre(28, "Action"),
        Genre(12, "Adventure"),
        Genre(16, "Animation"),
        Genre(35, "Comedy"),
        Genre(80, "Crime")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Genres") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding)
        ) {
            GenreList(
                genres = genres,
                onItemClick = {}
            )
        }
    }
}