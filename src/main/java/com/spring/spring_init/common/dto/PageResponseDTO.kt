package com.spring.spring_init.common.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponseDTO<T>(
    List<T> content,
    int page,
    int size,
    long totalElements
) {

    public PageResponseDTO(Page<T> page) {
        this(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }
}
