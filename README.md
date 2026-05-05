# Order Matching Engine

> ⚠️ Project đang trong giai đoạn xây dựng. README đầy đủ sẽ hoàn thiện khi xong.

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

- [WORKFLOW.md](WORKFLOW.md) — quy trình spec-driven
- [PROJECT_2_ORDER_MATCHING_ENGINE.md](PROJECT_2_ORDER_MATCHING_ENGINE.md) — project brief
- `DECISIONS.md` — sẽ thêm khi có quyết định kỹ thuật đáng ghi
