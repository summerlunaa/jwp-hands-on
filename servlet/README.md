# Servlet

[스프링 MVC 프레임워크의 동작 방식](https://www.notion.so/MVC-7240c2d993dc4e0684defaf80943a336)

## Servlet이란?

> 스프링 MVC는 서블릿 기반으로 작동한다. (DispatcherSevlet & Filter)
>
- A servlet is a small Java program that runs within a Web server.
- Servlets receice and respond to requests from Web clients. usually across HTTP
- 자바로 HTTP 요청을 처리하는 프로그램을 만들 때 서블릿을 사용한다.
- 서블릿은 자바 표준. [Jakarta EE 스펙](https://javaee.github.io/javaee-spec/javadocs/javax/servlet/Servlet.html).

## 서블릿 살펴보기

아래 세 가지 메서드를 라이프 사이클 메서드라 부른다. (컨테이너는 톰캣/WAS라 생각하면 된다.)

1. Load class

   class 파일을 불러온다.

2. Instantiate servlet (contructor runs)

   불러온 파일을 통해 Servlet 객체를 만든다.

3. inti()

   Servlet이 실행되었을 때 한 번만 실행된다.

4. service() (doGet, doPost…)

   요청을 처리할 때 호출된다. 요청을 받아서 응답을 처리해준다.

5. destroy()

   서버를 종료할 때 호출된다.


## 서블릿과 서블릿 컨테이너

- 서블릿을 한 번만 만들긴 하지만 싱글톤은 아니다.
- 서블릿 객체는 하나지만 하나의 요청이 하나의 스레드에서 실행되기 때문에 여러 요청을 처리할 수 있다.
- WS는 기본적으로 멀티스레드로 동작하므로 Servlet에 상태를 두면 안 된다.

## Filter란?

- 요청과 응답에 추가적인 작업을 하고 싶을 때 필터를 사용한다. (doFilter 메서드 구현)
- 필터도 표준에 맞춰 구현할 수 있도록 인터페이스가 제공된다.
- 인증, 로깅, 이미지 변환, 데이터 압축 등에 주로 사용. 비즈니스 로직 X
- 컨테이너와 servlet 중간에서 작동
