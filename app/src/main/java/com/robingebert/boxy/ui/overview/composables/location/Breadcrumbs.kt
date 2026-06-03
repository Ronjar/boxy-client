package com.robingebert.boxy.ui.overview.composables.location

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Breadcrumbs(
    modifier: Modifier = Modifier,
    breadcrumbs: List<String>,
    onBreadcumbClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(breadcrumbs) {
        if (breadcrumbs.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(breadcrumbs.lastIndex)
        }
    }

    LazyRow(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(horizontal = 4.dp),
    ) {
        itemsIndexed(breadcrumbs) { index, breadcrumb ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = breadcrumb,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            scope.launch {
                                delay(200)
                                onBreadcumbClick(index)
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                if (index < breadcrumbs.lastIndex) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                    )
                }

            }
        }
    }
}

@Preview
@Composable
fun BreadcrumbsPreview() {
    Breadcrumbs(
        breadcrumbs = listOf("Probe", "Probe 2", "Probe 3")
    ) {

    }
}