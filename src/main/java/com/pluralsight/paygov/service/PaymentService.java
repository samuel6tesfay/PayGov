package com.pluralsight.paygov.service;


import com.ingenico.connect.gateway.sdk.java.ApiException;
import com.ingenico.connect.gateway.sdk.java.Client;
import com.ingenico.connect.gateway.sdk.java.CommunicatorConfiguration;
import com.ingenico.connect.gateway.sdk.java.Factory;
import com.ingenico.connect.gateway.sdk.java.defaultimpl.AuthorizationType;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.Address;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.AmountOfMoney;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutRequest;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutResponse;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.definitions.HostedCheckoutSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Customer;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Order;
import com.pluralsight.paygov.domain.Payment;
import com.pluralsight.paygov.repository.PaymentRepository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * Service Implementation for managing {@link Payment}.
 */
@Service
@Transactional
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Save a payment.
     *
     * @param payment the entity to save.
     * @return the persisted entity.
     */
    public Payment save(Payment payment) {
        log.debug("Request to save Payment : {}", payment);
        return paymentRepository.save(payment);
    }

    /**
     * Update a payment.
     *
     * @param payment the entity to save.
     * @return the persisted entity.
     */
    public Payment update(Payment payment) {
        log.debug("Request to save Payment : {}", payment);
        return paymentRepository.save(payment);
    }

    /**
     * Partially update a payment.
     *
     * @param payment the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Payment> partialUpdate(Payment payment) {
        log.debug("Request to partially update Payment : {}", payment);

        return paymentRepository
            .findById(payment.getId())
            .map(existingPayment -> {
                if (payment.getCik() != null) {
                    existingPayment.setCik(payment.getCik());
                }
                if (payment.getCcc() != null) {
                    existingPayment.setCcc(payment.getCcc());
                }
                if (payment.getPaymentAmount() != null) {
                    existingPayment.setPaymentAmount(payment.getPaymentAmount());
                }
                if (payment.getName() != null) {
                    existingPayment.setName(payment.getName());
                }
                if (payment.getEmail() != null) {
                    existingPayment.setEmail(payment.getEmail());
                }
                if (payment.getPhone() != null) {
                    existingPayment.setPhone(payment.getPhone());
                }

                return existingPayment;
            })
            .map(paymentRepository::save);
    }

    /**
     * Get all the payments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        log.debug("Request to get all Payments");
        return paymentRepository.findAll();
    }

    /**
     * Get one payment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Payment> findOne(Long id) {
        log.debug("Request to get Payment : {}", id);
        return paymentRepository.findById(id);
    }

    /**
     * Delete the payment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Payment : {}", id);
        paymentRepository.deleteById(id);
    }

    public String getId(){
        final String uri = "https://mockbin.org/bin/2e0bab38-cb2a-43ce-8967-2bde366f9b4f";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    public void sendMail(String recipientGmail, String paymentStatus, String name) {
        String to = "samuel6tesfay@gmail.com";
        String from = "samuel66tesfaay@gmail.com";
        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(
            properties,
            new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("samuel66tesfay@gmail.com", "1234S@m@@l66T@sf@y5678");
                }
            }
        );

        session.setDebug(true);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("PayGov payment status notification");
            message.setText("Hi Mr/Mrs " + "'" + name + "'" + " your previous payment appeal is " + "'" + paymentStatus + "'");

            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public CreateHostedCheckoutResponse pay(Payment payment) throws URISyntaxException, IOException {


    //    URI propertiesUri = getClass().getResource("i18n/messages.properties").toURI();

       CommunicatorConfiguration communicatorConfiguration =  new CommunicatorConfiguration()
            .withApiKeyId("b1434a5d30991fcd")
            .withSecretApiKey("jEdhPGaut7zKmScHi0Qpmgxk0L2G0gj+54kPyaf0MkA=")
            .withApiEndpoint(URI.create("https://eu.sandbox.api-ingenico.com"))
            .withIntegrator("private")
            .withAuthorizationType(AuthorizationType.V1HMAC);
        Client client = (Client) Factory.createClient(communicatorConfiguration);


    //    Client client = (Client) Factory.createClient(propertiesUri, "b1434a5d30991fcd",
    //        "jEdhPGaut7zKmScHi0Qpmgxk0L2G0gj+54kPyaf0MkA=");

        HostedCheckoutSpecificInput hostedCheckoutSpecificInput = new HostedCheckoutSpecificInput();
        hostedCheckoutSpecificInput.setLocale("en_GB");
        hostedCheckoutSpecificInput.setReturnUrl("http://localhost:8080/payment");
        hostedCheckoutSpecificInput.setVariant("100");
        hostedCheckoutSpecificInput.setShowResultPage(false);

        AmountOfMoney amountOfMoney = new AmountOfMoney();
        amountOfMoney.setAmount(payment.getPaymentAmount());
        amountOfMoney.setCurrencyCode("USD");

        Address billingAddress = new Address();
        billingAddress.setCountryCode("US");

        Customer customer = new Customer();
        customer.setBillingAddress(billingAddress);
        customer.setMerchantCustomerId("1332");

        Order order = new Order();
        order.setAmountOfMoney(amountOfMoney);
        order.setCustomer(customer);

        CreateHostedCheckoutRequest body = new CreateHostedCheckoutRequest();
        body.setHostedCheckoutSpecificInput(hostedCheckoutSpecificInput);
        body.setOrder(order);

        CreateHostedCheckoutResponse response = client.merchant("1332").hostedcheckouts().create(body);

        log.info("Worldline partial redirect url : {}", response.getPartialRedirectUrl());
        log.info("Worldline RETURN MAC", response.getRETURNMAC());
        log.info("Worldline hostedCheckoutId", response.getHostedCheckoutId());

        return response;
    }



    }
