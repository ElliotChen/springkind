# Spring Boot Display Git Info in Actuator

## 前言

先前在[buildnumber-maven-plugin](https://blog.elliot.tw/?p=8)有說明我想在每個系統裡顯示相關Build的版本資訊的原因，但那畢竟是2013年的事了，10年過去了，怎麼還會用一樣的工具呢？

## SpringBoot 整合

Spring Boot可以提供的Application資訊，參考以下連結

[Spring Boot  - Actuator - Application Information](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.info)

| ID      | Name                                                         | Description                                                  | Prerequisites                                |
| :------ | :----------------------------------------------------------- | :----------------------------------------------------------- | :------------------------------------------- |
| `build` | [`BuildInfoContributor`](https://github.com/spring-projects/spring-boot/tree/v3.1.0/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/BuildInfoContributor.java) | Exposes build information.                                   | A `META-INF/build-info.properties` resource. |
| `env`   | [`EnvironmentInfoContributor`](https://github.com/spring-projects/spring-boot/tree/v3.1.0/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/EnvironmentInfoContributor.java) | Exposes any property from the `Environment` whose name starts with `info.`. | None.                                        |
| `git`   | [`GitInfoContributor`](https://github.com/spring-projects/spring-boot/tree/v3.1.0/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/GitInfoContributor.java) | Exposes git information.                                     | A `git.properties` resource.                 |
| `java`  | [`JavaInfoContributor`](https://github.com/spring-projects/spring-boot/tree/v3.1.0/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/JavaInfoContributor.java) | Exposes Java runtime information.                            | None.                                        |
| `os`    | [`OsInfoContributor`](https://github.com/spring-projects/spring-boot/tree/v3.1.0/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/OsInfoContributor.java) | Exposes Operating System information.                        | None.                                        |

裡面提到了`git`，主要就是讀取`git.properties`

所以只要能產生`git.properties`

在此有兩種方式，一個是由Maven Plugin產生，另一個則由Gradle Plugin產生

[Generate Git Information](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.build.generate-git-info)

## Maven - git-commit-id-maven-plugin

```xml
      <plugin>
        <groupId>io.github.git-commit-id</groupId>
        <artifactId>git-commit-id-maven-plugin</artifactId>
        <version>6.0.0</version>
        <executions>
          <execution>
            <id>get-the-git-infos</id>
            <goals>
              <goal>revision</goal>
            </goals>
            <phase>initialize</phase>
          </execution>
        </executions>
        <configuration>
          <generateGitPropertiesFile>true</generateGitPropertiesFile>         <generateGitPropertiesFilename>${project.build.outputDirectory}/git.properties</generateGitPropertiesFilename>
          <commitIdGenerationMode>full</commitIdGenerationMode>
        </configuration>
      </plugin>
```

## Gradle - gradle-git-properties

```
	id("com.gorylenko.gradle-git-properties") version "2.4.1"
```

## Spring Boot - Application Yaml

再來只要在`managment`裡加入`info.git.enabled=true` 即可

```yaml
management:
  endpoints:
    web:
      exposure:
        include: '*'
  info:
    git:
      enabled: true
    java:
      enabled: true
```

## Actuator Info

簡單透過actuator 提供的endpoint
[http://localhost:8080/actuator/info](http://localhost:8080/actuator/info)

即可看到下列的資訊

```json
{"git":{"branch":"main","commit":{"id":"12a77bb","time":"2023-02-13T04:15:42Z"}},"build":{"artifact":"single","name":"single","time":"2023-06-19T01:26:27.845Z","version":"latest","group":"tw.elliot"},"java":{"version":"17.0.7","vendor":{"name":"Homebrew","version":"Homebrew"},"runtime":{"name":"OpenJDK Runtime Environment","version":"17.0.7+0"},"jvm":{"name":"OpenJDK 64-Bit Server VM","vendor":"Homebrew","version":"17.0.7+0"}}}
```

PS: 拜託，不要再問現在系統上的是哪一版了...