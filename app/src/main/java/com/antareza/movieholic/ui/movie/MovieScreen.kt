package com.antareza.movieholic.ui.movie

import com.antareza.movieholic.ui.components.ErrorView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.antareza.movieholic.domain.model.Genre
import com.antareza.movieholic.domain.model.Movie
import com.antareza.movieholic.ui.movie.state.MovieState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(
    navController: NavController,
    viewModel: MovieViewModel = hiltViewModel()
) {
    val movieState = viewModel.movieState.value
    val genres = viewModel.genres.value
    val selectedGenre = viewModel.selectedGenre.value
    val searchQuery = viewModel.searchQuery.value
    val isSearching = viewModel.isSearching.value
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = { Text("Search movies...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = { focusManager.clearFocus() }
                            )
                        )
                    } else {
                        Text(
                            text = "Movies",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onSearchIconClicked() }) {
                        Icon(
                            imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (isSearching) "Close search" else "Search"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (!isSearching && genres.isNotEmpty()) {
                    // Genre Selector replacing Recommendation Tabs
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(genres) { genre ->
                            val isSelected = selectedGenre?.id == genre.id
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { viewModel.onGenreSelected(genre) }
                            ) {
                                Text(
                                    text = genre.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .height(3.dp)
                                            .width(16.dp)
                                            .background(Color.Blue, RoundedCornerShape(1.5.dp))
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(3.dp))
                                }
                            }
                        }
                    }
                }

                when (movieState) {
                    is MovieState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is MovieState.Success -> {
                        if (movieState.movies.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "No movies found", color = Color.Gray)
                            }
                        } else {
                            MovieList(
                                movies = movieState.movies,
                                onLoadMore = {
                                    viewModel.loadMoreMovies()
                                },
                                onItemClick = { movieId ->
                                    navController.navigate("movie/detail/$movieId")
                                }
                            )
                        }
                    }
                    is MovieState.Error -> {
                        ErrorView(
                            message = movieState.message,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieList(
    movies: List<Movie>,
    onLoadMore: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (movies.isNotEmpty()) {
            item {
                val heroMovie = movies.first()
                HeroMovieItem(
                    movie = heroMovie,
                    onItemClick = { onItemClick(heroMovie.id) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            items(movies.drop(1)) { movie ->
                MovieListItem(
                    movie = movie,
                    onItemClick = { onItemClick(movie.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    val layoutInfo = listState.layoutInfo
    val totalItems = layoutInfo.totalItemsCount
    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

    LaunchedEffect(lastVisibleItemIndex, totalItems) {
        if (lastVisibleItemIndex >= totalItems - 1 && totalItems > 0) {
            onLoadMore()
        }
    }
}

@Composable
fun HeroMovieItem(
    movie: Movie,
    onItemClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onItemClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.posterPath}"),
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MovieListItem(
    movie: Movie,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.posterPath}"),
            contentDescription = movie.title,
            modifier = Modifier
                .width(100.dp)
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = movie.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mock 5 stars based on voteAverage
                val rating = movie.voteAverage / 2
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = if (i <= rating) Color(0xFFFFC107) else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format("%.1f", movie.voteAverage),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Movie", // Simplified genre for list view
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Release: ${movie.releaseDate}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}