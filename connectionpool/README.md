# Connection Pool

유저가 보낸 요청을 처리하는 과정에서 서버(WAS)가 DB에 접근하기 위해서는 `Connection` 이 필요하다.

## Connection은 생성 비용이 크다

### DriverManager

JDBC에서는 `DriverManager`를 사용하면 `Connection` 을 아래와 같이 가져올 수 있다.

```java
Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)
```

하지만 이런식으로 DB에 접근할 때마다 Connection을 생성하는 것은 비효율적이다. `Connection`을 생성하는 데 비용이 많이 들기 때문이다.

```java
// 행을 insert하는 데 드는 시간 (괄호 안의 숫자는 비율을 나타낸다)
// 연결하는 데 비용이 3으로 꽤 큰 것을 알 수 있다.

Connecting: (3)

Sending query to server: (2)

Parsing query: (2)

Inserting row: (1 × size of row)

Inserting indexes: (1 × number of indexes)

Closing: (1)
```

## 그래서 Connection Pool이 필요하다.

따라서 `Connection` 을 생성하는 비용을 줄이기 위해 `Connection` 을 미리 만들어 두고 `Connection Pool` 에 보관하다가 필요할 때 꺼내 쓰는 것이다.

### DataSource

- `DataSource` 를 사용하면 `Connection Pool` 을 활용할 수 있다.
- `DataSource` 란?
    - DB, 파일 같은 물리적 데이터 소스에 연결할 때 사용하는 인터페이스다. 구현체는 각 vendor에서 제공한다.
- `DriverManager`가 아닌`DataSource` 를 사용하는 이유
    - 애플리케이션 코드를 직접 수정하지 않고 properties로 DB 연결을 변경할 수 있다.
    - `Connection Pool` Ehsms 분산 트랙잭션을 활용할 수 있다.

```java
JdbcDataSource dataSource = new JdbcDataSource();
dataSource.setURL(H2_URL);

// Connection을 새로 생성하지 않고, 이미 만들어진 Connection을 get으로 가져온다.
Connection connection = dataSource.getConnection(USER, PASSWORD);
```

## HikariCP

스프링 부트 2.0부터는 HikariCP를 기본 `DataSource`로 채택하고 있다. HikariCP는 빠르고 간편하고 오버헤드가 zero라고 한다(HikariCP 피셜).

### MySQL Configuration

> prepared statement란?
동일하거나 비슷한 데이터베이스 문을 높은 효율성으로 반복적으로 실행하기 위해 사용되는 기능.
일반적으로 쿼리나 업데이트와 같은 SQL 문과 함께 사용된다. 보통 템플릿의 형태를 취하며, 그 템플릿 안으로 특정한 상수값이 매 실행 때마다 대체된다.
>

```java
INSERT INTO products (name, price) VALUES (?, ?);
```

- `cachePrepStmts`
    - prepared statement의 캐싱 여부 (true: 캐싱 함)
    - 디폴트값: false, 추천값: `true` (캐싱 사용)
- `prepStmtCacheSize`
    - 하나의 커넥션에 캐싱할 prepared statement의 개수
    - the number of prepared statements that the MySQL driver will cache per connection.
    - 디폴트값: 25, 추천값: `250-500`
- `prepStmtCacheSqlLimit`
    - 캐싱할 prepared statement의 최대 길이
    - the maximum length of a prepared SQL statement that the driver will cache.
    - 디폴트값: 256, 추천값: `2048`

```java
final var hikariConfig = new HikariConfig();
hikariConfig.setJdbcUrl(H2_URL);
hikariConfig.setUsername(USER);
hikariConfig.setPassword(PASSWORD);
hikariConfig.setMaximumPoolSize(5);
hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

final var dataSource = new HikariDataSource(hikariConfig);
```

## References

- HikariCP repository [https://github.com/brettwooldridge/HikariCP#rocket-initialization](https://github.com/brettwooldridge/HikariCP#rocket-initialization)
- DataSource spring docs [https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#data.sql.datasource](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#data.sql.datasource)
- HikariCP Connection Pool Size [https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing](https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing)
- HikariCP Dead lock에서 벗어나기 (이론편) [https://techblog.woowahan.com/2664/](https://techblog.woowahan.com/2664/)
