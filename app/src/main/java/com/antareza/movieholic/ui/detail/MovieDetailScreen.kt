package com.antareza.movieholic.ui.detail

import com.antareza.movieholic.ui.components.ErrorView
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.antareza.movieholic.domain.model.Movie
import com.antareza.movieholic.ui.detail.state.MovieDetailState
import com.antareza.movieholic.ui.detail.state.ReviewState
import com.antareza.movieholic.ui.detail.state.VideoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    navController: NavController,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val movieDetailState = viewModel.movieDetailState.value
    val reviewState = viewModel.reviewState.value
    val videoState = viewModel.videoState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
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
            when (movieDetailState) {
                is MovieDetailState.Loading -> {
                    CircularProgressIndicator()
                }
                is MovieDetailState.Success -> {
                    MovieDetailContent(
                        movie = movieDetailState.movie,
                        reviewState = reviewState,
                        videoState = videoState,
                        onShowAllReviewsClick = { navController.navigate("review/${movieDetailState.movie.id}") }
                    )
                }
                is MovieDetailState.Error -> {
                    ErrorView(
                        message = movieDetailState.message,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun MovieDetailContent(
    movie: Movie,
    reviewState: ReviewState,
    videoState: VideoState,
    onShowAllReviewsClick: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.posterPath}"),
                    contentDescription = movie.title,
                    modifier = Modifier
                        .width(100.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = movie.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Text("Science fiction, Action", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("1h 52min", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${movie.releaseDate.take(4)} · America", fontSize = 14.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Language", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("English", fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Column {
                    Text("Comments", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    val commentsCount = if (reviewState is ReviewState.Success) {
                        reviewState.reviews.size.toString()
                    } else {
                        "0"
                    }
                    Text(commentsCount, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Column {
                    Text("Collection", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("1988", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Synopsis
        item {
            Text(
                text = "Synopsis",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = movie.overview,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Video Stills
        item {
            Text(
                text = "Video stills",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            when (videoState) {
                is VideoState.Loading -> CircularProgressIndicator()
                is VideoState.Error -> ErrorView(message = "Failed to load videos")
                is VideoState.Success -> {
                    val trailers = videoState.videos.filter { it.type == "Trailer" && it.site == "YouTube" }
                    if (trailers.isEmpty()) {
                        Text("No trailers available", color = Color.Gray)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(trailers) { video ->
                                Box(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://www.youtube.com/watch?v=${video.key}")
                                            )
                                            context.startActivity(intent)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter("https://img.youtube.com/vi/${video.key}/hqdefault.jpg"),
                                        contentDescription = "Trailer thumbnail",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(Color.White.copy(alpha = 0.8f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            tint = Color.Black,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Reviews replacing List of Actors
        item {
            Text(
                text = "Reviews", // Using reviews instead of actors as requested originally
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        when (reviewState) {
            is ReviewState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
            is ReviewState.Error -> {
                item { ErrorView(message = "Failed to load reviews") }
            }
            is ReviewState.Success -> {
                if (reviewState.reviews.isEmpty()) {
                    item { Text("No reviews available", color = Color.Gray) }
                } else {
                    val displayReviews = reviewState.reviews.take(5)
                    items(displayReviews) { review ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = review.author,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = review.content,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 20.sp
                            )
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                    }
                    if (reviewState.reviews.size >= 5) { // Assuming there might be more if we get 5
                        item {
                            TextButton(
                                onClick = { onShowAllReviewsClick() },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Text("Show all ${reviewState.reviews.size} data", color = Color(0xFF0D6EFD), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom button
        }
    }
}