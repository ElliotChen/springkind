package tw.elliot.emp.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringConsumer {

	@KafkaListener(topics = {"pubsub"})
	public void listen(ConsumerRecord<String, String> recored) {
		log.info("Got a message from kafka: [{}]", recored.value());
	}
}
