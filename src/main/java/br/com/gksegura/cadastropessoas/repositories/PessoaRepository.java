package br.com.gksegura.cadastropessoas.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.gksegura.cadastropessoas.entities.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    boolean existsByCpfCnpj(String cpfCnpj);

    boolean existsByCpfCnpjAndIdNot(String cpfCnpj, Long id);

    Page<Pessoa> findByNomeContainingIgnoreCaseOrCpfCnpjContainingIgnoreCase(String nome, String cpfCnpj,
            Pageable pageable);
}