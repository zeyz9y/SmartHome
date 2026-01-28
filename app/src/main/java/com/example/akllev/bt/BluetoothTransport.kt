@file:Suppress("SpellCheckingInspection")

package com.example.akllev.bt

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * SpRepository'yi arka plan ve UI tarafından ortak kullanmak için ince sarmalayıcı.
 * Burada bağlanma ve komut yazma işlemleri tek yerden yapılır.
 */
class BluetoothTransport private constructor(private val appCtx: Context) {

    // Servis/UI dışında da güvenle kullanabilelim diye repository'yi burada yaratıyoruz
    private val sp by lazy { SpRepository(appCtx) }

    companion object {
        @Volatile private var instance: BluetoothTransport? = null
        fun get(ctx: Context): BluetoothTransport =
            instance ?: synchronized(this) {
                instance ?: BluetoothTransport(ctx.applicationContext).also { instance = it }
            }
    }

    /**
     * Gerekirse kayıtlı cihaza bağlanır.
     * @param nameOrMac null ise SharedPreferences("bt") içinden "device_name" -> "device_mac" okunur.
     */
    suspend fun connectIfNeeded(nameOrMac: String? = null): Boolean = withContext(Dispatchers.IO) {
        val prefs = appCtx.getSharedPreferences("bt", Context.MODE_PRIVATE)
        val target = nameOrMac
            ?: prefs.getString("device_name", null)
            ?: prefs.getString("device_mac", null)

        if (target.isNullOrBlank()) {
            Log.w("BluetoothTransport", "Hedef cihaz yok (device_name/device_mac boş).")
            return@withContext false
        }

        return@withContext try {
            sp.connect(target) // SpRepository.connect(String)
        } catch (e: SecurityException) {
            Log.e("BluetoothTransport", "BLUETOOTH_CONNECT izni yok", e)
            false
        } catch (e: Exception) {
            Log.e("BluetoothTransport", "connect hata", e)
            false
        }
    }

    /** Isıtma AÇ/KAPAT — şu an LED’le temsil ediyoruz. */
    suspend fun heatingSet(on: Boolean) = withContext(Dispatchers.IO) {
        try {
            sp.setLed(on) // SpRepository.setLed(Boolean)
        } catch (e: SecurityException) {
            Log.e("BluetoothTransport", "BLUETOOTH_CONNECT izni yok", e)
        } catch (e: Exception) {
            Log.e("BluetoothTransport", "heatingSet hata", e)
        }
    }

    /** (opsiyonel) Dilersen hedef sıcaklık değeri yazımı burada genişletebilirsin. */
    suspend fun writeTargetTemp(temp: Float) = withContext(Dispatchers.IO) {
        Log.d("BluetoothTransport", "writeTargetTemp -> $temp (gerekirse burada protokol yaz)")
        // Örn: socket outputStream'e özel komut yazımı ekleyebilirsin (SpRepository'ye metod da eklenebilir).
    }

}
