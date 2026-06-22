package ru.ncfu.autoshow.dto.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Запрос создания/обновления марки. */
public record BrandRequest(
        @NotBlank(message = "Название марки обязательно")
        @Size(max = 80)
        String name,

        @Size(max = 80)
        String country,

        @Size(max = 512)
        String logoUrl
) {
}
