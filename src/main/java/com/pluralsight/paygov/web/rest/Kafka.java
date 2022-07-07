package com.pluralsight.paygov.web.rest;

import com.pluralsight.paygov.domain.Payment;
import com.pluralsight.paygov.kafka.Producer;
import com.pluralsight.paygov.kafka.Storage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Transactional(timeout = 60)
public class Kafka {

    private final Logger logger = LoggerFactory.getLogger(Kafka.class);


    @Autowired
    Producer producer;

    @Autowired
    private Storage messageStorage;

    Payment payment;

    @PostMapping("/kafka")
    public void sendMessage(@RequestBody Object payment) {
        producer.publishToTopic(payment);
    }

    public void getMessage(@RequestBody Payment payment) {

        logger.info("getMessage", payment);
        this.payment = payment;
    }

    @GetMapping("/kafkadmin")
    public List<Payment> kafkaAdminMessage() {
        logger.info("payments", messageStorage.getAll());
        return messageStorage.getAll();
    }

    @PostMapping("/kafkaPaymentApproval")
    public Payment paymentApproval(@RequestBody Payment payment) throws InterruptedException, ExecutionException, IOException {
        logger.info("paymentApproval", payment);
        messageStorage.add(payment);
        return payment;
    }
}
