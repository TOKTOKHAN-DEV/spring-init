package com.spring.spring_init.common.dto

import org.springframework.data.domain.Page

data class PageResponseDTO<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long
) {
    constructor(page: Page<T>) : this(
        content = page.content,
        page = page.number,
        size = page.size,
        totalElements = page.totalElements
    )
}
