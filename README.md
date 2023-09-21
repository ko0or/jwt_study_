## REST API
`[GET] http://localhost:8080/api/user`
- 현재 로그인된 유저의 정보
- HTTP HEADER에  JWT 토큰 내용이 필요. ( key: Authorization , value : Bearer 한칸 띄우고 JWT토큰내용 )
![image](https://github.com/ko0or/jwt_study_/assets/105141296/2b8d5f0d-2d9e-49e4-afb7-00a8782b5be7)
<br />


`[GET] http://localhost:8080/api/user/유저명`
- (ADMIN 권한만 가능) 특정 유저의 정보를 검색
- 위와 동일하게 HTTP HEADER에  JWT 토큰 내용이 필요. 
  
`[POST] http://localhost:8080/api/signup`
- 다음 JSON 형식을 HTTP BODY에 담아서 **회원가입**을 진행
```
{
  "username" : "minwoo",
  "password" : "minwoo",
  "nickname" : "minwoo"
}
```
`[POST] http://localhost:8080/api/authenticate`
- 다음 JSON 형식을 HTTP BODY에 담아서 **로그인** 진행

```
{
  "username" : "minwoo",
  "password" : "minwoo"
}
```
<br /><br />

## 참고 자료 (사이트)
<br />

`인프런 - Spring Boot JWT Tutorial` https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-jwt#
<br />

`JWT` https://jwt.io/
<br /><br /><br /><br />

## 개발 환경
버전 : JDK : 1.8,  SpringBoot : 2.7.15 <br />
주요 디펜던시 : H2 Database, JPA, Security, Lombok, DevTools, Validation <br />

`💾 build.gradle`
```
plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.15'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'
}

group = 'me.silvernine'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '1.8'
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    compileOnly 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    runtimeOnly 'com.h2database:h2'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'

    implementation group: 'io.jsonwebtoken', name: 'jjwt-api', version: '0.11.5'
    runtimeOnly group: 'io.jsonwebtoken', name: 'jjwt-impl', version: '0.11.5'
    runtimeOnly group: 'io.jsonwebtoken', name: 'jjwt-jackson', version: '0.11.5'
}

tasks.named('test') {
    useJUnitPlatform()
}

```

<br /><br />

`💾 application.yml`
```
spring:

  h2:
    console:
      enabled: true

  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:

    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
        show_sql: true
    defer-datasource-initialization: true

  sql:
    init:
      mode: always




jwt:
  header: Authorization
  secret: c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK # HS512 알고리즘을 사용 ( 64byte 이상으로 작성  )
  token-validity-in-seconds: 86400 # 60 * 60 * 24 = 86400초 (= 24시간)
  
  
  
logging:
  level:
    me.silvernine.tutorial: debug

```



