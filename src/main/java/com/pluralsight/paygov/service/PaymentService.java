package com.pluralsight.paygov.service;

import com.pluralsight.paygov.domain.Payment;
import com.pluralsight.paygov.repository.PaymentRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
