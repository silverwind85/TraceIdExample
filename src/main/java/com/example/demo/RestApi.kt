package com.example.demo

import brave.propagation.CurrentTraceContext
import com.example.demo.model.JokeResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration

/**
 * Configures the application's REST API.
 */
@Component
@RestController
@Suppress("unused")
class RestApi(
    private val currentTraceContext: CurrentTraceContext,
    private val jokeFlow: JokeFlow,
    @Value("\${endpoint.timeout}") val timeout: Long
) {

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("dsc")
    fun displayedDsc(

    ): Mono<Array<JokeResponse>> {
        returningResponseLog()
        return jokeFlow.get()
            .doOnNext { returningResponseLog() }
            .timeout(Duration.ofMillis(timeout))
    }

    private fun returningResponseLog() {
        val trackingId = currentTraceContext.get().traceId()
        println("TrackingId: $trackingId")
    }
}
