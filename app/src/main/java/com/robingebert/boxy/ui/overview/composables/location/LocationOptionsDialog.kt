package com.robingebert.boxy.ui.overview.composables.location

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.ui.common.composables.ImageWithPlaceholder

enum class LocationOption {
    EDIT,
    DELETE,
}

@Composable
fun LocationOptionsDialog(
    location: Location,
    onDismiss: () -> Unit,
    onSelected: (LocationOption) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageWithPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    imageName = location.picture
                )
                Spacer(Modifier.height(24.dp))

                /*FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onSelected(LocationOption.EDIT)
                    },
                    shape = ButtonDefaults.squareShape
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Default.Edit,
                        contentDescription = ""
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(text = "Bearbeiten", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.weight(1f))
                }
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onSelected(LocationOption.DELETE)
                    },
                    shape = ButtonDefaults.squareShape,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = ""
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(text = "Löschen", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.weight(1f))
                }*/

                val size = SplitButtonDefaults.MediumContainerHeight

                SplitButtonLayout(
                    modifier = Modifier.padding(bottom = 16.dp),
                    leadingButton = {
                        SplitButtonDefaults.LeadingButton(
                            modifier = Modifier.heightIn(size),
                            shapes = SplitButtonDefaults.leadingButtonShapesFor(size),
                            contentPadding = SplitButtonDefaults.leadingButtonContentPaddingFor(size),
                            onClick = {
                                onSelected(LocationOption.EDIT)
                            }) {
                            Icon(
                                Icons.Filled.Edit,
                                modifier = Modifier.size(SplitButtonDefaults.leadingButtonIconSizeFor(size)),
                                contentDescription = null,
                            )
                            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
                            Text("Bearbeiten", style = ButtonDefaults.textStyleFor(size))
                        }
                    },
                    trailingButton = {
                        SplitButtonDefaults.TrailingButton(
                            modifier =Modifier.heightIn(size),
                            shapes = SplitButtonDefaults.trailingButtonShapesFor(size),
                            contentPadding = SplitButtonDefaults.trailingButtonContentPaddingFor(size),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            onClick = {
                                onSelected(LocationOption.DELETE)
                            }
                        ) {
                            Icon(
                                Icons.Filled.DeleteForever,
                                modifier = Modifier.size(SplitButtonDefaults.trailingButtonIconSizeFor(size)),
                                contentDescription = null,
                            )
                        }

                    },
                )
            }
        }
    }
}

@Composable
fun ListItem(modifier: Modifier = Modifier, icon: ImageVector, title: String, onClick: () -> Unit) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClick()
            }
    ) {
        Row(
            Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(modifier = Modifier.size(30.dp), imageVector = icon, contentDescription = "")
            Spacer(Modifier.width(10.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}