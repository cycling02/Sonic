package com.cycling.presentation.tageditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.model.TagUpdate
import com.cycling.domain.repository.AudioMetadataRepository
import com.cycling.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TagEditorViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val audioMetadataRepository: AudioMetadataRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val songId: Long = savedStateHandle.get<Long>("songId") ?: -1L

    private val _uiState = MutableStateFlow(TagEditorUiState())
    val uiState: StateFlow<TagEditorUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<TagEditorEffect>()
    val uiEffect: SharedFlow<TagEditorEffect> = _uiEffect.asSharedFlow()

    init {
        loadSong()
    }

    fun handleIntent(intent: TagEditorIntent) {
        when (intent) {
            is TagEditorIntent.LoadSong -> loadSong()
            is TagEditorIntent.TitleChanged -> updateTitle(intent.title)
            is TagEditorIntent.ArtistChanged -> updateArtist(intent.artist)
            is TagEditorIntent.AlbumChanged -> updateAlbum(intent.album)
            is TagEditorIntent.YearChanged -> updateYear(intent.year)
            is TagEditorIntent.GenreChanged -> updateGenre(intent.genre)
            is TagEditorIntent.ComposerChanged -> updateComposer(intent.composer)
            is TagEditorIntent.Save -> save()
            is TagEditorIntent.DiscardChanges -> discardChanges()
            is TagEditorIntent.KeepEditing -> keepEditing()
            is TagEditorIntent.BackPressed -> handleBackPressed()
        }
    }

    private fun loadSong() {
        Timber.d("loadSong: starting songId=$songId")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val song = songRepository.getSongById(songId)
            if (song != null) {
                Timber.d("loadSong: loaded song ${song.title}")
                val audioMetadata = audioMetadataRepository.getAudioMetadata(song.path)
                _uiState.update {
                    it.copy(
                        song = song,
                        audioMetadata = audioMetadata,
                        isLoading = false,
                        editedTitle = song.title,
                        editedArtist = song.artist,
                        editedAlbum = song.album,
                        editedYear = audioMetadata?.year ?: "",
                        editedGenre = audioMetadata?.genre ?: "",
                        editedComposer = audioMetadata?.composer ?: "",
                        hasChanges = false
                    )
                }
            } else {
                Timber.d("loadSong: song not found")
                _uiState.update { it.copy(isLoading = false, error = "歌曲未找到") }
            }
        }
    }

    private fun updateTitle(title: String) {
        val maxLength = 200
        val trimmedTitle = title.take(maxLength)
        _uiState.update { state ->
            state.copy(
                editedTitle = trimmedTitle,
                hasChanges = checkForChanges(state.copy(editedTitle = trimmedTitle))
            )
        }
    }

    private fun updateArtist(artist: String) {
        val maxLength = 200
        val trimmedArtist = artist.take(maxLength)
        _uiState.update { state ->
            state.copy(
                editedArtist = trimmedArtist,
                hasChanges = checkForChanges(state.copy(editedArtist = trimmedArtist))
            )
        }
    }

    private fun updateAlbum(album: String) {
        val maxLength = 200
        val trimmedAlbum = album.take(maxLength)
        _uiState.update { state ->
            state.copy(
                editedAlbum = trimmedAlbum,
                hasChanges = checkForChanges(state.copy(editedAlbum = trimmedAlbum))
            )
        }
    }

    private fun updateYear(year: String) {
        val filteredYear = year.filter { it.isDigit() }.take(4)
        _uiState.update { state ->
            state.copy(
                editedYear = filteredYear,
                hasChanges = checkForChanges(state.copy(editedYear = filteredYear))
            )
        }
    }

    private fun updateGenre(genre: String) {
        val maxLength = 100
        val trimmedGenre = genre.take(maxLength)
        _uiState.update { state ->
            state.copy(
                editedGenre = trimmedGenre,
                hasChanges = checkForChanges(state.copy(editedGenre = trimmedGenre))
            )
        }
    }

    private fun updateComposer(composer: String) {
        val maxLength = 200
        val trimmedComposer = composer.take(maxLength)
        _uiState.update { state ->
            state.copy(
                editedComposer = trimmedComposer,
                hasChanges = checkForChanges(state.copy(editedComposer = trimmedComposer))
            )
        }
    }

    private fun checkForChanges(state: TagEditorUiState): Boolean {
        val song = state.song ?: return false
        val metadata = state.audioMetadata

        return state.editedTitle != song.title ||
                state.editedArtist != song.artist ||
                state.editedAlbum != song.album ||
                state.editedYear != (metadata?.year ?: "") ||
                state.editedGenre != (metadata?.genre ?: "") ||
                state.editedComposer != (metadata?.composer ?: "")
    }

    private fun save() {
        val song = _uiState.value.song ?: return
        val metadata = _uiState.value.audioMetadata

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val titleChanged = _uiState.value.editedTitle != song.title
            val artistChanged = _uiState.value.editedArtist != song.artist
            val albumChanged = _uiState.value.editedAlbum != song.album

            if (titleChanged || artistChanged || albumChanged) {
                val updateSuccess = songRepository.updateSongInfo(
                    songId = song.id,
                    title = if (titleChanged) _uiState.value.editedTitle else null,
                    artist = if (artistChanged) _uiState.value.editedArtist else null,
                    album = if (albumChanged) _uiState.value.editedAlbum else null
                )
                if (!updateSuccess) {
                    _uiState.update { it.copy(isSaving = false, error = "更新歌曲信息失败") }
                    _uiEffect.emit(TagEditorEffect.ShowError("更新歌曲信息失败"))
                    return@launch
                }
            }

            val yearChanged = _uiState.value.editedYear != (metadata?.year ?: "")
            val genreChanged = _uiState.value.editedGenre != (metadata?.genre ?: "")
            val composerChanged = _uiState.value.editedComposer != (metadata?.composer ?: "")

            if (yearChanged || genreChanged || composerChanged || titleChanged || artistChanged || albumChanged) {
                val tagUpdate = TagUpdate(
                    title = if (titleChanged) _uiState.value.editedTitle else null,
                    artist = if (artistChanged) _uiState.value.editedArtist else null,
                    album = if (albumChanged) _uiState.value.editedAlbum else null,
                    year = if (yearChanged) _uiState.value.editedYear.ifEmpty { null } else null,
                    genre = if (genreChanged) _uiState.value.editedGenre.ifEmpty { null } else null,
                    composer = if (composerChanged) _uiState.value.editedComposer.ifEmpty { null } else null
                )

                val result = audioMetadataRepository.updateAudioTags(song.path, tagUpdate)
                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(isSaving = false, hasChanges = false) }
                        _uiEffect.emit(TagEditorEffect.ShowToast("保存成功"))
                        _uiEffect.emit(TagEditorEffect.NavigateBack)
                    },
                    onFailure = { error ->
                        Timber.e(error, "Failed to update audio tags")
                        _uiState.update { it.copy(isSaving = false, error = "保存标签失败: ${error.message}") }
                        _uiEffect.emit(TagEditorEffect.ShowError("保存标签失败"))
                    }
                )
            } else {
                _uiState.update { it.copy(isSaving = false, hasChanges = false) }
                _uiEffect.emit(TagEditorEffect.NavigateBack)
            }
        }
    }

    private fun discardChanges() {
        viewModelScope.launch {
            _uiState.update { it.copy(showDiscardDialog = false, hasChanges = false) }
            _uiEffect.emit(TagEditorEffect.NavigateBack)
        }
    }

    private fun keepEditing() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    private fun handleBackPressed() {
        if (_uiState.value.hasChanges) {
            _uiState.update { it.copy(showDiscardDialog = true) }
        } else {
            viewModelScope.launch {
                _uiEffect.emit(TagEditorEffect.NavigateBack)
            }
        }
    }
}
