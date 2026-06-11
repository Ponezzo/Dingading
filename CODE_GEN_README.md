### 1. Back

spring boot 프로젝트 gradle 로 demo 프로젝트 만들어서

의존성 추가하기

```
dependencies {
    implementation 'org.openapitools:jackson-databind-nullable:0.2.6'
    implementation 'javax.validation:validation-api:2.0.1.Final'
    implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

jakarta.servlet.http.HttpServletResponse 패키지를 찾을 수 없다는 오류가 발생하면

```
implementation 'org.springframework.boot:spring-boot-starter-web'
```
를 추가해주면 Jakarta Servlet API를 제공한다


```
openApiGenerate {
    generatorName = "spring"
    inputSpec = "$rootDir/src/main/resources/openapi.yaml".toString()
    outputDir = "$buildDir/generated".toString()
    apiPackage = "com.ssafy.api"
    modelPackage = "com.ssafy.model"
    configOptions = [
        dateLibrary: "java8",
        interfaceOnly: "true",
        useSpringBoot3: "true"
    ]
}
```

생성된 HelloApi.java 가 build/generated 폴더 안에 있는데, controller 가 인식을 못하면 build.gradle 에 위치를 추가해줌

```
sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/src/main/java")
        }
    }
}
```
=> gradle 이 자동으로 build/generated 를 인식한다

​

build.gradle 

```
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.4.3'
    id 'io.spring.dependency-management' version '1.1.7'
    id "org.openapi.generator" version "7.0.1"
}

group = 'com.ssafy'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

openApiGenerate {
    generatorName = "spring"
    inputSpec = "$rootDir/src/main/resources/openapi.yaml".toString()
    outputDir = "$buildDir/generated".toString()
    apiPackage = "com.ssafy.api"
    modelPackage = "com.ssafy.model"
    configOptions = [
            dateLibrary: "java8",
            interfaceOnly: "true",
            useSpringBoot3: "true"
    ]
}

sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/src/main/java")
        }
    }
}


dependencies {
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0'
    implementation 'org.openapitools:jackson-databind-nullable:0.2.6'
    implementation 'javax.validation:validation-api:2.0.1.Final'
    implementation 'org.springframework.boot:spring-boot-starter-web'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

resources/openapi.yaml 을 작성

```
openapi: 3.0.0
info:
  title: Sample API
  version: 1.0.0
servers:
  - url: http://localhost:8080
    description: Local server
paths:
  /hello:
    get:
      summary: 간단한 인사말 API
      operationId: sayHello
      responses:
        "200":
          description: 성공 응답
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
                    example: "Hello, World!"
```

.\gradlew openApiGenerate 하면 api 를 자동 생성해준다.

​

중간에 수정하면 .\gradlew clean build 해서 삭제하고 다시 해보면 됨

### 2. Front

```
npm i openapi-generator
```

( 안 되면 npm i -g npm@11.1.0 실행하고 다시 실행 )

​

openapi.yaml 저장

```
openapi: 3.0.0
info:
  title: Sample API
  version: 1.0.0
servers:
  - url: http://localhost:3000
    description: Local server
paths:
  /hello:
    get:
      summary: 간단한 인사말 API
      operationId: sayHello
      responses:
        "200":
          description: 성공 응답
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
                    example: "Hello, World!"
```

```
npx @openapitools/openapi-generator-cli generate -i openapi.yaml(yaml 파일명) -g javascript(만드려는 언어명) -o ./api(생성할 경로)
```

실행은 ​

```
cd ./api (생성된 경로)
```
​
npm start 하면 실행된다!

localhost:3000/hello 를 가보면 잘 실행되는 걸 볼 수 있다

​
