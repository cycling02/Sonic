package com.cycling.data.repository

import com.cycling.data.api.DeepSeekApiService
import com.cycling.data.api.dto.DeepSeekRequest
import com.cycling.data.store.ApiKeyStore
import com.cycling.data.store.AiInfoCacheStore
import com.cycling.domain.model.AiInfo
import com.cycling.domain.model.AiInfoType
import com.cycling.domain.repository.AiRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val apiKeyStore: ApiKeyStore,
    private val deepSeekApiService: DeepSeekApiService,
    private val aiInfoCacheStore: AiInfoCacheStore
) : AiRepository {

    override suspend fun getApiKey(): String? = apiKeyStore.getApiKey()

    override suspend fun setApiKey(apiKey: String) = apiKeyStore.setApiKey(apiKey)

    override suspend fun hasApiKey(): Boolean = apiKeyStore.hasApiKey()

    override suspend fun getCachedSongInfo(songTitle: String, artist: String): AiInfo? {
        return aiInfoCacheStore.getSongInfo(songTitle, artist)
    }

    override suspend fun getCachedArtistInfo(artistName: String): AiInfo? {
        return aiInfoCacheStore.getArtistInfo(artistName)
    }

    override suspend fun getCachedAlbumInfo(albumTitle: String, artist: String): AiInfo? {
        return aiInfoCacheStore.getAlbumInfo(albumTitle, artist)
    }

    override suspend fun getSongInfo(songTitle: String, artist: String): Result<AiInfo> {
        val cached = getCachedSongInfo(songTitle, artist)
        if (cached != null) {
            return Result.success(cached)
        }
        
        val apiKey = getApiKey() ?: return Result.failure(Exception("API Key 未配置"))
        
        val prompt = buildSongPrompt(songTitle, artist)
        return callApi(apiKey, prompt, AiInfoType.SONG, songTitle).also { result ->
            result.onSuccess { info ->
                aiInfoCacheStore.saveSongInfo(songTitle, artist, info)
            }
        }
    }

    override suspend fun getArtistInfo(artistName: String): Result<AiInfo> {
        val cached = getCachedArtistInfo(artistName)
        if (cached != null) {
            return Result.success(cached)
        }
        
        val apiKey = getApiKey() ?: return Result.failure(Exception("API Key 未配置"))
        
        val prompt = buildArtistPrompt(artistName)
        return callApi(apiKey, prompt, AiInfoType.ARTIST, artistName).also { result ->
            result.onSuccess { info ->
                aiInfoCacheStore.saveArtistInfo(artistName, info)
            }
        }
    }

    override suspend fun getAlbumInfo(albumTitle: String, artist: String): Result<AiInfo> {
        val cached = getCachedAlbumInfo(albumTitle, artist)
        if (cached != null) {
            return Result.success(cached)
        }
        
        val apiKey = getApiKey() ?: return Result.failure(Exception("API Key 未配置"))
        
        val prompt = buildAlbumPrompt(albumTitle, artist)
        return callApi(apiKey, prompt, AiInfoType.ALBUM, albumTitle).also { result ->
            result.onSuccess { info ->
                aiInfoCacheStore.saveAlbumInfo(albumTitle, artist, info)
            }
        }
    }

    private suspend fun callApi(
        apiKey: String,
        prompt: String,
        type: AiInfoType,
        title: String
    ): Result<AiInfo> {
        val request = DeepSeekRequest(
            messages = listOf(
                DeepSeekRequest.Message(
                    role = "system",
                    content = "你是一位专业的音乐评论家和音乐历史专家，拥有丰富的音乐知识。请用中文回答问题，回答要准确、简洁、信息丰富。如果某些信息不确定，请如实说明。"
                ),
                DeepSeekRequest.Message(
                    role = "user",
                    content = prompt
                )
            )
        )

        return deepSeekApiService.chat(apiKey, request).mapCatching { response ->
            val content = response.getContent() ?: throw Exception("API 返回空内容")
            AiInfo(
                type = type,
                title = title,
                content = content
            )
        }
    }

    private fun buildSongPrompt(songTitle: String, artist: String): String {
        return """
请提供歌曲《$songTitle》（演唱者：$artist）的详细信息，按以下格式回答：

【基本信息】
- 歌曲名称：
- 演唱者：
- 作词：
- 作曲：
- 发行时间：
- 所属专辑：

【创作背景】
（简述歌曲的创作背景和灵感来源）

【歌词主题】
（分析歌词表达的主题、情感和意境）

【音乐风格】
（描述歌曲的音乐风格、编曲特点）

【歌曲影响】
（歌曲的受欢迎程度、获奖情况、文化影响等）

如果某些信息无法确定，请标注"暂无确切信息"。
        """.trimIndent()
    }

    private fun buildArtistPrompt(artistName: String): String {
        return """
请提供音乐艺术家/乐队"$artistName"的详细信息，按以下格式回答：

【基本信息】
- 艺名/乐队名：
- 真实姓名：（如适用）
- 出生日期/成立时间：
- 国籍/地区：
- 职业：
- 活跃年代：

【音乐风格】
（描述艺术家的音乐风格、特点和演变）

【代表作品】
（列出3-5首代表歌曲或专辑）

【职业生涯】
（简述艺术家的职业发展历程和重要里程碑）

【荣誉成就】
（主要奖项、认证、成就等）

【艺术影响】
（对音乐界的影响和贡献）

如果某些信息无法确定，请标注"暂无确切信息"。如果是乐队，请提供成员信息。
        """.trimIndent()
    }

    private fun buildAlbumPrompt(albumTitle: String, artist: String): String {
        return """
请提供专辑《$albumTitle》（艺术家：$artist）的详细信息，按以下格式回答：

【基本信息】
- 专辑名称：
- 艺术家：
- 发行时间：
- 唱片公司：
- 曲目数量：
- 专辑时长：
- 音乐风格：

【创作背景】
（专辑的创作背景、录制过程、灵感来源）

【专辑主题】
（专辑的整体主题和概念）

【代表曲目】
（列出专辑中的代表歌曲，简要介绍）

【专辑评价】
（专业评价、商业成绩、乐评人观点）

【专辑影响】
（专辑对艺术家生涯的影响、对音乐界的影响）

如果某些信息无法确定，请标注"暂无确切信息"。
        """.trimIndent()
    }
}
