package com.cycling.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.repository.PlayerRepository
import com.cycling.domain.usecase.GetAllAlbumsUseCase
import com.cycling.domain.usecase.GetAllArtistsUseCase
import com.cycling.domain.usecase.GetAllSongsUseCase
import com.cycling.domain.usecase.GetFavoriteSongsUseCase
import com.cycling.domain.usecase.GetMostPlayedSongsUseCase
import com.cycling.domain.usecase.GetRecentlyPlayedSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllSongsUseCase: GetAllSongsUseCase,
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val getAllArtistsUseCase: GetAllArtistsUseCase,
    private val getFavoriteSongsUseCase: GetFavoriteSongsUseCase,
    private val getMostPlayedSongsUseCase: GetMostPlayedSongsUseCase,
    private val getRecentlyPlayedSongsUseCase: GetRecentlyPlayedSongsUseCase,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    val playerState = playerRepository.playerState

    private val _uiEffect = MutableSharedFlow<HomeEffect>()
    val uiEffect: SharedFlow<HomeEffect> = _uiEffect.asSharedFlow()

    private var songsLoaded = false
    private var albumsLoaded = false
    private var artistsLoaded = false
    private var favoritesLoaded = false
    private var mostPlayedLoaded = false
    private var recentlyPlayedLoaded = false

    init {
        loadData()
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadData -> loadData()
            is HomeIntent.SongClick -> handleSongClick(intent.song)
            is HomeIntent.AlbumClick -> handleAlbumClick(intent.album)
            is HomeIntent.ArtistClick -> handleArtistClick(intent.artist)
            is HomeIntent.NavigateToSongs -> navigateTo(HomeEffect.NavigateToSongs)
            is HomeIntent.NavigateToAlbums -> navigateTo(HomeEffect.NavigateToAlbums)
            is HomeIntent.NavigateToArtists -> navigateTo(HomeEffect.NavigateToArtists)
            is HomeIntent.NavigateToPlaylists -> navigateTo(HomeEffect.NavigateToPlaylists)
            is HomeIntent.NavigateToSettings -> navigateTo(HomeEffect.NavigateToSettings)
            is HomeIntent.NavigateToScan -> navigateTo(HomeEffect.NavigateToScan)
            is HomeIntent.NavigateToFavorites -> navigateTo(HomeEffect.NavigateToFavorites)
            is HomeIntent.NavigateToRecentlyPlayed -> navigateTo(HomeEffect.NavigateToRecentlyPlayed)
            is HomeIntent.NavigateToMostPlayed -> navigateTo(HomeEffect.NavigateToMostPlayed)
            is HomeIntent.NavigateToSearch -> navigateTo(HomeEffect.NavigateToSearch)
            is HomeIntent.PlayPause -> playerRepository.playPause()
            is HomeIntent.MiniPlayerClick -> handleMiniPlayerClick()
        }
    }
    
    private fun handleMiniPlayerClick() {
        viewModelScope.launch {
            playerRepository.getCurrentState().currentSong?.let { song ->
                _uiEffect.emit(HomeEffect.NavigateToPlayer(song.id))
            }
        }
    }

    private fun loadData() {
        songsLoaded = false
        albumsLoaded = false
        artistsLoaded = false
        favoritesLoaded = false
        mostPlayedLoaded = false
        recentlyPlayedLoaded = false
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            launch {
                getAllSongsUseCase().collectLatest { songs ->
                    _uiState.update { state ->
                        state.copy(
                            recentlyAdded = songs.sortedByDescending { it.dateAdded }.take(10)
                        )
                    }
                    songsLoaded = true
                    checkAllDataLoaded()
                }
            }
            
            launch {
                getAllAlbumsUseCase().collectLatest { albums ->
                    _uiState.update { state ->
                        state.copy(topAlbums = albums.take(10))
                    }
                    albumsLoaded = true
                    checkAllDataLoaded()
                }
            }
            
            launch {
                getAllArtistsUseCase().collectLatest { artists ->
                    _uiState.update { state ->
                        state.copy(topArtists = artists.take(10))
                    }
                    artistsLoaded = true
                    checkAllDataLoaded()
                }
            }

            launch {
                getFavoriteSongsUseCase().collectLatest { favorites ->
                    _uiState.update { state ->
                        state.copy(favoriteSongs = favorites.take(10))
                    }
                    favoritesLoaded = true
                    checkAllDataLoaded()
                }
            }

            launch {
                getMostPlayedSongsUseCase().collectLatest { mostPlayed ->
                    _uiState.update { state ->
                        state.copy(mostPlayed = mostPlayed.take(10))
                    }
                    mostPlayedLoaded = true
                    checkAllDataLoaded()
                }
            }

            launch {
                getRecentlyPlayedSongsUseCase().collectLatest { recentlyPlayed ->
                    _uiState.update { state ->
                        state.copy(recentlyPlayed = recentlyPlayed.take(10))
                    }
                    recentlyPlayedLoaded = true
                    checkAllDataLoaded()
                }
            }
        }
    }
    
    private fun checkAllDataLoaded() {
        if (songsLoaded && albumsLoaded && artistsLoaded && favoritesLoaded && mostPlayedLoaded && recentlyPlayedLoaded) {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun handleSongClick(song: com.cycling.domain.model.Song) {
        viewModelScope.launch {
            _uiEffect.emit(HomeEffect.NavigateToPlayer(song.id))
        }
    }

    private fun handleAlbumClick(album: com.cycling.domain.model.Album) {
        viewModelScope.launch {
            _uiEffect.emit(HomeEffect.NavigateToAlbumDetail(album.id))
        }
    }

    private fun handleArtistClick(artist: com.cycling.domain.model.Artist) {
        viewModelScope.launch {
            _uiEffect.emit(HomeEffect.NavigateToArtistDetail(artist.id))
        }
    }

    private fun navigateTo(effect: HomeEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }
}
