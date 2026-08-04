package br.com.gksegura.cadastropessoas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.gksegura.cadastropessoas.entities.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    boolean existsByCpfCnpj(String cpfCnpj);

    boolean existsByCpfCnpjAndIdNot(String cpfCnpj, Long id);
}