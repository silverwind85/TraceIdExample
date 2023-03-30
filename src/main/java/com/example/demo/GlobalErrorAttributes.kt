package com.example.demo

import brave.propagation.CurrentTraceContext
import com.example.demo.model.ErrorResponse
import com.example.demo.model.ErrorResponseType
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

@Component
class GlobalErrorAttributes(
    private val currentTraceContext: CurrentTraceContext
) : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions?) =
        when (val exception = getError(request)) {
            else -> handleUnexpectedException(getError(request))
        }


    private fun handleUnexpectedException(exception: Throwable): Map<String, Any> {
        val trackingId = currentTraceContext.get()?.traceId()
        println("TrackingId: $trackingId")
        return createResponseAttributes(ErrorResponseType.INTERNAL_SERVER_ERROR_RESPONSE)
    }

    private fun createResponseAttributes(errorResponse: ErrorResponse) =
        mapOf(ERROR_STATUS to errorResponse.statusCode, ERROR_BODY to errorResponse)

    companion object {
        const val ERROR_STATUS = "status"
        const val ERROR_BODY = "body"

    }
}