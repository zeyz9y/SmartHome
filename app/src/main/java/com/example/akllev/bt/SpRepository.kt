package com.example.akllev.bt

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.akllev.model.SensorData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class SpRepository(private val ctx: Context) {
    companion object { private const val TAG = "SpRepo" }

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val connectMutex = Mutex()

    private var socket: BluetoothSocket? = null
    private var readerJob: Job? = null

    private val _data = MutableStateFlow(SensorData())
    val data: StateFlow<SensorData> = _data

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected

    private fun hasBtConnect(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true // Android 11 ve öncesi: runtime izni yok
        }

    /** UI’dan verilen name/MAC ile bağlan */
    suspend fun connect(macOrName: String): Boolean = connectMutex.withLock {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "connect() target='$macOrName' sdk=${Build.VERSION.SDK_INT}")

            val ad = adapter ?: run {
                Log.e(TAG, "No Bluetooth adapter on this device")
                throw IllegalStateException("Bu cihazda Bluetooth yok")
            }

            if (!ad.isEnabled) {
                Log.w(TAG, "Bluetooth disabled")
                throw IllegalStateException("Bluetooth kapalı, lütfen açın")
            }

            if (!hasBtConnect()) {
                Log.e(TAG, "Missing BLUETOOTH_CONNECT permission")
                throw SecurityException("BLUETOOTH_CONNECT izni yok")
            }

            // Eski bağlantıyı temizle
            try { readerJob?.cancelAndJoin() } catch (_: Exception) {}
            readerJob = null
            try { socket?.close() } catch (_: Exception) {}
            socket = null
            _connected.value = false

            ad.cancelDiscovery()

            val bonded = ad.bondedDevices.orEmpty()
            Log.d(TAG, "bonded.size=${bonded.size} bondedNames=${bonded.map { it.name }}")

            val dev = bonded.firstOrNull { d ->
                d.address.equals(macOrName, ignoreCase = true) ||
                        d.name.equals(macOrName, ignoreCase = true)
            } ?: run {
                // MAC verilmişse doğrudan remote device al (eşleşmemişse çoğu cihazda connect başarısız olur)
                try {
                    Log.w(TAG, "Device not in bonded list, trying getRemoteDevice(...)")
                    ad.getRemoteDevice(macOrName)
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "Invalid MAC or name: $macOrName", e)
                    throw IllegalArgumentException("Cihaz bulunamadı. Önce eşleştirin ya da doğru MAC adresini girin.")
                }
            }

            Log.d(TAG, "creating RFCOMM socket to ${dev.name} (${dev.address})")
            val s = try {
                dev.createRfcommSocketToServiceRecord(BtUuids.SPP)
            } catch (e: Exception) {
                Log.e(TAG, "createRfcommSocketToServiceRecord failed", e)
                throw e
            }

            try {
                Log.d(TAG, "socket.connect() …")
                s.connect()
                Log.d(TAG, "RFCOMM connected, starting reader loop")
            } catch (e: Exception) {
                try { s.close() } catch (_: Exception) {}
                Log.e(TAG, "socket.connect() failed", e)
                throw Exception("Bağlantı kurulamadı: ${e.message ?: "bilinmeyen hata"}")
            }

            socket = s
            _connected.value = true

            readerJob = scope.launch {
                val br = BufferedReader(InputStreamReader(s.inputStream, StandardCharsets.US_ASCII))
                try {
                    while (isActive) {
                        val line = br.readLine() ?: break
                        Log.v(TAG, "rx: $line")

                        when {
                            line.startsWith("LED:", ignoreCase = true) -> {
                                val ledOn = line.contains("ON", ignoreCase = true)
                                _data.value = _data.value.copy(led = ledOn)
                            }
                            line.isNotBlank() && (line[0].isDigit() || line[0] == '-') -> {
                                val p = line.split(',')
                                if (p.size >= 2) {
                                    val t = p[0].trim().toDoubleOrNull()
                                    val h = p[1].trim().toDoubleOrNull()
                                    if (t != null && h != null) {
                                        _data.value = _data.value.copy(
                                            temperature = t.toFloat(),
                                            humidity    = h.toFloat()
                                        )
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "reader loop error", e)
                } finally {
                    Log.d(TAG, "reader loop end; closing socket")
                    _connected.value = false
                    try { br.close() } catch (_: Exception) {}
                    try { socket?.close() } catch (_: Exception) {}
                    socket = null
                }
            }

            true
        }
    }

    suspend fun setLed(on: Boolean) = withContext(Dispatchers.IO) {
        val cmd = if (on) "LEDON\n" else "LEDOFF\n"
        Log.d(TAG, "tx: $cmd (hasPerm=${hasBtConnect()}, connected=${_connected.value})")
        if (!hasBtConnect()) return@withContext
        socket?.outputStream?.apply {
            write(cmd.toByteArray(StandardCharsets.US_ASCII))
            flush()
        } ?: Log.w(TAG, "outputStream is null (not connected)")
    }

    fun disconnect() {
        Log.d(TAG, "disconnect() called")
        scope.launch {
            try { readerJob?.cancelAndJoin() } catch (_: Exception) {}
            readerJob = null
            try { socket?.close() } catch (_: Exception) {}
            socket = null
            _connected.value = false
        }
    }
}
