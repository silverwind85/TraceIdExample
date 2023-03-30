package com.example.demo

import com.example.demo.model.JokeResponse
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JokeFlow(
    private val dscRetrieval: JokesRetrieval
) {
    fun get(): Mono<Array<JokeResponse>> {
        return dscRetrieval.retrieveDscImage()

    }
}
