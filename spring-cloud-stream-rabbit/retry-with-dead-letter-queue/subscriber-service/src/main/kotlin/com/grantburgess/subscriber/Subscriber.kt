package com.grantburgess.subscriber

import org.springframework.amqp.AmqpRejectAndDontRequeueException
import org.springframework.amqp.ImmediateAcknowledgeAmqpException
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.messaging.handler.annotation.Headers

@EnableBinding(Sink::class)
class Subscriber : RetryableHeaderListener {
    @StreamListener(Sink.INPUT)
    fun listen(order: OrderDto, @Headers headers: Map<String, Any>) {
        processHeaders(headers)

        // Send message to DLQ (dead letter queue)
        if (order.id >= 5)
            throw AmqpRejectAndDontRequeueException("failed")

        System.out.println("Processed order: $order.")
    }
}

data class OrderDto(val id: Int, val status: String) {
    constructor() : this(0, "")
}

interface RetryableHeaderListener {
    fun processHeaders(headers: Map<*, *>) {
        headers
                .filter { it.key == "x-death" }
                .map {
                    giveUpAfterThreeAttempts(
                            mapOf(it.key to it.value)
                    )
                }
    }

    private fun giveUpAfterThreeAttempts(xDeathHeader: Map<Any?, Any?>) {
        val xDeathElements = xDeathHeader.values.first() as ArrayList<Map<*, *>>
        val retryCount = xDeathElements[0]["count"] as Long

        if (retryCount >= 3L) {
            // give up after 3 rejections
            throw ImmediateAcknowledgeAmqpException("Failed after 4 attempts")
        }
    }
}