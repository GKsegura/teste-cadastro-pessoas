package br.com.gksegura.cadastropessoas.dtos;

import br.com.gksegura.cadastropessoas.validations.CpfOuCnpj;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PessoaRequestDTO(

        @NotBlank(message = "O nome é obrigatório") String nome,

        @NotBlank(message = "O CPF/CNPJ é obrigatório") @CpfOuCnpj(message = "O CPF/CNPJ informado é inválido") String cpfCnpj,

        @NotBlank(message = "O telefone é obrigatório") @Pattern(regexp = "\\(\\d{2}\\)\\s?\\d{4,5}-?\\d{4}", message = "O telefone informado é inválido") String telefone,

        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "O e-mail informado é inválido") String email) {
}
