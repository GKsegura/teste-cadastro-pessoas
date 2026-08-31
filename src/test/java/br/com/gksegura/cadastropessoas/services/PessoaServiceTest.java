package br.com.gksegura.cadastropessoas.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.gksegura.cadastropessoas.dtos.PessoaRequestDTO;
import br.com.gksegura.cadastropessoas.dtos.PessoaResponseDTO;
import br.com.gksegura.cadastropessoas.entities.Pessoa;
import br.com.gksegura.cadastropessoas.exceptions.RecursoDuplicadoException;
import br.com.gksegura.cadastropessoas.exceptions.RecursoNaoEncontradoException;
import br.com.gksegura.cadastropessoas.repositories.PessoaRepository;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock
    private PessoaRepository repository;

    private PessoaService service;

    @BeforeEach
    void setUp() {
        service = new PessoaService(repository);
    }

    private Pessoa criarPessoa(Long id, String nome, String cpfCnpj) {
        Pessoa pessoa = new Pessoa(nome, cpfCnpj, "(11) 91234-5678", nome.toLowerCase() + "@exemplo.com");
        ReflectionTestUtils.setField(pessoa, "id", id);
        return pessoa;
    }

    @Test
    void cadastrar_deveSalvarQuandoCpfCnpjNaoEstaCadastrado() {
        PessoaRequestDTO dto = new PessoaRequestDTO("Ana Souza", "529.982.247-25", "(11) 91234-5678",
                "ana@exemplo.com");
        when(repository.existsByCpfCnpj(dto.cpfCnpj())).thenReturn(false);
        when(repository.save(any(Pessoa.class))).thenAnswer(chamada -> {
            Pessoa pessoa = chamada.getArgument(0);
            ReflectionTestUtils.setField(pessoa, "id", 1L);
            return pessoa;
        });

        PessoaResponseDTO resultado = service.cadastrar(dto);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo(dto.nome());
        assertThat(resultado.cpfCnpj()).isEqualTo(dto.cpfCnpj());
        verify(repository).save(any(Pessoa.class));
    }

    @Test
    void cadastrar_deveLancarExcecaoQuandoCpfCnpjJaEstaCadastrado() {
        PessoaRequestDTO dto = new PessoaRequestDTO("Ana Souza", "529.982.247-25", "(11) 91234-5678",
                "ana@exemplo.com");
        when(repository.existsByCpfCnpj(dto.cpfCnpj())).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(dto))
                .isInstanceOf(RecursoDuplicadoException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void existeCpfCnpj_semIdIgnorar_deveConsultarSemExcluirNenhumRegistro() {
        when(repository.existsByCpfCnpj("529.982.247-25")).thenReturn(true);

        boolean resultado = service.existeCpfCnpj("529.982.247-25", null);

        assertThat(resultado).isTrue();
        verify(repository, never()).existsByCpfCnpjAndIdNot(any(), anyLong());
    }

    @Test
    void existeCpfCnpj_comIdIgnorar_deveExcluirOProprioRegistroDaConsulta() {
        when(repository.existsByCpfCnpjAndIdNot("529.982.247-25", 7L)).thenReturn(false);

        boolean resultado = service.existeCpfCnpj("529.982.247-25", 7L);

        assertThat(resultado).isFalse();
        verify(repository, never()).existsByCpfCnpj(any());
    }

    @Test
    void listar_semBusca_deveListarTodasAsPessoasPaginadas() {
        Pageable pageable = PageRequest.of(0, 10);
        Pessoa pessoa = criarPessoa(1L, "Ana Souza", "529.982.247-25");
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(pessoa), pageable, 1));

        Page<PessoaResponseDTO> resultado = service.listar(null, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("Ana Souza");
        verify(repository, never()).findByNomeContainingIgnoreCaseOrCpfCnpjContainingIgnoreCase(any(), any(), any());
    }

    @Test
    void listar_comBusca_deveFiltrarPorNomeOuCpfCnpj() {
        Pageable pageable = PageRequest.of(0, 10);
        Pessoa pessoa = criarPessoa(1L, "Ana Souza", "529.982.247-25");
        when(repository.findByNomeContainingIgnoreCaseOrCpfCnpjContainingIgnoreCase("ana", "ana", pageable))
                .thenReturn(new PageImpl<>(List.of(pessoa), pageable, 1));

        Page<PessoaResponseDTO> resultado = service.listar("ana", pageable);

        assertThat(resultado.getContent()).hasSize(1);
        verify(repository, never()).findAll(any(Pageable.class));
    }

    @Test
    void buscarPorId_deveRetornarPessoaQuandoExiste() {
        Pessoa pessoa = criarPessoa(1L, "Ana Souza", "529.982.247-25");
        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));

        PessoaResponseDTO resultado = service.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Ana Souza");
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoIdNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void atualizar_deveAtualizarQuandoValido() {
        Pessoa pessoaExistente = criarPessoa(1L, "Ana Souza", "529.982.247-25");
        PessoaRequestDTO dto = new PessoaRequestDTO("Ana Paula Souza", "529.982.247-25", "(11) 99999-0000",
                "ana.paula@exemplo.com");
        when(repository.findById(1L)).thenReturn(Optional.of(pessoaExistente));
        when(repository.existsByCpfCnpjAndIdNot(dto.cpfCnpj(), 1L)).thenReturn(false);
        when(repository.save(pessoaExistente)).thenReturn(pessoaExistente);

        PessoaResponseDTO resultado = service.atualizar(1L, dto);

        assertThat(resultado.nome()).isEqualTo("Ana Paula Souza");
        assertThat(resultado.telefone()).isEqualTo("(11) 99999-0000");
        assertThat(resultado.email()).isEqualTo("ana.paula@exemplo.com");
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoIdNaoExiste() {
        PessoaRequestDTO dto = new PessoaRequestDTO("Ana Souza", "529.982.247-25", "(11) 91234-5678",
                "ana@exemplo.com");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(99L, dto))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoCpfCnpjJaPertenceAOutraPessoa() {
        Pessoa pessoaExistente = criarPessoa(1L, "Ana Souza", "529.982.247-25");
        PessoaRequestDTO dto = new PessoaRequestDTO("Ana Souza", "111.444.777-35", "(11) 91234-5678",
                "ana@exemplo.com");
        when(repository.findById(1L)).thenReturn(Optional.of(pessoaExistente));
        when(repository.existsByCpfCnpjAndIdNot(dto.cpfCnpj(), 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(1L, dto))
                .isInstanceOf(RecursoDuplicadoException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void excluir_deveExcluirQuandoIdExiste() {
        when(repository.existsById(1L)).thenReturn(true);

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void excluir_deveLancarExcecaoQuandoIdNaoExiste() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(repository, never()).deleteById(any());
    }
}
