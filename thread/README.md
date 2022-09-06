# Thread-safe Class

- 상태 변수를 스레드 간에 공유하지 않는다.
- 상태 변수를 변경할 수 없도록 만든다.
- 상태 변수에 접근할 때 동기화를 사용한다.
- 캡슐화나 데이터 은닉은 스레드 안전한 클래스 작성에 도움이 된다.

### 상태 없는 객체를 항상 스레드에 안전하다.

```java
public class StatelessFactorizer implements Servelet {
	public void service(ServletRequest req, ServletResponse res) {
		BigInteger i = extractFormRequest(req);
		BigInteger[] factors = factor(i);
		encodeIntoResponse(resp, factors);
	}
}
```

여기서 i는 지역변수. 지역변수는 프로세스의 메모리에 저장되는 것이 아니라 개별 스레드의 call stack에 저장되므로 스레드 안전하다.

# 자바의 스레드

### 자바에서 스레드를 만들고 실행하는 법

1. `Thread` 클래스를 상속받는다.
2. `Runnable` 인터페이스를 구현한다.

`start()` 하면 새로 만든 스레드를 실행한다. `join()` 하면 새로 만든 스레드의 작업이 완료될 때까지 메인 스레드가 기다린다.

### thread 설정

- **`accept-count`** : 대기중인 스레드를 저장할 queue의 사이즈
- **`max-connections`** : 클라이언트와 한 번에 연결할 수 있는 최대 갯수
- **`threads.max`** : 생성할 수 있는 스레드의 최대 개수
