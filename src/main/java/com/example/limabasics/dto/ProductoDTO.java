package com.example.limabasics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductoDTO(

                @NotBlank(message = "El nombre del producto no puede estar vacío") @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres") String nombre,

                @NotBlank(message = "La categoría es obligatoria") String categoria,

                @NotNull(message = "El precio es obligatorio") @Positive(message = "El precio debe ser un número positivo mayor a cero") Integer precio,

                @NotBlank(message = "La URL de la imagen es obligatoria") String imagen) {
}