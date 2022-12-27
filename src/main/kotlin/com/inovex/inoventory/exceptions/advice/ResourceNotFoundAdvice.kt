package com.inovex.inoventory.exceptions.advice

import com.inovex.inoventory.exceptions.ResourceNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class ResourceNotFoundAdvice {
    @ExceptionHandler(ResourceNotFoundException::class)
    fun resourceNotFoundHandler(ex: ResourceNotFoundException, request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity.notFound().build()
    }
}
