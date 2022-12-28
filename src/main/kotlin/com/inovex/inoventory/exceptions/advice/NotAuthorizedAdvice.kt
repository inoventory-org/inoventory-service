package com.inovex.inoventory.exceptions.advice

import com.inovex.inoventory.exceptions.NotAuthorizedException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class NotAuthorizedAdvice {
    @ExceptionHandler(NotAuthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun resourceNotFoundHandler(ex: NotAuthorizedException, request: WebRequest): String? {
        return ex.message
    }
}
