package com.example.demo

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutException
import io.netty.handler.timeout.ReadTimeoutHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.NestedExceptionUtils
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientException
import org.springframework.web.util.DefaultUriBuilderFactory
import reactor.core.publisher.Mono
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import java.io.IOException
import java.util.concurrent.TimeUnit


@Configuration
open class WebClientConfiguration(

    private val webclientBuilder: WebClient.Builder
) {

    @Bean
    open fun webclientJokes(
        @Value("\${joke.url}") url: String,
        @Value("\${common.connect.timeout}") connectTimeout: Int,
        @Value("\${common.read.timeout}") readTimeout: Int
    ): WebClientConfig {
        return WebClientConfig(
            webClient = createSecureWebClient(
                url,
                connectTimeout,
                readTimeout
            ),
            url = url,
            errors = mapOf(
                HTTP_ERROR to "4",
                TIMEOUT_ERROR to "5",
                CONNECTION_ERROR to "7"
            ),
            timeout = readTimeout
        )
    }

    private fun createSecureWebClient(
        url: String,
        connectTimeout: Int,
        readTimeout: Int
    ): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
            .doOnConnected { conn: Connection ->
                conn.addHandlerLast(ReadTimeoutHandler(readTimeout.toLong(), TimeUnit.MILLISECONDS))
            }
        val factory = DefaultUriBuilderFactory(url)
        factory.encodingMode = DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT
        return webclientBuilder.clone()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .uriBuilderFactory(factory)
            .baseUrl(url)
            .filter(logRequestFilterFunction())
            .build()
    }

    private fun logRequestFilterFunction(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor {
            Mono.just(it)
        }
    }

    companion object {
        const val HTTP_ERROR = "http"
        const val TIMEOUT_ERROR = "timeout"
        const val CONNECTION_ERROR = "connection"
    }
}

class WebClientConfig(
    val webClient: WebClient,
    val url: String,
    val errors: Map<String, String>,
    val timeout: Int
) {
    fun provideErrorId(exception: Throwable, config: WebClientConfig) =
        when {
            NestedExceptionUtils.getRootCause(exception) is ReadTimeoutException -> config.errors[WebClientConfiguration.TIMEOUT_ERROR]
            exception is WebClientException -> config.errors[WebClientConfiguration.CONNECTION_ERROR]
            exception is IOException -> config.errors[WebClientConfiguration.CONNECTION_ERROR]
            else -> "7"
        }
}