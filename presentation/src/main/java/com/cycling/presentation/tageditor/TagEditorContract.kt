package com.cycling.presentation.tageditor

import com.cycling.domain.model.AudioMetadata
import com.cycling.domain.model.Song

data class TagEditorUiState(
    val song: Song? = null,
    val audioMetadata: AudioMetadata? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val editedTitle: String = "",
    val editedArtist: String = "",
    val editedAlbum: String = "",
    val editedYear: String = "",
    val editedGenre: String = "",
    val editedComposer: String = "",
    val hasChanges: Boolean = false,
    val showDiscardDialog: Boolean = false,
    val error: String? = null
)

sealed interface TagEditorIntent {
    data class LoadSong(val songId: Long) : TagEditorIntent
    data class TitleChanged(val title: String) : TagEditorIntent
    data class ArtistChanged(val artist: String) : TagEditorIntent
    data class AlbumChanged(val album: String) : TagEditorIntent
    data class YearChanged(val year: String) : TagEditorIntent
    data class GenreChanged(val genre: String) : TagEditorIntent
    data class ComposerChanged(val composer: String) : TagEditorIntent
    data object Save : TagEditorIntent
    data object DiscardChanges : TagEditorIntent
    data object KeepEditing : TagEditorIntent
    data object BackPressed : TagEditorIntent
}

sealed interface TagEditorEffect {
    data object NavigateBack : TagEditorEffect
    data class ShowToast(val message: String) : TagEditorEffect
    data class ShowError(val message: String) : TagEditorEffect
}
