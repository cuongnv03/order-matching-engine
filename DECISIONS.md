# Decision log

Mỗi entry ghi lại 1 quyết định kỹ thuật đáng kể: bối cảnh, các phương án đã cân nhắc, và lý do chọn. Mục đích: trả lời được câu hỏi *"sao lại làm thế?"* trong code review hoặc interview, và để chính mình 6 tháng sau hiểu lại.

Format: `D-XXX: [tiêu đề]`. Mới nhất ở trên.

---

## D-003: Versions Catalog (libs.versions.toml) thay vì hard-code version

- **Context:** Project sẽ có nhiều dependency (Jackson, Java-WebSocket, JUnit, AssertJ, JMH, SLF4J, Logback, ArchUnit) chia trên 2 module. Cần cách quản lý version không lặp.
- **Alternatives:**
  - (a) Hard-code `"5.10.2"` mỗi `build.gradle.kts`
  - (b) Style cũ `ext { junitVersion = "..." }` trong root build
  - (c) **Versions Catalog** (`gradle/libs.versions.toml`)
- **Decision:** (c).
- **Rationale:** Cách Gradle 7.4+ khuyến nghị. Type-safe accessor sinh tự động (`libs.junit.bom`) → IDE auto-complete + compile-time bắt typo. Bundle gom nhóm dep liên quan (`libs.bundles.testing` = junit + assertj). Đổi version chỉ 1 chỗ. Catalog cũng được Dependabot/Renovate hiểu native.
- **Trade-off:** Curve học cú pháp TOML + accessor naming convention (`-` trong toml → `.` trong Kotlin). Acceptable.

---

## D-002: Pin gradle wrapper version 8.10.2 thay vì 9.5

- **Context:** Local máy có Gradle 9.5.0 (vừa cài). Wrapper là version mà CI và mọi máy clone repo sẽ dùng — độc lập với local.
- **Alternatives:**
  - (a) Pin 9.5.0 (latest)
  - (b) **Pin 8.10.2** (current stable, plugin-ecosystem-tested)
  - (c) Để nguyên local version
- **Decision:** (b) — 8.10.2.
- **Rationale:** Gradle 9.x mới ra, plugin `me.champeau.jmh` (sẽ dùng cho benchmarks module) test chủ yếu trên 8.x → 9.x có deprecation warnings và edge-case bug. 8.10.2 đủ feature cần: Java 17 toolchain, configuration cache, version catalog, parallel build. Khi 9.x ổn định + plugin update: đổi 1 dòng trong `gradle-wrapper.properties`.
- **Trade-off:** Không tận dụng được tính năng mới của 9.x (task provenance, faster cache). Không quan trọng cho project này.

---

## D-001: Tách `benchmarks` thành Gradle module riêng

- **Context:** Project yêu cầu JMH benchmark suite (đo latency match/add/cancel, GC profile, long vs BigDecimal). JMH cần annotation processor và runtime helper — không liên quan production runtime của matching engine.
- **Alternatives:**
  - (a) 1 module duy nhất với JMH plugin trên toàn bộ source
  - (b) Tách JMH thành sourceSet `jmh` riêng trong cùng module
  - (c) **Tách module Gradle hoàn toàn** (`benchmarks/`)
- **Decision:** (c).
- **Rationale:** Boundary cứng — production jar `engine` hoàn toàn không biết JMH tồn tại, classpath không kéo thêm `jmh-core` (~3MB) không cần. CI có thể chạy/skip benchmark module độc lập (`./gradlew :engine:build` không trigger benchmark). Module split cũng giúp benchmark code chỉ depend vào public API của engine — buộc engine có API rõ ràng. Chi phí nhỏ: phải khai báo `dependencies { jmh(project(":engine")) }`.
- **Trade-off:** 1 module thừa trong settings.gradle. Acceptable.
