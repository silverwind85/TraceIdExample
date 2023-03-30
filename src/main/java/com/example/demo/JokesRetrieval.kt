package com.example.demo

import brave.Tracing
import brave.propagation.CurrentTraceContext
import com.example.demo.model.ErrorHandler
import com.example.demo.model.JokeResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JokesRetrieval(
    @Qualifier("webclientJokes") private val webclientJokes: WebClientConfig,
    private val errorHandler: ErrorHandler,
    private val currentTraceContext: CurrentTraceContext,
) {
    fun retrieveDscImage(
    ): Mono<Array<JokeResponse>> {
        val trackingId = currentTraceContext.get()?.traceId()
        println("TrackingId: $trackingId")
        return webclientJokes.webClient.get()
            .retrieve()
            .bodyToMono(Array<JokeResponse>::class.java).doOnNext { throw Exception() }
            .handleExceptions()

    }

    private fun <T> Mono<T>.handleExceptions(
    ) = onErrorMap { errorHandler.handle(it) }

}


