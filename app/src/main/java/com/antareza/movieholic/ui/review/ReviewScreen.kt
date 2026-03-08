package com.antareza.movieholic.ui.review

import com.antareza.movieholic.ui.components.ErrorView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.antareza.movieholic.ui.detail.state.ReviewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    navController: NavController,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val reviewState = viewModel.reviewState.value
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Reviews") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            when (reviewState) {
                is ReviewState.Loading -> {
                    // Show full screen loading only on first load
                    if (viewModel.page == 1) {
                        CircularProgressIndicator()
                    }
                }
                is ReviewState.Error -> {
                    ErrorView(
                        message = "Failed to load reviews",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is ReviewState.Success -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(reviewState.reviews) { review ->
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = review.author,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = review.content,
                                    fontSize = 15.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 22.sp
                                )
                            }
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 12.dp))
                        }

                        if (viewModel.isLoading) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }

                    val layoutInfo = listState.layoutInfo
                    val totalItems = layoutInfo.totalItemsCount
                    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                    LaunchedEffect(lastVisibleItemIndex, totalItems) {
                        if (lastVisibleItemIndex >= totalItems - 1) {
                            viewModel.loadMoreReviews()
                        }
                    }
                }
            }
        }
    }
}
