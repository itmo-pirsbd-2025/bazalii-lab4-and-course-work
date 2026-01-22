# Курсовая работа по предмету

## Как запустить

- Пререквизиты
  - Установить `ollama`
  - Выполнить команду `ollama run llama3.1:8b`, которая скачает нужную модель и запустит ее
- Непосредственный запуск
  - Выполнить команду `docker-compose up -d`, которая запустит БД для саммари, а также инициализирует таблицу
  - Выполнить команду ` ./gradlew bootRun`, которая запустит само приложение. Swagger будет доступен [тут](http://localhost:8080/swagger-ui/index.html#/)

## Нагрузочное тестирование

Также было проведено нагрузочное тестирование утилитой bombardier на генерацию саммари и на получение саммари по id.
Команды для bombardier находятся в файле `infrastructure/bombardier/commands`

```json lines
    bombardier --connections=10 -f PostgresMessages --method=POST --header="Content-Type:application/json" --duration=300s --timeout 60s --latencies localhost:8080/api/summary
    Bombarding http://localhost:8080/api/summary for 5m0s using 10 connection(s)
    [===============================================================================================================================================] 5m0s
    Done!
    Statistics        Avg      Stdev        Max
    Reqs/sec         0.14       6.13     405.94
    Latency         0.97m      5.05s      1.00m
    Latency Distribution
    50%      1.00m
    75%      1.00m
    90%      1.00m
    95%      1.00m
    99%      1.00m
    HTTP codes:
    1xx - 0, 2xx - 11, 3xx - 0, 4xx - 0, 5xx - 0
    others - 42
    Errors:
    timeout - 42
    Throughput:     1.15KB/s
```

```json lines
    bombardier --connections=50 --method=GET --header="Content-Type:application/json" --duration=120s --timeout 1s --latencies localhost:8080/api/summary/0cda2077-c624-4843-bbc8-8b3b4f570bd0
    Bombarding http://localhost:8080/api/summary/0cda2077-c624-4843-bbc8-8b3b4f570bd0 for 2m0s using 50 connection(s)
    [===============================================================================================================================================] 2m0s
    Done!
    Statistics        Avg      Stdev        Max
    Reqs/sec      5594.06    1411.21    8757.80
    Latency        8.94ms     2.06ms   130.25ms
    Latency Distribution
    50%     8.25ms
    75%     9.95ms
    90%    12.20ms
    95%    14.05ms
    99%    19.64ms
    HTTP codes:
    1xx - 0, 2xx - 670991, 3xx - 0, 4xx - 0, 5xx - 0
    others - 0
    Throughput:    10.19MB/s
```

В случае с генерацией саммари можно попробовать выбрать более быструю/маленькую модель в угоду ухудшения генерации саммари. 
Плюс в случае нелокального приложения можно горизонтально масштабировать модельку.
Метод получения данных отлично себя показал, но с ростом таблицы можно рассмотреть партицирование/шардирование и договориться о политике длительности хранения данных.