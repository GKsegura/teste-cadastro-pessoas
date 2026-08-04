package br.com.gksegura.cadastropessoas.dtos;

import br.com.gksegura.cadastropessoas.entities.Pessoa;

public record PessoaResponseDTO(Long id, String nome, String cpfCnpj, String telefone, String email) {

    public static PessoaResponseDTO fromEntity(Pessoa pessoa) {
        return new PessoaResponseDTO(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getCpfCnpj(),
                pessoa.getTelefone(),
                pessoa.getEmail());
    }
}
