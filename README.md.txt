# Akıllı Ev - Android Uygulaması (Bluetooth + MVVM)

Bu repo, staj projesi kapsamında geliştirilen **Akıllı Ev** Android uygulamasını içerir.
Uygulama, mikrodenetleyici tarafıyla **Classic Bluetooth (SPP)** (örn. HC-05) üzerinden haberleşir.

## Özellikler
- Bluetooth cihazına bağlanma / bağlantı yönetimi
- Sensör verisi görüntüleme (firmware'e bağlı)
- Kontrol komutları gönderme (LED/role/servo vb., firmware'e bağlı)
- Alarm / zamanlayıcı ve bildirim akışları (projede varsa)
- MVVM + Repository mimarisi

## Kurulum ve Çalıştırma
1. Repo’yu klonla:
   ```bash
   git clone <REPO_URL>
2.Android Studio ile aç.
3.Gradle Sync bittikten sonra gerçek cihaza çalıştır (Bluetooth için emülatör önerilmez).

Android 12+ Bluetooth İzinleri

Android 12 (API 31) ve üzeri sürümlerde runtime izinleri gerekebilir:

BLUETOOTH_CONNECT

BLUETOOTH_SCAN (tarama yapılıyorsa)

Eşleştirme (Pairing)

Telefonun Bluetooth ayarlarından modülü eşleştir, sonra uygulamadan bağlan.
HC-05 için yaygın PIN: 1234 veya 0000.

Klasör Yapısı

bt/ : Bluetooth bağlantı ve IO

model/ : Veri modelleri

repository/ : Veri katmanı

viewmodel/ : ViewModel katmanı

ui/ : Arayüz ekranları

service/ : Servis/arkaplan işleri (varsa)

alarm/ : Alarm & bildirim (varsa)

util/ : yardımcılar