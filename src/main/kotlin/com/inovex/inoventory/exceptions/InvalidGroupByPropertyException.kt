package com.inovex.inoventory.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidGroupByPropertyException(message: String) : RuntimeException(message)