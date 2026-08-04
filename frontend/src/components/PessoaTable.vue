<script setup>
import { Modal } from 'bootstrap'
import { onMounted, ref } from 'vue'

defineProps({
    pessoas: Array,
    carregando: Boolean
})

const emit = defineEmits(['excluir', 'editar'])

const modalRef = ref(null)
const pessoaSelecionada = ref(null)
let modal = null

onMounted(() => {
    modal = new Modal(modalRef.value)
})

function abrirConfirmacao(pessoa) {
    pessoaSelecionada.value = pessoa
    modal.show()
}

function confirmarExclusao() {
    emit('excluir', pessoaSelecionada.value.id)
    modal.hide()
}
</script>

<template>

    <div class="table-responsive">
        <table class="table table-striped table-hover align-middle">
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>CPF/CNPJ</th>
                    <th>Telefone</th>
                    <th>E-mail</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                <tr v-if="carregando">
                    <td colspan="5" class="text-center text-muted py-4">Carregando...</td>
                </tr>
                <tr v-else-if="!pessoas.length">
                    <td colspan="5" class="text-center text-muted py-4">Nenhuma pessoa cadastrada ainda.</td>
                </tr>
                <tr v-for="pessoa in pessoas" v-else :key="pessoa.id">
                    <td>{{ pessoa.nome }}</td>
                    <td>{{ pessoa.cpfCnpj }}</td>
                    <td>{{ pessoa.telefone }}</td>
                    <td>{{ pessoa.email }}</td>
                    <td>
                        <button type="button" class="btn btn-sm btn-outline-warning me-2"
                            @click="$emit('editar', pessoa)">
                            Editar
                        </button>
                        <button type="button" class="btn btn-sm btn-outline-danger"
                            @click="abrirConfirmacao(pessoa)">
                            Excluir
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <div ref="modalRef" class="modal fade" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Confirmar exclusão</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
                </div>
                <div class="modal-body">
                    Tem certeza que deseja excluir
                    <strong>{{ pessoaSelecionada?.nome }}</strong>?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-danger" @click="confirmarExclusao">Excluir</button>
                </div>
            </div>
        </div>
    </div>

</template>
