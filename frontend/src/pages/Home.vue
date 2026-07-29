<script setup>
import { onMounted, ref } from 'vue'
import { useToast } from 'vue-toastification'

import AppFooter from '@/components/AppFooter.vue'
import PessoaForm from '@/components/PessoaForm.vue'
import PessoaTable from '@/components/PessoaTable.vue'

import {
    cadastrarPessoa,
    excluirPessoa,
    listarPessoas
} from '@/services/pessoaService'

const toast = useToast()

const pessoas = ref([])

async function cadastrar(dados) {
    try {
        await cadastrarPessoa(dados)

        toast.success('Pessoa cadastrada com sucesso!')
        await carregarPessoas()

    } catch (erro) {

        if (erro.response?.status === 400) {
            const mensagens = Object.values(erro.response.data).join(' | ')
            toast.error(mensagens)
        } else {
            toast.error('Erro ao cadastrar pessoa')
        }

    }
}

async function excluir(id) {

    if (!confirm('Tem certeza que deseja excluir esta pessoa?'))
        return

    try {

        await excluirPessoa(id)

        toast.success('Pessoa excluída com sucesso!')

        await carregarPessoas()

    } catch {

        toast.error('Erro ao excluir pessoa')

    }

}

async function carregarPessoas() {

    try {

        const resposta = await listarPessoas()

        pessoas.value = resposta.data

    } catch {

        toast.error('Erro ao carregar pessoas')

    }

}

onMounted(carregarPessoas)
</script>

<template>
    <div class="container">

        <h1>Cadastro de Pessoas</h1>

        <PessoaForm @cadastrar="cadastrar" />

        <PessoaTable :pessoas="pessoas" @excluir="excluir" />

        <AppFooter />

    </div>
</template>