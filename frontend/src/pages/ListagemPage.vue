<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'

import PessoaTable from '@/components/PessoaTable.vue'
import { excluirPessoa, listarPessoas } from '@/services/pessoaService'

const router = useRouter()
const toast = useToast()

const pessoas = ref([])
const carregando = ref(false)
const busca = ref('')
const paginaAtual = ref(0)
const totalPaginas = ref(0)

let timeoutBusca = null

async function carregarPessoas() {
    carregando.value = true
    try {
        const resposta = await listarPessoas({ busca: busca.value, page: paginaAtual.value })
        pessoas.value = resposta.data.content
        totalPaginas.value = resposta.data.page.totalPages
    } catch {
        toast.error('Erro ao carregar pessoas')
    } finally {
        carregando.value = false
    }
}

function buscarComDebounce() {
    clearTimeout(timeoutBusca)
    timeoutBusca = setTimeout(() => {
        paginaAtual.value = 0
        carregarPessoas()
    }, 400)
}

function irParaPagina(pagina) {
    if (pagina < 0 || pagina >= totalPaginas.value) return
    paginaAtual.value = pagina
    carregarPessoas()
}

function editar(pessoa) {
    router.push(`/pessoas/${pessoa.id}/editar`)
}

async function excluir(id) {
    try {
        await excluirPessoa(id)
        toast.success('Pessoa excluída com sucesso!')
        if (pessoas.value.length === 1 && paginaAtual.value > 0) {
            paginaAtual.value -= 1
        }
        await carregarPessoas()
    } catch {
        toast.error('Erro ao excluir pessoa')
    }
}

onMounted(carregarPessoas)
onUnmounted(() => clearTimeout(timeoutBusca))
</script>

<template>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3 mb-0">Cadastro de Pessoas</h1>
        <RouterLink to="/pessoas/novo" class="btn btn-primary">Nova Pessoa</RouterLink>
    </div>

    <input v-model="busca" type="text" class="form-control mb-3" placeholder="Buscar por nome ou CPF/CNPJ"
        @input="buscarComDebounce" />

    <PessoaTable :pessoas="pessoas" :carregando="carregando" @excluir="excluir" @editar="editar" />

    <nav v-if="totalPaginas > 1" aria-label="Paginação" class="d-flex justify-content-center mt-3">
        <ul class="pagination">
            <li class="page-item" :class="{ disabled: paginaAtual === 0 }">
                <button type="button" class="page-link" @click="irParaPagina(paginaAtual - 1)">Anterior</button>
            </li>
            <li class="page-item disabled">
                <span class="page-link">Página {{ paginaAtual + 1 }} de {{ totalPaginas }}</span>
            </li>
            <li class="page-item" :class="{ disabled: paginaAtual >= totalPaginas - 1 }">
                <button type="button" class="page-link" @click="irParaPagina(paginaAtual + 1)">Próxima</button>
            </li>
        </ul>
    </nav>
</template>
