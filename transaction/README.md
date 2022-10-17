[Spring-Transaction-Note#transaction_manager_이해하기](https://narusas.github.io/2019/07/17/Spring-Transaction-Note.html#transaction_manager_%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0)

[[Spring 3 - Transaction] 트랜잭션 추상화 클래스의 종류와 사용법](https://springsource.tistory.com/127)

[Interface PlatformTransactionManager](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/PlatformTransactionManager.html)

- TransactionManager에서 commit, rollback을 하면 connection을 닫아준다!! DataSourceUtils에서!

# 트랜잭션

> 논리적 작업 단위이다.
>
- 데이터의 정합성을 보장하기 위해 고안된 방법이다.
- 커밋되거나 롤백될 수 있는 가장 작은 작업의 단위이다.
- db의 논리적 작업 단위이다.
- a single unit of logic or work, sometimes made up of multiple operations.
- 트랜잭션의 목적
    - 오류로부터 복구를 허용하고 데이터베이스를 일관성 있게 유지하는 안정적인 작업 단위를 제공한다.
    - 동시에 접근하는 여러 프로그램 간에 격리를 제공한다.

## ACID 속성

- `원자성(Atomicity)`: 트랜잭션과 관련된 작업은 모두 실행(Commit)되거나 모두 실패(Rollback)해야 한다.
- `일관성(Consistency)`: 트랜잭션은 참조 무결성 등의 제약 조건을 위반하지 않고 실행 전후로 데이터의 일관성이 손상되지 않아야 한다.
- `격리성(Isolation)`: 여러 개의 트랜잭션이 서로 간섭 없이 수행되어야 한다.
- `지속성(Durability)`: 트랜잭션 커밋 후에는 시스템이 중단되거나 장애가 발생해도 데이터가 그대로 유지되어야 한다.

## 격리 수준(isolation level)

[MySQL InnoDB Isolation level](https://brunch.co.kr/@jinyoungchoi95/5)

> 트랜잭션이 동시에 변경이나 쿼리를 수행할 때 성능과 안정성, 일관성 및 결과 재현성 간의 균형을 미세 조정하는 설정
>
- 트랜잭션이 동시에 수행될 때 다른 트랜잭션에서 변경하거나 조회하는 데이터를 어디까지 볼 수 있도록 허용할 것인지에 대한 설정이다.
- 트랜잭션은 다양한 비정상 상태, 현상을 방지하기 위해 독립적으로 실행된다. 이 비정상 상태는 동시에 여러 클라이언트가 데이터에 접근하고 수정하기 때문이다. 따라서 dirty read, non-repeatable read, phantom read 등의 현상이 발생할 수 있다.

| 읽기 부정합 \ 격리 레벨 | read uncommitted | read committed | repeatable read | serializable |
| --- | --- | --- | --- | --- |
| dirty read | 발생 | 없음 | 없음 | 없음 |
| non-repeatable read | 발생 | 발생 | 없음 | 없음 |
| phantom read | 발생 | 발생 | 발생 | 없음 |

### 읽기 부정합

- dirty read: 커밋 되지 않은, 처리 중인 데이터를 읽는 현상.
- non-repeatable read: 한 트랙잭션 내에서 같은 쿼리를 두 번 실행 했을 때 다른 값이 나오는 현상.
- phantom read: 다른 트랜잭션에서 수행한 변경 작업에 의해 데이터가 보였다 안 보였다 하는 현상.
    - non-repeatable read가 하나의 row를 읽을 때 발생하는 현상이라면 phontom read는 여러 row를 읽을 때 중간에 데이터가 삽입되거나 삭제되어서 조회 시 데이터가 생겼다 없어졌다 하는 현상이다.

### 격리 레벨

- read uncommitted: 아직 커밋 되지 않은 데이터를 읽을 수 있다.
- read committed: 커밋 하기 전에는 이전 데이터에 대한 snap shot을 남겨 두어 다른 트랜잭션이 변경한 데이터에 의해 영향을 받지 않는다. 따라서 dirty read는 일어나지 않는다. 하지만 커밋된 데이터에 대해서는 정합성을 유지한다고 판단하기 때문에 커밋 된다면 snap shot을 커밋된 데이터로 다시 덮어 쓰게 된다. 따라서 non-repeatable read가 발생한다.
- repeatable read: 한 트랜잭션 안에서 다시 읽어도 같은 데이터를 읽는다. 즉, 트랜잭션이 시작하기 전에 커밋된 데이터만 읽는다.
    - MySQL InnoDB에서는 Next-key Lock을 사용하기 때문에 중간에 특정 데이터가 추가 혹은 삭제되어 발생하는 phantom read가 발생하지 않는다.
- serializable: 한 트랜잭션에서 읽고 쓰는 레코드를 다른 트랜잭션에서 절대 접근할 수 없다. 하지만 성능이 느려진다. (s-lock, x-lock)

## Lock

[Lock으로 이해하는 Transaction의 Isolation Level](https://suhwan.dev/2019/06/09/transaction-isolation-level-and-lock/)

[[데이터베이스] MySQL 스토리지 엔진의 잠금](https://steady-coding.tistory.com/553)

[Select 쿼리는 S락이 아니다. (X락과-S락의-차이](https://velog.io/@soongjamm/Select-%EC%BF%BC%EB%A6%AC%EB%8A%94-S%EB%9D%BD%EC%9D%B4-%EC%95%84%EB%8B%88%EB%8B%A4.-X%EB%9D%BD%EA%B3%BC-S%EB%9D%BD%EC%9D%98-%EC%B0%A8%EC%9D%B4))

## 전파(Propagation)

- 트랜잭션의 경계에서 이미 진행 중인 트랜잭션이 있을 때 또는 없을 때 어떻게 동작할 것인가를 결정하는 방식을 말한다.

### Required

> Support a current transaction, create a new one if none exists.
>

트랜잭션이 존재하면 그대로 이어 받아 사용하고 없으면 새로 만든다.

### Requires_new

> Create a new transaction, and suspend the current transaction if one exists.
>

트랜잭션을 무조건 새로 만들고 기존 트랜잭션이 있으면 보류한다.

### Supports

> Support a current transaction, execute non-transactionally if none exists.
>

트랜잭션이 존재하면 그대로 이어 받아 사용하고 없으면 트랜잭션 없이 동작한다.

### NotSupported

> Execute non-transactionally, suspend the current transaction if one exists.
>

기존 트랜잭션이 있으면 보류하고 트랜잭션 없이 동작한다.

### Mandatory

> Support a current transaction, throw an exception if none exists.
>

트랜잭션이 존재하면 그대로 이어 받아 사용하고 없으면 예외를 발생시킨다.

### Nested

> Execute within a nested transaction if a current transaction exists, behave like REQUIRED otherwise.
>

기존 트랜잭션이 있으면 중첩된 트랜잭션을 실행하고 없으면 새로 만든다.

- 중첩된 트랜잭션이란?
    - 중첩 트랜잭션이 끝나고 커밋은 부모 트랜잭션의 끝에서 이루어 진다.
    - 중첩 트랜잭션 내부에서 롤백이 발생하면 중첩 트랜잭션 내부 로직만 롤백된다.
    - 중첩 트랜잭션이 끝난 이후 부모 트랜잭션에서 롤백이 발생하면 중첩 트랜잭션까지 롤백한다.

### Never

> Execute non-transactionally, throw an exception if a transaction exists.
>

트랜잭션 없이 동작하며 있다면 예외를 발생시킨다.
