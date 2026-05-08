# Order Matching Engine

<<<<<<< HEAD
> ⚠️ README đầy đủ sẽ hoàn thiện khi xong.
=======
> ⚠️ README đầy đủ sẽ hoàn thiện khi xong
>>>>>>> d36c225cc271f108a107aac6b20225283ce40887

In-memory limit order book matching engine viết bằng **core Java 17+** (no framework), giao tiếp với UI qua **WebSocket / JSON**.

## Modules

| Module | Mô tả |
|---|---|
| `engine` | Domain, order book, matching, gateway WebSocket |
| `benchmarks` | JMH benchmarks (chạy độc lập, không trộn vào production build) |
| `frontend` | React + TypeScript trading terminal (sẽ thêm ở Phase 5) |

## Run locally

```bash
./gradlew :engine:run
```

## Test

```bash
./gradlew :engine:test
```

## Tài liệu
