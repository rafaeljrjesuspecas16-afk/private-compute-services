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

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.VisibleForTesting
import com.google.common.flogger.GoogleLogger
import com.google.protobuf.ByteString
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Utility class for generating a random key and encrypting/decrypting it using Android Keystore.
 * The provided methods are thread-safe.
 */
object KeyGenerationUtil {
  private val logger = GoogleLogger.forEnclosingClass()

  private const val ANDROID_KEY_STORE = "AndroidKeyStore"
  private const val AES_GCM_NO_PADDING = "AES/GCM/NoPadding"
  private const val SEALED_MEMORY_KEY_ALIAS = "sm_key_alias"

  @VisibleForTesting const val KEY_LENGTH = 32

  /**
   * Creates a random key of length [KEY_LENGTH].
   *
   * @return the generated random key.
   */
  fun createRandomKey(): ByteString {
    val secureRandom = SecureRandom()
    val bytes = ByteArray(KEY_LENGTH)
    secureRandom.nextBytes(bytes)
    return ByteString.copyFrom(bytes)
  }

  /**
   * Encrypts [data] using a key with alias [SEALED_MEMORY_KEY_ALIAS] in Android Keystore.
   *
   * If the key does not yet exist, it will be generated within Android KeyStore.
   *
   * @param data the data to encrypt.
   * @return [EncryptionResult.Success] containing the [EncryptedKey] with encrypted data and
   *   metadata, or [EncryptionResult.RetryableError] if encryption fails.
   */
  @Synchronized
  fun encrypt(data: ByteString): EncryptionResult<EncryptedKey> {
    try {
      val secretKey = getOrCreateSecretKey(SEALED_MEMORY_KEY_ALIAS)
      val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
      cipher.init(Cipher.ENCRYPT_MODE, secretKey)

      val params = cipher.parameters.getParameterSpec(GCMParameterSpec::class.java)
      val iv = cipher.iv
      val encrypted = cipher.doFinal(data.toByteArray())

      val result = ByteArray(iv.size + encrypted.size)
      System.arraycopy(iv, 0, result, 0, iv.size)
      System.arraycopy(encrypted, 0, result, iv.size, encrypted.size)

      return EncryptionResult.Success(
        EncryptedKey(
          encryptedData = ByteString.copyFrom(result),
          ivLength = params.iv.size,
          tagLengthBits = params.tLen,
        )
      )
    } catch (e: Exception) {
      logger.atWarning().withCause(e).log("Encryption failed")
      return EncryptionResult.RetryableError(e)
    }
  }

  /**
   * Decrypts [encryptedKey] using a key with alias [SEALED_MEMORY_KEY_ALIAS] in Android Keystore.
   *
   * @param encryptedKey the [EncryptedKey] to decrypt.
   * @return [EncryptionResult.Success] containing the decrypted data as a [ByteString],
   *   [EncryptionResult.IrrecoverableError] if decryption fails due to a missing key or bad
   *   ciphertext, or [EncryptionResult.RetryableError] for other failures.
   */
  @Synchronized
  fun decrypt(encryptedKey: EncryptedKey): EncryptionResult<ByteString> {
    try {
      val secretKey = getSecretKey(SEALED_MEMORY_KEY_ALIAS)
      if (secretKey == null) {
        logger.atWarning().log("Missing secret key")
        return EncryptionResult.IrrecoverableError(IllegalStateException("Missing secret key"))
      }
      val encryptedBytes = encryptedKey.encryptedData.toByteArray()
      val iv = encryptedBytes.copyOfRange(0, encryptedKey.ivLength)
      val ciphertext = encryptedBytes.copyOfRange(encryptedKey.ivLength, encryptedBytes.size)

      val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
      val spec = GCMParameterSpec(encryptedKey.tagLengthBits, iv)
      cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
      return EncryptionResult.Success(ByteString.copyFrom(cipher.doFinal(ciphertext)))
    } catch (e: Exception) {
      logger.atWarning().withCause(e).log("Decryption failed")
      return if (e is BadPaddingException || e is IllegalBlockSizeException) {
        EncryptionResult.IrrecoverableError(e)
      } else {
        EncryptionResult.RetryableError(e)
      }
    }
  }

  private fun getSecretKey(alias: String): SecretKey? {
    val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
    keyStore.load(null)
    return keyStore.getKey(alias, null) as SecretKey?
  }

  private fun getOrCreateSecretKey(alias: String): SecretKey {
    val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
    keyStore.load(null)
    val key = keyStore.getKey(alias, null) as SecretKey?
    if (key != null) {
      return key
    }
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
    val spec =
      KeyGenParameterSpec.Builder(
          alias,
          KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
        .setKeySize(256)
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .build()
    keyGenerator.init(spec)
    return keyGenerator.generateKey()
  }
}
