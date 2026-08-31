package br.com.gksegura.cadastropessoas.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public boolean existeCpfCnpj(String cpfCnpj, Long idIgnorar) {
        if (idIgnorar != null) {
            return repository.existsByCpfCnpjAndIdNot(cpfCnpj, idIgnorar);
        }
        return repository.existsByCpfCnpj(cpfCnpj);
    }

    public Page<PessoaResponseDTO> listar(String busca, Pageable pageable) {
        Page<Pessoa> pagina = (busca == null || busca.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNomeContainingIgnoreCaseOrCpfCnpjContainingIgnoreCase(busca, busca, pageable);
        return pagina.map(PessoaResponseDTO::fromEntity);
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
