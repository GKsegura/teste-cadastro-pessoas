package br.com.gksegura.cadastropessoas.services;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.gksegura.cadastropessoas.dtos.PessoaRequestDTO;
import br.com.gksegura.cadastropessoas.dtos.PessoaResponseDTO;
import br.com.gksegura.cadastropessoas.entities.Pessoa;
import br.com.gksegura.cadastropessoas.exceptions.RecursoDuplicadoException;
import br.com.gksegura.cadastropessoas.exceptions.RecursoNaoEncontradoException;
import br.com.gksegura.cadastropessoas.repositories.PessoaRepository;

@Service
public class PessoaService {

    private final PessoaRepository repository;

    public PessoaService(PessoaRepository repository) {
        this.repository = repository;
    }

    public PessoaResponseDTO cadastrar(PessoaRequestDTO dto) {
        if (repository.existsByCpfCnpj(dto.cpfCnpj())) {
            throw new RecursoDuplicadoException("Já existe uma pessoa cadastrada com o CPF/CNPJ " + dto.cpfCnpj());
        }
        Pessoa pessoa = new Pessoa(dto.nome(), dto.cpfCnpj(), dto.telefone(), dto.email());
        Pessoa salva = repository.save(pessoa);
        return PessoaResponseDTO.fromEntity(salva);
    }

    public List<PessoaResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(PessoaResponseDTO::fromEntity)
                .toList();
    }

    public PessoaResponseDTO buscarPorId(Long id) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa com id " + id + " não encontrada"));
        return PessoaResponseDTO.fromEntity(pessoa);
    }

    public PessoaResponseDTO atualizar(Long id, PessoaRequestDTO dto) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa com id " + id + " não encontrada"));

        if (repository.existsByCpfCnpjAndIdNot(dto.cpfCnpj(), id)) {
            throw new RecursoDuplicadoException("Já existe uma pessoa cadastrada com o CPF/CNPJ " + dto.cpfCnpj());
        }

        pessoa.setNome(dto.nome());
        pessoa.setCpfCnpj(dto.cpfCnpj());
        pessoa.setTelefone(dto.telefone());
        pessoa.setEmail(dto.email());
        Pessoa atualizada = repository.save(pessoa);
        return PessoaResponseDTO.fromEntity(atualizada);
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Pessoa com id " + id + " não encontrada");
        }
        repository.deleteById(id);
    }
}
