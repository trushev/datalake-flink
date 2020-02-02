## Данный проект содержит код для Apache Flink

*Запуск проекта*

```
mvn clean package
mvn exec:java -Dexec.mainClass=ru.comptech2020.EventProcessor
```

*Запуск проекта в докере*
1. Перейти в корень проекта
2. Выполнить команды

```
docker build . -t datalake-flink
docker run --name app -d datalake-flink
```
