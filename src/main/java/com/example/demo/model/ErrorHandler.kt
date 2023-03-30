package com.example.demo.model

import brave.propagation.CurrentTraceContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class ErrorHandler(
    private val currentTraceContext: CurrentTraceContext
) {
    fun handle(
        exception: Throwable,
    ): Throwable {
        when (exception) {
            is WebClientResponseException -> {
                println(currentTraceContext.get())
            }

        }
        return RuntimeException()
    }
}

