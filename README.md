# SmartHome – Akıllı Ev Android Uygulaması  
**STM32 + HC-05 (Bluetooth Classic / SPP) + Jetpack Compose + MVVM**

Bu repo, staj projesi kapsamında geliştirilen **SmartHome** Android uygulamasını içerir.  
Uygulama, **STM32** üzerinde çalışan sistem ile **HC-05 Bluetooth Classic (SPP)** üzerinden haberleşerek **DHT11 sıcaklık/nem verisini gerçek zamanlı gösterir** ve **LED (akıllı evde klima/cihaz simülasyonu gibi) kontrolü** sağlar.

> Projenin erken aşamasında test amaçlı **PC köprüsü** (Python + Flask + pySerial) denenmiş, daha sonra doğrudan **telefon ↔ HC-05** bağlantısına geçilmiştir.

---

## 🚀 Özellikler

### Gerçek zamanlı veri izleme
- STM32’den her saniye gelen **sıcaklık/nem verileri** UI’da anlık güncellenir.
- Veri formatı (STM32 → Android):  
  `XX.X,YY.Y\r\n`  (ör. `23.4,58.1\r\n`)

### Cihaz kontrolü (LED / Simülasyon)
- Uygulamadaki switch/tuş ile **LED ON/OFF** komutu gönderilir:
  - `LEDON`
  - `LEDOFF`
- “Optimistic update” yaklaşımı: UI hızlı güncellenir, hata olursa geri alınır.

### Manuel / Otomatik mod mantığı (firmware tarafı)
- Manuel komut verildiğinde sistem **MANUAL** moda geçer.
- Otomatik modda sıcaklık **30°C** eşiğini geçince LED otomatik aktif olabilir.
- STM32 tarafında OLED ekranda sıcaklık/nem + LED durumu + mod bilgisi gösterilebilir.

### Bağlantı dayanıklılığı
- Eşleştirilmiş cihazı **adıyla** bulup bağlanma (bondedDevices)
- RFCOMM soketi ile SPP bağlantısı
- Bağlantı hatalarında **yeniden bağlanma** (reconnect) desteği
- “socket closed / read failed” gibi durumlarda temiz disconnect → yeniden bağlanma akışı

### Uygulama ekranları
- **Dashboard:** sıcaklık/nem kartları, LED kontrolü, bağlantı durumu ikonu
- **Devices:** eşleştirilmiş cihaz listesi / cihaz seçimi & ekleme akışı
- **Alerts & Schedules:** uyarılar ve zamanlayıcılar (ısıtma/cihaz zamanlaması)  
  - Haftalık gün seçimi + başlangıç/bitiş saatleri  
  - Bir sonraki tetik zamanını gösterme  
  - Android 13+ bildirim izni yönetimi  
  - Arka planda çalışan kısa süreli servis ile komut gönderme yaklaşımı

---

## 🧱 Mimari

- **UI:** Jetpack Compose + Navigation
- **State yönetimi:** `StateFlow`
- **Mimari:** **MVVM (Repository → ViewModel → UI)**
- **Paylaşım:** CompositionLocal ile ViewModel paylaşımı (projede kullanıldı)
- **Bluetooth:** Classic / SPP (HC-05 BLE değildir)

---

## 📁 Paket/Klasör Yapısı (Genel)

> Projede isimler farklı paketlere dağılmış olabilir; rol dağılımı şu şekilde:

- `bt/` : Bluetooth bağlantı ve IO (socket, read/write, stream)
- `repository/` : cihaz bulma, bağlanma, satır bazlı okuma, komut gönderme, reconnect
- `viewmodel/` : StateFlow ile UI state’leri, LED toggle vb.
- `ui/` : Dashboard / Devices / Alerts / Schedules ekranları
- `service/` : arka plan komut akışı / kısa süreli servis (varsa)
- `alarm/` : zamanlayıcı altyapısı (varsa)
- `model/` : veri modelleri
- `util/` : yardımcı fonksiyonlar

---

## ✅ Gereksinimler

- Android Studio (güncel stable önerilir)
- Bluetooth destekli Android cihaz (emülatör önerilmez)
- **HC-05** (veya Classic SPP uyumlu benzeri) + STM32 sistemi
- STM32 tarafında seri hız (örnek): **9600 baud** (proje ayarına göre)

---

## ⚙️ Kurulum ve Çalıştırma

1. Repo’yu klonla:
   ```bash
   git clone https://github.com/zeyz9y/SmartHome.git
2.Android Studio ile aç ve Gradle Sync bitmesini bekle.

3.Uygulamayı gerçek cihazda çalıştır.

4.Telefonun Bluetooth ayarlarından HC-05 ile eşleştir.

5.Uygulamada Devices ekranından cihazı seç ve bağlan.

🔐 Android 12+ Bluetooth İzinleri (API 31+)
Android 12 (API 31) ve üzeri sürümlerde Bluetooth izinleri runtime istenir.
Projede kullanılan akışa göre şu izinler gerekir:

BLUETOOTH_CONNECT

BLUETOOTH_SCAN (tarama yapılıyorsa)

İzin yönetimi projede “ihtiyaç olduğunda isteme” yaklaşımıyla ele alınmıştır.

🔔 Android 13+ Bildirim İzni (API 33+)
Alerts/Schedules ekranlarında bildirim kullanılıyorsa Android 13+ için:

POST_NOTIFICATIONS runtime izni gerekebilir.

🔗 Eşleştirme (Pairing)
HC-05 için yaygın PIN: 1234 veya 0000 (modül konfigürasyonuna bağlı)

🧪 Test Senaryoları
Eşleştirme: Telefon ↔ HC-05

Bağlantı: Dashboard’tan bağlanma/bağlantı ikonuyla durum takibi

Veri akışı: 1 sn’de bir gelen sıcaklık/nem verisinin UI’da güncellenmesi

LED kontrol: switch ile LEDON/LEDOFF komutlarının çalışması

Yeniden bağlanma: bağlantı kopup geldiğinde sorunsuz devam etmesi

Navigasyon: Devices ekranına geçiş ve cihaz seçimi

🛣️ Roadmap / Geliştirme Notları
Başlangıçta ESP-01 Wi-Fi ile HTTP/Retrofit yaklaşımı planlandı; fiziksel modül temin edilemediği için nihai çözüm Bluetooth olarak bırakıldı.

İleride Wi-Fi modülü entegre edilirse Bluetooth/TCP arasında geçiş kolay olacak şekilde arayüz sadeleştirildi.

## 🖼️ Ekran Görüntüleri

### Dashboard
![Dashboard](docs/dashboard)

### Devices
![Devices](docs/devices)

### Schedules
![Schedules](docs/schedules)
