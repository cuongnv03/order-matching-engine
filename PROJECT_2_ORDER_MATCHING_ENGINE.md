# PROJECT 2: Order Matching Engine

## MỤC TIÊU
Build một matching engine cho limit order book — core của mọi sàn giao dịch. Project này chứng minh tôi hiểu data structures và performance ở level sâu, không chỉ biết dùng framework. Viết bằng core Java, KHÔNG framework.

## THỜI GIAN
~10 ngày, 8 giờ/ngày.

## TARGET AUDIENCE KHI TRÌNH BÀY
Interviewer fintech/trading firm. Dài hạn: quant dev interviews. Họ sẽ hỏi: "Explain matching algorithm" và "Show me the benchmark numbers."

---

## YÊU CẦU CHỨC NĂNG

### Order Book
- Mỗi symbol (VN30, AAPL...) có 1 order book riêng
- Buy side: sorted giá giảm dần (highest bid first)
- Sell side: sorted giá tăng dần (lowest ask first)
- Trong cùng price level: FIFO (order đến trước match trước)
- Hỗ trợ: add order, cancel order, get order book snapshot

### Matching Algorithm (Price-Time Priority)
- Khi add buy order: nếu price >= best ask → match
- Partial fill: buy 100 @ 50 nhưng best ask chỉ có 30 → fill 30, 70 còn lại nằm trong book
- Mỗi match tạo Trade object (buyer, seller, price, quantity, timestamp)
- Market order: match ngay ở best available price, cancel phần unfilled

### Order Types
- Limit order: có specific price
- Market order: no price, execute immediately at best available
- Cancel order: O(1) lookup by order ID

### Risk Checks
- Reject order nếu quantity > max allowed
- Reject nếu price ngoài daily limit band (configurable)

### Real-time UI (React + TypeScript)
- Order book depth display: bid/ask table với price levels và quantities
- Depth chart visualization
- Trade ticker: scrolling list các trades gần nhất
- Order entry form: buy/sell toggle, price, quantity, order type
- Price chart (line hoặc candlestick) — dùng Recharts hoặc Lightweight Charts
- Order history: table orders đã submit với status
- WebSocket connection status indicator

### WebSocket Communication
- Client → Server: SubmitOrder, CancelOrder
- Server → Client: OrderBookSnapshot, TradeEvent, OrderUpdate
- JSON message protocol

---

## YÊU CẦU KỸ THUẬT

### Tech Stack
- **Engine:** Core Java 17+ — KHÔNG Spring, KHÔNG framework (deliberate choice vì matching engine cần low latency, framework overhead không chấp nhận được)
- **Communication:** WebSocket (Java-WebSocket library cho backend)
- **Build:** Gradle
- **Benchmarking:** JMH (Java Microbenchmark Harness)
- **Testing:** JUnit 5
- **Frontend:** React + TypeScript
- **Deploy:** Backend → Railway, Frontend → Vercel

### Data Structure Requirements
- Buy side: `TreeMap<Long, Deque<Order>>` — price descending
- Sell side: `TreeMap<Long, Deque<Order>>` — price ascending
- Lookup: `HashMap<String, Order>` — O(1) cancel
- Price stored as `long` (price × 10000) — avoid floating point trong finance

### Performance Requirements
- Benchmark với JMH: đo add/match/cancel latency
- Test với 1M+ orders
- Optimize: long thay BigDecimal, primitive collections nếu cần, object pooling giảm GC pressure
- Ghi benchmark results: orders/second, P50/P95/P99 latency, memory footprint
- Before/after optimization comparison bắt buộc

### Concurrency
- Per-symbol order book: mỗi symbol có lock riêng, không global lock
- Concurrent correctness test: multiple threads submit orders, verify matching correctness
- Throughput benchmark: orders matched/second under contention

### Testing
- Unit test mọi scenario: exact match, partial fill, multiple fills, no match, empty book, market order on empty side
- Concurrent correctness tests
- Benchmark suite

### Documentation
- README: matching algorithm explanation, architecture diagram, benchmark table, deployed URL
- DECISIONS.md: tại sao TreeMap, tại sao long not BigDecimal (có benchmark proof), tại sao WebSocket not REST, locking strategy

---

## KIẾN THỨC TÔI CẦN HỌC TỪ PROJECT NÀY
- Limit order book hoạt động thế nào
- Price-time priority matching
- Tại sao floating point nguy hiểm trong finance
- TreeMap internals (Red-Black tree)
- JMH benchmarking methodology
- Lock-free vs fine-grained locking
- WebSocket protocol vs REST — khi nào dùng cái nào
- Object pooling và GC pressure
