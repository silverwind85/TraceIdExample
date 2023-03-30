package com.example.demo.model

import org.springframework.http.HttpStatus

object ErrorResponseType {

    val INTERNAL_SERVER_ERROR_RESPONSE = ErrorResponse("InternalServerError", HttpStatus.INTERNAL_SERVER_ERROR.value())

}