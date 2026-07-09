/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.`as`.oss.sealedmemory.service.impl

import androidx.appsearch.app.AppSearchSession
import androidx.appsearch.app.GetByDocumentIdRequest
import androidx.appsearch.app.PutDocumentsRequest
import androidx.appsearch.app.RemoveByDocumentIdRequest
import androidx.appsearch.app.SearchSpec
import androidx.appsearch.app.SetSchemaRequest
import com.google.async.coroutines.GuardedByMutex
import com.google.common.flogger.GoogleLogger
import com.google.protobuf.ByteString
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** AppSearch storage for keys. */
@Singleton
class AppSearchKeyStore
@Inject
constructor(private val searchSessionProvider: AppSearchSessionProvider) {
  private val mutex = Mutex()
  @GuardedByMutex("mutex") private lateinit var searchSession: AppSearchSession
  @GuardedByMutex("mutex") private var initialized = false

  @GuardedByMutex("mutex")
  private suspend fun ensureInitialized() {
    if (!initialized) {
      initialize()
      initialized = true
    }
  }

  @GuardedByMutex("mutex")
  private suspend fun initialize() {
    try {
      val localSearchSession = searchSessionProvider.getAppSearchSession()
      val setSchemaRequest =
        SetSchemaRequest.Builder()
          .addDocumentClasses(EncryptedKeyDocument::class.java, KeyAssociation::class.java)
          .setDocumentClassDisplayedBySystem(
            EncryptedKeyDocument::class.java,
            /* displayed= */ false,
          )
          .setDocumentClassDisplayedBySystem(KeyAssociation::class.java, /* displayed= */ false)
          .build()
      localSearchSession.setSchemaAsync(setSchemaRequest).await()
      searchSession = localSearchSession
    } catch (e: Exception) {
      throw logAndWrapException("Failed to initialize storage.", e)
    }
  }

  /** Stores [key] with the given [keyName]. */
  suspend fun storeKey(keyName: String, key: EncryptedKey): Unit = mutex.withLock {
    ensureInitialized()
    try {
      val encryptedKeyDocument =
        EncryptedKeyDocument(
          namespace = KEY_NAMESPACE,
          id = keyName,
          key = key.encryptedData.toByteArray(),
          ivLength = key.ivLength,
          tagLengthBits = key.tagLengthBits,
        )
      val putRequest = PutDocumentsRequest.Builder().addDocuments(encryptedKeyDocument).build()
      searchSession.putAsync(putRequest).await()
    } catch (e: Exception) {
      throw logAndWrapException("Failed to store key.", e)
    }
  }

  /**
   * Creates an association between [keyAssociation] and [keyName]. Returns true if successful,
   * false if it already exists.
   */
  suspend fun createKeyAssociation(keyAssociation: String, keyName: String): Boolean =
    mutex.withLock {
      ensureInitialized()
      if (retrieveKeyAssociationLocked(keyAssociation) != null) {
        return@withLock false
      }
      try {
        val association =
          KeyAssociation(
            namespace = KEY_ASSOCIATION_NAMESPACE,
            id = keyAssociation,
            keyName = keyName,
          )
        val putRequest = PutDocumentsRequest.Builder().addDocuments(association).build()
        searchSession.putAsync(putRequest).await()
        return@withLock true
      } catch (e: Exception) {
        throw logAndWrapException("Failed to create key association.", e)
      }
    }

  /**
   * Updates the [keyName] for an existing [keyAssociation]. Returns true if successful, false if it
   * doesn't exist.
   */
  suspend fun updateKeyAssociation(keyAssociation: String, keyName: String): Boolean =
    mutex.withLock {
      ensureInitialized()
      if (retrieveKeyAssociationLocked(keyAssociation) == null) {
        return@withLock false
      }
      try {
        val association =
          KeyAssociation(
            namespace = KEY_ASSOCIATION_NAMESPACE,
            id = keyAssociation,
            keyName = keyName,
          )
        val putRequest = PutDocumentsRequest.Builder().addDocuments(association).build()
        searchSession.putAsync(putRequest).await()
        return@withLock true
      } catch (e: Exception) {
        throw logAndWrapException("Failed to update key association.", e)
      }
    }

  /** Retrieves the key for [keyName] if it exists. */
  suspend fun retrieveKey(keyName: String): EncryptedKey? = mutex.withLock {
    ensureInitialized()
    try {
      val getRequest = GetByDocumentIdRequest.Builder(KEY_NAMESPACE).addIds(keyName).build()
      val result = searchSession.getByDocumentIdAsync(getRequest).await()
      val document = result.successes[keyName] ?: return null
      val keyDocument = document.toDocumentClass(EncryptedKeyDocument::class.java)
      return EncryptedKey(
        encryptedData = ByteString.copyFrom(keyDocument.key),
        ivLength = keyDocument.ivLength,
        tagLengthBits = keyDocument.tagLengthBits,
      )
    } catch (e: Exception) {
      throw logAndWrapException("Failed to retrieve key.", e)
    }
  }

  /** Retrieves the key name used by [keyAssociation] if it exists. */
  suspend fun retrieveKeyAssociation(keyAssociation: String): String? = mutex.withLock {
    ensureInitialized()
    retrieveKeyAssociationLocked(keyAssociation)
  }

  @GuardedByMutex("mutex")
  private suspend fun retrieveKeyAssociationLocked(keyAssociation: String): String? {
    try {
      val getRequest =
        GetByDocumentIdRequest.Builder(KEY_ASSOCIATION_NAMESPACE).addIds(keyAssociation).build()
      val result = searchSession.getByDocumentIdAsync(getRequest).await()
      val document = result.successes[keyAssociation] ?: return null
      val association = document.toDocumentClass(KeyAssociation::class.java)
      return association.keyName
    } catch (e: Exception) {
      throw logAndWrapException("Failed to retrieve key association.", e)
    }
  }

  /** Retrieves the key for the key name used by [keyAssociation] if it exists. */
  suspend fun retrieveKeyByAssociation(keyAssociation: String): EncryptedKey? {
    val keyName = retrieveKeyAssociation(keyAssociation) ?: return null
    return retrieveKey(keyName)
  }

  /** Retrieves a map of all keys with their names. */
  suspend fun retrieveAllKeys(): Map<String, EncryptedKey> = mutex.withLock {
    ensureInitialized()
    try {
      val searchSpec =
        SearchSpec.Builder()
          .addFilterNamespaces(KEY_NAMESPACE)
          .addFilterDocumentClasses(EncryptedKeyDocument::class.java)
          .setResultCountPerPage(RESULTS_PER_PAGE)
          .build()
      return buildMap {
        searchSession.search(/* query= */ "", searchSpec).use { searchResults ->
          var results = searchResults.getNextPageAsync().await()
          while (results.isNotEmpty()) {
            for (result in results) {
              val keyDocument =
                result.genericDocument.toDocumentClass(EncryptedKeyDocument::class.java)
              put(
                keyDocument.id,
                EncryptedKey(
                  encryptedData = ByteString.copyFrom(keyDocument.key),
                  ivLength = keyDocument.ivLength,
                  tagLengthBits = keyDocument.tagLengthBits,
                ),
              )
            }
            results = searchResults.getNextPageAsync().await()
          }
        }
      }
    } catch (e: Exception) {
      throw logAndWrapException("Failed to retrieve keys with names.", e)
    }
  }

  /** Retrieves a map of all key associations to key names. */
  suspend fun retrieveAllKeyAssociations(): Map<String, String> = mutex.withLock {
    ensureInitialized()
    try {
      val searchSpec =
        SearchSpec.Builder()
          .addFilterNamespaces(KEY_ASSOCIATION_NAMESPACE)
          .addFilterDocumentClasses(KeyAssociation::class.java)
          .setResultCountPerPage(RESULTS_PER_PAGE)
          .build()
      return buildMap {
        searchSession.search(/* query= */ "", searchSpec).use { searchResults ->
          var results = searchResults.getNextPageAsync().await()
          while (results.isNotEmpty()) {
            for (result in results) {
              val association = result.genericDocument.toDocumentClass(KeyAssociation::class.java)
              put(association.id, association.keyName)
            }
            results = searchResults.getNextPageAsync().await()
          }
        }
      }
    } catch (e: Exception) {
      throw logAndWrapException("Failed to retrieve key associations.", e)
    }
  }

  /** Deletes the key for the given [keyName]. */
  suspend fun deleteKey(keyName: String): Unit = mutex.withLock {
    ensureInitialized()
    try {
      val request = RemoveByDocumentIdRequest.Builder(KEY_NAMESPACE).addIds(keyName).build()
      searchSession.removeAsync(request).await()
    } catch (e: Exception) {
      throw logAndWrapException("Failed to delete key.", e)
    }
  }

  /**
   * Deletes the association for the given [keyAssociation]. Returns true if it was deleted, false
   * if it didn't exist.
   */
  suspend fun deleteKeyAssociation(keyAssociation: String): Boolean = mutex.withLock {
    ensureInitialized()
    if (retrieveKeyAssociationLocked(keyAssociation) == null) {
      return@withLock false
    }
    try {
      val request =
        RemoveByDocumentIdRequest.Builder(KEY_ASSOCIATION_NAMESPACE).addIds(keyAssociation).build()
      searchSession.removeAsync(request).await()
      return@withLock true
    } catch (e: Exception) {
      throw logAndWrapException("Failed to delete key association.", e)
    }
  }

  private fun logAndWrapException(msg: String, e: Exception): Exception {
    logger.atSevere().withCause(e).log("%s", msg)
    return RuntimeException(msg, e)
  }

  companion object {
    private val logger = GoogleLogger.forEnclosingClass()

    private const val KEY_NAMESPACE = "sm_key"
    private const val KEY_ASSOCIATION_NAMESPACE = "sm_key_association"
    private const val RESULTS_PER_PAGE = 100
  }
}
