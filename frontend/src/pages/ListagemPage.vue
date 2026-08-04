<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'

import PessoaTable from '@/components/PessoaTable.vue'
import { excluirPessoa, listarPessoas } from '@/services/pessoaService'

const router = useRouter()
const toast = useToast()

const pessoas = ref([])
const carregando = ref(false)

async function carregarPessoas() {
    carregando.value = true
    try {
        const resposta = await listarPessoas()
        pessoas.value = resposta.data
    } catch {
        toast.error('Erro ao carregar pessoas')
    } finally {
        carregando.value = false
    }
}

function editar(pessoa) {
    router.push(`/pessoas/${pessoa.id}/editar`)
}

async function excluir(id) {
    try {
        await excluirPessoa(id)
        toast.success('Pessoa excluída com sucesso!')
        await carregarPessoas()
    } catch {
        toast.error('Erro ao excluir pessoa')
    }
}

onMounted(carregarPessoas)
</script>

<template>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3 mb-0">Cadastro de Pessoas</h1>
        <RouterLink to="/pessoas/novo" class="btn btn-primary">Nova Pessoa</RouterLink>
    </div>

    <PessoaTable :pessoas="pessoas" :carregando="carregando" @excluir="excluir" @editar="editar" />
</template>
