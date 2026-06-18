package com.yaleiden.nwsdata.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.yaleiden.nwsdata.wear.presentation.theme.EsFISHWXTheme

class MainActivity : ComponentActivity() {

    private val viewModel: WearLakeLevelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            EsFISHWXTheme {
                AppScaffold {
                    WearApp(
                        uiState = uiState,
                        onRefreshClick = { viewModel.fetchLatestLevel() }
                    )
                }
            }
        }
    }
}

@Composable
fun WearApp(
    uiState: WearUiState,
    onRefreshClick: () -> Unit
) {
    val columnState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(scrollState = columnState) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxWidth()
        ) {


            // --- New Site Name Subtitle Section ---
            item {
                if (uiState is WearUiState.Success) {
                    Text(
                        text = uiState.siteName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .transformedHeight(this, transformationSpec)
                    )
                }
            }

            item {
                when (uiState) {
                    is WearUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .transformedHeight(this, transformationSpec)
                        )
                    }
                    is WearUiState.Success -> {
                        Text(
                            text = uiState.latestLevel,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .transformedHeight(this, transformationSpec)
                        )
                    }
                    is WearUiState.Error -> {
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .transformedHeight(this, transformationSpec)
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = onRefreshClick,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 32.dp, vertical = 4.dp)
                        .transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Text(
                        text = "Refresh",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// --- Previews updated with Mock Data ---

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MainScreenSuccessPreview() {
    EsFISHWXTheme {
        AppScaffold {
            WearApp(
                uiState = WearUiState.Success(
                    siteName = "Clarks Hill near Plumb Branch",
                    latestLevel = "324.58 ft"
                ),
                onRefreshClick = {}
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun MainScreenLoadingPreview() {
    EsFISHWXTheme {
        AppScaffold {
            WearApp(
                uiState = WearUiState.Loading,
                onRefreshClick = {}
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun MainScreenErrorPreview() {
    EsFISHWXTheme {
        AppScaffold {
            WearApp(
                uiState = WearUiState.Error("Network Timeout"),
                onRefreshClick = {}
            )
        }
    }
}