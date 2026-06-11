package com.robingebert.boxy.domain

import android.content.Context
import com.github.terrakok.fuzzykot.extractTop
import com.google.mediapipe.tasks.components.containers.Embedding
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.robingebert.boxy.domain.models.Asset
import com.robingebert.boxy.domain.models.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.getValue


sealed class EmbeddedSearchItem(
    open val name: String,
    open val id: Long,
    open val embedding: Embedding
) {
    override fun toString(): String {
        return name
    }

    data class Asset(
        override val name: String,
        override val id: Long,
        override val embedding: Embedding
    ) : EmbeddedSearchItem(name, id, embedding)

    data class Location(
        override val name: String,
        override val id: Long,
        override val embedding: Embedding
    ) : EmbeddedSearchItem(name, id, embedding)
}

class SearchRepository(
    private val context: Context,
    private val assetRepository: AssetRepository,
    private val locationRepository: LocationRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val textEmbedder: TextEmbedder by lazy {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("universal_sentence_encoder.tflite")
            .build()

        val options = TextEmbedder.TextEmbedderOptions.builder()
            .setBaseOptions(baseOptions)
            .build()

        TextEmbedder.createFromOptions(context, options)
    }

    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.GERMAN)
        .setTargetLanguage(TranslateLanguage.ENGLISH)
        .build()

    private val germanEnglishTranslator = Translation.getClient(options)

    private suspend fun downloadTranslationModel() = suspendCancellableCoroutine { continuation ->
        germanEnglishTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    private suspend fun translate(text: String): String =
        suspendCancellableCoroutine { continuation ->
            germanEnglishTranslator.translate(text)
                .addOnSuccessListener { translatedText ->
                    continuation.resume(translatedText)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    private var cachedEmbeddings = MutableStateFlow<List<EmbeddedSearchItem>?>(null)

    init {
        scope.launch {
            downloadTranslationModel()
            combine(
                assetRepository.assets,
                locationRepository.locations
            ) { assets, locations ->
                updateCache(assets, locations)
            }.collect { newCache ->
                cachedEmbeddings.value = newCache
            }
        }
    }

    private suspend fun updateCache(
        assets: List<Asset>,
        locations: List<Location>
    ): List<EmbeddedSearchItem> = withContext(Dispatchers.Default) {

        val currentCache = cachedEmbeddings

        val embeddedAssets = assets.map { asset ->
            val existing = currentCache.value?.filterIsInstance<EmbeddedSearchItem.Asset>()
                ?.find { it.name == asset.name && it.id == asset.id }

            if (existing != null) {
                existing
            } else {
                val result = textEmbedder.embed(translate(asset.name))
                EmbeddedSearchItem.Asset(
                    asset.name,
                    asset.id,
                    result.embeddingResult().embeddings().first()
                )
            }
        }

        val embeddedLocations = locations.map { location ->
            val existing = currentCache.value?.filterIsInstance<EmbeddedSearchItem.Location>()
                ?.find { it.name == location.name && it.id == location.id }

            if (existing != null) {
                existing
            } else {
                val result = textEmbedder.embed(translate(location.name))
                EmbeddedSearchItem.Location(
                    location.name,
                    location.id,
                    result.embeddingResult().embeddings().first()
                )
            }
        }

        return@withContext embeddedAssets + embeddedLocations
    }

    suspend fun searchWithAi(
        query: String,
        minSimilarity: Double = 0.5
    ): List<Pair<Boolean, Long>> =
        withContext(Dispatchers.Default) {
            val localCache = cachedEmbeddings.filterNotNull().first()

            if (query.isBlank() || localCache.isEmpty()) return@withContext emptyList()

            val queryResult = textEmbedder.embed(translate(query))
            val queryEmbedding = queryResult.embeddingResult().embeddings().first()

            localCache.map { item ->
                val similarity = TextEmbedder.cosineSimilarity(queryEmbedding, item.embedding)
                Pair(Pair(item is EmbeddedSearchItem.Location, item.id), similarity)
            }
                .filter { it.second >= minSimilarity }
                .sortedByDescending { it.second }
                .slice(0 until minOf(20, localCache.size))
                .map { it.first }
        }

    suspend fun searchFuzzy(query: String): List<Pair<Boolean, Long>> =
        withContext(Dispatchers.Default) {
            val localCache = cachedEmbeddings.filterNotNull().first()

            if (query.isBlank() || localCache.isEmpty()) return@withContext emptyList()

            localCache.extractTop(query, limit = 20).map { item ->
                Pair(item.referent is EmbeddedSearchItem.Location, item.referent.id)
            }
        }
}