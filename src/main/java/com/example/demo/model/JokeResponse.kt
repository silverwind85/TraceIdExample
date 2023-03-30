package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
data class JokeResponse(
    @field:Schema(required = true)
    val id: Int,

    @field:Schema(required = true)
    val setup: String,

    @field:Schema(required = false)
    val punchline: String,

    @field:Schema(required = false)
    val type: String
)



