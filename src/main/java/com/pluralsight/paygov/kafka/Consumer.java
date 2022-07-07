package com.pluralsight.paygov.kafka;

import com.pluralsight.paygov.domain.Payment;
import com.pluralsight.paygov.web.rest.Kafka;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

    @Autowired
    Kafka kafka;

    private final Logger log = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = "user-create", groupId = "user")
    public void receive(ConsumerRecord<String, LinkedHashMap<String, Object>> obj)
        throws IOException, ExecutionException, InterruptedException {
        log.info("Received / consumed object is : {}", obj);

        Payment pay = new Payment();

        pay.setCik(obj.value().get("cik").toString());
        pay.setCcc(obj.value().get("ccc").toString());
        // pay.setPaymentAmount((Long) obj.value().get("paymentAmount"));
        pay.setName((obj.value().get("name").toString()));
        pay.setEmail((obj.value().get("email").toString()));
        pay.setPhone(obj.value().get("phone").toString());

        // pay.setCik("cik");
        // pay.setCcc("ccc");
        pay.setPaymentAmount(12l);
        // pay.setName("name");
        // pay.setEmail("email");
        // pay.setPhone("phone");
        pay.setApprovalStatus("Approve");

        log.info("Received / consumed payment is : {}", pay);


        kafka.getMessage(pay);
        kafka.kafkaAdminMessage();
        kafka.paymentApproval(pay);
    }
}
