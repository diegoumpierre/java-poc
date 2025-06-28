Start Docker Kafka: docker-compose up -d

Start your Spring Boot app

Test it:

bash

curl "http://localhost:8080/kafka/send?message=HelloKafka"