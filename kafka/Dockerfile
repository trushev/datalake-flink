FROM openjdk:8
RUN apt-get update && \
	apt-get install -y wget && \
	apt-get clean && \
	wget https://archive.apache.org/dist/kafka/0.11.0.3/kafka_2.11-0.11.0.3.tgz && \
	tar -xzf kafka_2.11-0.11.0.3.tgz && \
	rm kafka_2.11-0.11.0.3.tgz
ADD start.sh /
RUN chmod +x start.sh
EXPOSE 2181 9092
ENTRYPOINT ["./start.sh", "kafka_2.11-0.11.0.3"]
