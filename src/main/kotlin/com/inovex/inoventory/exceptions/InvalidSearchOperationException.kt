package com.inovex.inoventory.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
class InvalidSearchOperationException(message: String) : RuntimeException(message)