package com.example.demo.model

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Error response model class
 */
data class ErrorResponse(
    @Schema(required = true) val name: String,
    @Schema(required = true) val statusCode: Int
) {

    override fun toString(): String {
        return "{\"name\":\"$name\",\"statusCode\":$statusCode}"
    }
}