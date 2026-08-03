<script setup>
import { onMounted, ref } from 'vue'
import { useToast } from 'vue-toastification'

import AppFooter from '@/components/AppFooter.vue'
import PessoaForm from '@/components/PessoaForm.vue'
import PessoaTable from '@/components/PessoaTable.vue'

import {
    atualizarPessoa,
    cadastrarPessoa,
    excluirPessoa,
    listarPessoas
} from '@/services/pessoaService'

const toast = useToast()

const pessoas = ref([])
const carregando = ref(false)
const pessoaEmEdicao = ref(null)

function tratarErro(erro, mensagemPadrao) {
    if (erro.response?.status === 400) {
        const mensagens = Object.values(erro.response.data).join(' | ')
        toast.error(mensagens)
    } else {
        toast.error(mensagemPadrao)
    }
}

async function cadastrar(dados) {
    try {
        await cadastrarPessoa(dados)
        toast.success('Pessoa cadastrada com sucesso!')
        await carregarPessoas()
    } catch (erro) {
        tratarErro(erro, 'Erro ao cadastrar pessoa')
    }
}

async function atualizar(id, dados) {
    try {
        await atualizarPessoa(id, dados)
        toast.success('Pessoa atualizada com sucesso!')
        pessoaEmEdicao.value = null
        await carregarPessoas()
    } catch (erro) {
        tratarErro(erro, 'Erro ao atualizar pessoa')
    }
}

function editar(pessoa) {
    pessoaEmEdicao.value = pessoa
}

function cancelarEdicao() {
    pessoaEmEdicao.value = null
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

onMounted(carregarPessoas)
</script>

<template>
    <div class="container">

        <h1>Cadastro de Pessoas</h1>

        <PessoaForm
            :pessoa-em-edicao="pessoaEmEdicao"
            @cadastrar="cadastrar"
            @atualizar="atualizar"
            @cancelar-edicao="cancelarEdicao"
        />

        <PessoaTable :pessoas="pessoas" :carregando="carregando" @excluir="excluir" @editar="editar" />

        <AppFooter />

    </div>
</template>
