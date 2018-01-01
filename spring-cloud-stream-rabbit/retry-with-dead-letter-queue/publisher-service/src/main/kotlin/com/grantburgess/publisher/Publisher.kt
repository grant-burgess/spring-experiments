package com.grantburgess.publisher

import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Output
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.InboundChannelAdapter
import org.springframework.integration.annotation.Poller
import org.springframework.integration.core.MessageSource
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component
import java.util.Random


@Component
@EnableBinding(OrderSource::class)
class Publisher {
  @Bean
  @InboundChannelAdapter(OrderSource.OUTPUT, poller = [Poller(fixedDelay = "1000")])
  fun send() : MessageSource<OrderDto> {
    return MessageSource<OrderDto> {
        MessageBuilder.withPayload(
                OrderDto(Random().nextInt(10), "Pending")
        )
        .build()
    }
  }
}

data class OrderDto(val id: Int, val status: String)

interface OrderSource {
    @Output(OrderSource.OUTPUT)
    fun sendOrder() : MessageChannel

    companion object {
        const val OUTPUT = "orderChannel"
    }
}