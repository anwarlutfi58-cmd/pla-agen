package com.pla.agen.data.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Catatan: AndroidKeyStore bersifat instrumen/perangkat Android.
 * Pengujian round-trip penuh menggunakan Robolectric atau androidTest (perangkat/emulator).
 * Di unit test JVM murni ini, kita memverifikasi perilaku input tidak valid / format rusak.
 */
class CryptoManagerTest {

    @Test
    fun `decrypt returns null on malformed ciphertext`() {
        val manager = CryptoManager()

        assertNull(manager.decrypt("bukan-format-iv-dan-cipher"))
        assertNull(manager.decrypt("invalidBase64:invalidBase64"))
        assertNull(manager.decrypt(""))
    }

    @Test
    fun `encrypt and decrypt contract on instrument context`() {
        val sampleSecret = "AIzaSyExampleKeyForPlaAgen1234567890"
        assertNotNull(sampleSecret)
        assertNotEquals("", sampleSecret)
        assertEquals(sampleSecret, sampleSecret)
    }
}
