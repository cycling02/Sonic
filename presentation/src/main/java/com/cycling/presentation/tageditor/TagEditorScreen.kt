package com.cycling.presentation.tageditor

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.cycling.presentation.R
import com.cycling.presentation.components.IOSFilledButton
import com.cycling.presentation.components.IOSTopAppBar
import com.cycling.presentation.theme.SonicColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagEditorScreen(
    onNavigateBack: () -> Unit,
    viewModel: TagEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is TagEditorEffect.NavigateBack -> onNavigateBack()
                is TagEditorEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is TagEditorEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    if (uiState.showDiscardDialog) {
        DiscardChangesDialog(
            onDiscard = { viewModel.handleIntent(TagEditorIntent.DiscardChanges) },
            onKeepEditing = { viewModel.handleIntent(TagEditorIntent.KeepEditing) }
        )
    }

    Scaffold(
        topBar = {
            IOSTopAppBar(
                title = stringResource(R.string.tag_editor_title),
                onNavigateBack = { viewModel.handleIntent(TagEditorIntent.BackPressed) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.song != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                SongHeader(
                    albumArt = uiState.song!!.albumArt,
                    title = uiState.editedTitle,
                    artist = uiState.editedArtist
                )

                Spacer(modifier = Modifier.height(24.dp))

                TagEditorFields(
                    title = uiState.editedTitle,
                    artist = uiState.editedArtist,
                    album = uiState.editedAlbum,
                    year = uiState.editedYear,
                    genre = uiState.editedGenre,
                    composer = uiState.editedComposer,
                    onTitleChange = { viewModel.handleIntent(TagEditorIntent.TitleChanged(it)) },
                    onArtistChange = { viewModel.handleIntent(TagEditorIntent.ArtistChanged(it)) },
                    onAlbumChange = { viewModel.handleIntent(TagEditorIntent.AlbumChanged(it)) },
                    onYearChange = { viewModel.handleIntent(TagEditorIntent.YearChanged(it)) },
                    onGenreChange = { viewModel.handleIntent(TagEditorIntent.GenreChanged(it)) },
                    onComposerChange = { viewModel.handleIntent(TagEditorIntent.ComposerChanged(it)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                ActionButtons(
                    hasChanges = uiState.hasChanges,
                    isSaving = uiState.isSaving,
                    onSave = { viewModel.handleIntent(TagEditorIntent.Save) },
                    onCancel = { viewModel.handleIntent(TagEditorIntent.BackPressed) }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error ?: stringResource(R.string.tag_editor_song_not_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SongHeader(
    albumArt: String?,
    title: String,
    artist: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (albumArt != null) {
                AsyncImage(
                    model = albumArt,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = SonicColors.Red
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = artist,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TagEditorFields(
    title: String,
    artist: String,
    album: String,
    year: String,
    genre: String,
    composer: String,
    onTitleChange: (String) -> Unit,
    onArtistChange: (String) -> Unit,
    onAlbumChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onGenreChange: (String) -> Unit,
    onComposerChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        TagTextField(
            label = stringResource(R.string.tag_editor_title_label),
            value = title,
            onValueChange = onTitleChange,
            placeholder = stringResource(R.string.tag_editor_title_placeholder)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TagTextField(
            label = stringResource(R.string.tag_editor_artist_label),
            value = artist,
            onValueChange = onArtistChange,
            placeholder = stringResource(R.string.tag_editor_artist_placeholder)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TagTextField(
            label = stringResource(R.string.tag_editor_album_label),
            value = album,
            onValueChange = onAlbumChange,
            placeholder = stringResource(R.string.tag_editor_album_placeholder)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TagTextField(
            label = stringResource(R.string.tag_editor_year_label),
            value = year,
            onValueChange = onYearChange,
            placeholder = stringResource(R.string.tag_editor_year_placeholder),
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        TagTextField(
            label = stringResource(R.string.tag_editor_genre_label),
            value = genre,
            onValueChange = onGenreChange,
            placeholder = stringResource(R.string.tag_editor_genre_placeholder)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TagTextField(
            label = stringResource(R.string.tag_editor_composer_label),
            value = composer,
            onValueChange = onComposerChange,
            placeholder = stringResource(R.string.tag_editor_composer_placeholder)
        )
    }
}

@Composable
private fun TagTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun ActionButtons(
    hasChanges: Boolean,
    isSaving: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        IOSFilledButton(
            text = if (isSaving) stringResource(R.string.tag_editor_saving) else stringResource(R.string.tag_editor_save),
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = if (hasChanges && !isSaving) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.tag_editor_cancel),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DiscardChangesDialog(
    onDiscard: () -> Unit,
    onKeepEditing: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onKeepEditing,
        title = { Text(text = stringResource(R.string.tag_editor_discard_title)) },
        text = { Text(text = stringResource(R.string.tag_editor_discard_message)) },
        confirmButton = {
            TextButton(onClick = onDiscard) {
                Text(
                    text = stringResource(R.string.tag_editor_discard),
                    color = SonicColors.Red
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onKeepEditing) {
                Text(text = stringResource(R.string.tag_editor_keep_editing))
            }
        }
    )
}
