package com.pluralsight.paygov.web.rest;

import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutResponse;
import com.ingenico.connect.gateway.sdk.java.domain.payment.CreatePaymentResponse;
import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.PayPalException;
import com.pluralsight.paygov.domain.Payment;
import com.pluralsight.paygov.repository.PaymentRepository;
import com.pluralsight.paygov.service.PaymentService;
import com.pluralsight.paygov.web.rest.errors.BadRequestAlertException;
import com.pluralsight.util.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.xml.sax.SAXException;

import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import urn.ebay.api.PayPalAPI.*;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.*;


/**
 * REST controller for managing {@link com.pluralsight.paygov.domain.Payment}.
 */
@RestController
@RequestMapping("/api")
public class PaymentResource {

    private final Logger log = LoggerFactory.getLogger(PaymentResource.class);

    SetExpressCheckoutResponseType setExpressCheckoutResponse;

    private static final String ENTITY_NAME = "payment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentService paymentService;

    private final PaymentRepository paymentRepository;


    public PaymentResource(PaymentService paymentService, PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }



    /**
     * {@code POST  /payments} : Create a new payment.
     *
     * @param payment the payment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new payment, or with status {@code 400 (Bad Request)} if the payment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/payments")
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) throws URISyntaxException {
        log.debug("REST request to save Payment : {}", payment);
        if (payment.getId() != null) {
            throw new BadRequestAlertException("A new payment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Payment result = paymentService.save(payment);
        paymentService.sendMail(payment.getEmail(), payment.getApprovalStatus(), payment.getName());
        return ResponseEntity
            .created(new URI("/api/payments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PostMapping("/payments/worldline")
    public CreateHostedCheckoutResponse payPaymentThroughWorldLine(@Valid @RequestBody Payment payment) throws URISyntaxException, IOException {
        
        paymentService.save(payment);
        return paymentService.pay(payment);

    }

    /**
     * {@code PUT  /payments/:id} : Updates an existing payment.
     *
     * @param id the id of the payment to save.
     * @param payment the payment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated payment,
     * or with status {@code 400 (Bad Request)} if the payment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the payment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/payments/{id}")
    public ResponseEntity<Payment> updatePayment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Payment payment
    ) throws URISyntaxException {
        log.debug("REST request to update Payment : {}, {}", id, payment);
        if (payment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, payment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Payment result = paymentService.update(payment);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, payment.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /payments/:id} : Partial updates given fields of an existing payment, field will ignore if it is null
     *
     * @param id the id of the payment to save.
     * @param payment the payment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated payment,
     * or with status {@code 400 (Bad Request)} if the payment is not valid,
     * or with status {@code 404 (Not Found)} if the payment is not found,
     * or with status {@code 500 (Internal Server Error)} if the payment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/payments/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Payment> partialUpdatePayment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Payment payment
    ) throws URISyntaxException {
        log.debug("REST request to partial update Payment partially : {}, {}", id, payment);
        if (payment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, payment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Payment> result = paymentService.partialUpdate(payment);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, payment.getId().toString())
        );
    }

    /**
     * {@code GET  /payments} : get all the payments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of payments in body.
     * @throws URISyntaxException
     * @throws IOException
     */

     @GetMapping("/payments")
     public List<Payment> getAllPayments() {
         return paymentService.findAll();
     }

    @GetMapping("/payments/mockbin")
    public String getTransactionId()  {
         return paymentService.getId();
    }


