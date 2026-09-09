<h1>SP-Framework</h1>

<p>해당 프로젝트는 프록시, 모드 사용시 연동을 쉽게 하게 만든 프레임워크 입니다</p>
<p>해당 프로젝트는 미완성된 프로젝트 입니다 일부 오류가 있을 수 있습니다</p>
<p>해당 프로젝트는 DB는 MariaDB를 기준으로 제작 했습니다</p>

<h2>플러그인 버전</h2>

- 플러그인 : spigot 1.12+
  - 1.12.x~1.16.x -all 버전 사용
  - 1.17+ -base
- 벨로시티 : 3.4.0-SNAPSHOT
  - all 버전 사용

<h2>라이센스</h2>

Copyright (c) 2026 Teujaem

1. 상업적 이용이 가능합니다.
2. 2차 수정이 불가능 합니다. (fork 포함)
3. 2차 배포가 불가능 합니다.

<h2>디스코드</h2>
https://discord.gg/5wrAVaghbq

<h2>config</h2>

<p>velocity/plugins/spframework/config.yml</p>

```
language: ko_kr

websocket:
  host: "0.0.0.0"
  port: 8080

debug: false

database:
  host: "localhost"
  port: 3306
  name: "velocity_db"
  user: "root"
  password: ""
```

<p>bukkit/plugins/SP-Framework/config.yml</p>

```
language: ko_kr

proxy: false

websocket:
  host: "0.0.0.0"
  port: 8080

debug: false

database:
  host: "localhost"
  port: 3306
  name: "velocity_db"
  user: "root"
  password: ""
```