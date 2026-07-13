package br.com.gksegura.cadastropessoas.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PessoaRequestDTO(

        @NotBlank(message = "O nome é obrigatório") String nome,

        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "O e-mail informado é inválido") String email,

        @NotNull(message = "A idade é obrigatória") @Positive(message = "A idade deve ser maior que zero") Integer idade) {
}