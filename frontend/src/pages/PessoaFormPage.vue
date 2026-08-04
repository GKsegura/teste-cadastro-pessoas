<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'

import PessoaForm from '@/components/PessoaForm.vue'
import { atualizarPessoa, buscarPessoaPorId, cadastrarPessoa } from '@/services/pessoaService'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const pessoaEmEdicao = ref(null)
const carregando = ref(false)

function tratarErro(erro, mensagemPadrao) {
    if (erro.response?.status === 400 || erro.response?.status === 409) {
        const dados = erro.response.data
        const mensagens = typeof dados === 'object' ? Object.values(dados).join(' | ') : mensagemPadrao
        toast.error(mensagens)
    } else {
        toast.error(mensagemPadrao)
    }
}

async function cadastrar(dados) {
    try {
        await cadastrarPessoa(dados)
        toast.success('Pessoa cadastrada com sucesso!')
        router.push('/')
    } catch (erro) {
        tratarErro(erro, 'Erro ao cadastrar pessoa')
    }
}

async function atualizar(id, dados) {
    try {
        await atualizarPessoa(id, dados)
        toast.success('Pessoa atualizada com sucesso!')
        router.push('/')
    } catch (erro) {
        tratarErro(erro, 'Erro ao atualizar pessoa')
    }
}

function cancelar() {
    router.push('/')
}

onMounted(async () => {
    if (!route.params.id) return

    carregando.value = true
    try {
        const resposta = await buscarPessoaPorId(route.params.id)
        pessoaEmEdicao.value = resposta.data
    } catch {
        toast.error('Pessoa não encontrada')
        router.push('/')
    } finally {
        carregando.value = false
    }
})
</script>

<template>
    <h1 class="h3 mb-4">{{ route.params.id ? 'Editar Pessoa' : 'Nova Pessoa' }}</h1>

    <p v-if="carregando" class="text-muted">Carregando...</p>
    <PessoaForm v-else :pessoa-em-edicao="pessoaEmEdicao" @cadastrar="cadastrar" @atualizar="atualizar"
        @cancelar="cancelar" />
</template>