    /**
     * {@code GET  /payments/:id} : get the "id" payment.
     *
    //  * @param id the id of the payment to retrieve.
    //  * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the payment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/payments/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        log.debug("REST request to get Payment : {}", id);
        Optional<Payment> payment = paymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(payment);
    }

    /**
     * {@code DELETE  /payments/:id} : delete the "id" payment.
     *
     * @param id the id of the payment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/payments/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        log.debug("REST request to delete Payment : {}", id);
        paymentService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
    }
    @Value("${spring.application.paypalUrl}")
    String paypalUrl;

    @Value("${spring.application.returnUrl}")
    String returnUrl;

    @Value("${spring.application.cancelUrl}")
    String cancelUrl;

    @PostMapping("/payments/paypal")
    public String setExpressCheckout(@Valid @RequestBody Payment payment)
        throws PayPalException, ClientActionRequiredException, SSLConfigurationException, MissingCredentialException, InvalidResponseDataException, InvalidCredentialException, IOException, HttpErrorException, InterruptedException, SAXException, ParserConfigurationException {
        Long payerId = 5L;
        // String paymentAmount = payAmount.getPaymentAmount().toString();

        String returnURL = this.returnUrl;
        String cancelURL = this.cancelUrl;
         

        PaymentActionCodeType paymentAction = PaymentActionCodeType.SALE;
        CurrencyCodeType currencyCode = CurrencyCodeType.EUR;

        Map<String, String> configurationMap = Configuration.getAcctAndConfig();
        PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configurationMap);

        SetExpressCheckoutRequestType setExpressCheckoutReq = new SetExpressCheckoutRequestType();
        setExpressCheckoutReq.setVersion("63.0");

        SetExpressCheckoutRequestDetailsType details = new SetExpressCheckoutRequestDetailsType();

        PaymentDetailsType paymentDetails = new PaymentDetailsType();
        paymentDetails.setOrderDescription("PayGov integration with paypal");
        paymentDetails.setInvoiceID("INVOICE-" + Math.random());
        BasicAmountType orderTotal = new BasicAmountType();
        orderTotal.setValue(String.valueOf(payment.getPaymentAmount()));
        orderTotal.setCurrencyID(currencyCode);
        paymentDetails.setOrderTotal(orderTotal);
        paymentDetails.setPaymentAction(paymentAction);
        details.setPaymentDetails(Arrays.asList(new PaymentDetailsType[] { paymentDetails }));
        details.setReturnURL(returnURL);
        details.setCancelURL(cancelURL);
        details.setCustom(payerId.toString());

        setExpressCheckoutReq.setSetExpressCheckoutRequestDetails(details);

        SetExpressCheckoutReq expressCheckoutReq = new SetExpressCheckoutReq();

        expressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutReq);

        setExpressCheckoutResponse = service.setExpressCheckout(expressCheckoutReq);

        getExpressCheckoutDetails(setExpressCheckoutResponse.getToken());

        String redirectURL = ( this.paypalUrl + setExpressCheckoutResponse.getToken());

        log.info("Paypal returned url : {}", redirectURL);

        return JSONObject.quote(redirectURL);
    }
   

     public GetExpressCheckoutDetailsResponseDetailsType getExpressCheckoutDetails(String token)
        throws PayPalException, SAXException, ParserConfigurationException, SSLConfigurationException, HttpErrorException, InvalidResponseDataException, ClientActionRequiredException, MissingCredentialException, IOException, InterruptedException, InvalidCredentialException {
        Map<String, String> configurationMap = Configuration.getAcctAndConfig();
        PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configurationMap);

        GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();
        GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequestType = new GetExpressCheckoutDetailsRequestType();
        getExpressCheckoutDetailsRequestType.setVersion("63.0");

        getExpressCheckoutDetailsReq.setGetExpressCheckoutDetailsRequest(new GetExpressCheckoutDetailsRequestType(token));
        GetExpressCheckoutDetailsResponseType getExpressCheckoutDetailsResponseType = service.getExpressCheckoutDetails(
            getExpressCheckoutDetailsReq
        );

        log.info("GetExpressCheckoutDetailsResponseDetailsType");
        log.info(
            "PayerID         : {}",
            getExpressCheckoutDetailsResponseType.getGetExpressCheckoutDetailsResponseDetails().getPayerInfo().getPayerID()
        );
        log.info("Ack             : {}", getExpressCheckoutDetailsResponseType.getAck());
        log.info("Token           : {}", getExpressCheckoutDetailsResponseType.getGetExpressCheckoutDetailsResponseDetails().getToken());
        log.info(
            "Payment Details : {}",
            getExpressCheckoutDetailsResponseType.getGetExpressCheckoutDetailsResponseDetails().getPaymentDetails()
        );
        log.info(
            "Payment Info    : {}",
            getExpressCheckoutDetailsResponseType.getGetExpressCheckoutDetailsResponseDetails().getPaymentInfo()
        );
        log.info(
            "Billing Address : {}",
            getExpressCheckoutDetailsResponseType.getGetExpressCheckoutDetailsResponseDetails().getBillingAddress()
        );
        log.info(
            "Checkout Status : {}",
            getExpressCheckoutDetailsResponseType.getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus()
        );

        return getExpressCheckoutDetailsResponseType.getGetExpressCheckoutDetailsResponseDetails();
    }

}
