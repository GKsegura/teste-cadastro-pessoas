<script setup>
import { vMaska } from 'maska/vue'
import { reactive, ref, watch } from 'vue'

import { verificarCpfCnpjExistente } from '@/services/pessoaService'

const mascaraCpfCnpj = { mask: ['###.###.###-##', '##.###.###/####-##'] }
const mascaraTelefone = { mask: ['(##) ####-####', '(##) #####-####'] }

const props = defineProps({
    pessoaEmEdicao: {
        type: Object,
        default: null
    }
})

const emit = defineEmits(['cadastrar', 'atualizar', 'cancelar'])

const form = reactive({
    nome: '',
    cpfCnpj: '',
    telefone: '',
    email: ''
})

const tentouSalvar = ref(false)
const cpfCnpjDuplicado = ref(false)
const verificandoCpfCnpj = ref(false)

let timeoutVerificacao = null
let requisicaoAtual = 0

watch(() => props.pessoaEmEdicao, (pessoa) => {
    Object.assign(form, pessoa
        ? { nome: pessoa.nome, cpfCnpj: pessoa.cpfCnpj, telefone: pessoa.telefone, email: pessoa.email }
        : { nome: '', cpfCnpj: '', telefone: '', email: '' })
    tentouSalvar.value = false
    cpfCnpjDuplicado.value = false
}, { immediate: true })

function agendarVerificacaoCpfCnpj() {
    clearTimeout(timeoutVerificacao)
    cpfCnpjDuplicado.value = false

    const digitos = form.cpfCnpj.replace(/\D/g, '')
    if (digitos.length !== 11 && digitos.length !== 14) {
        verificandoCpfCnpj.value = false
        return
    }

    verificandoCpfCnpj.value = true
    timeoutVerificacao = setTimeout(() => verificarCpfCnpj(form.cpfCnpj), 500)
}

async function verificarCpfCnpj(cpfCnpj) {
    const idDaRequisicao = ++requisicaoAtual
    try {
        const resposta = await verificarCpfCnpjExistente(cpfCnpj, props.pessoaEmEdicao?.id)
        if (idDaRequisicao === requisicaoAtual) {
            cpfCnpjDuplicado.value = resposta.data
        }
    } catch {
        // falha na verificação prévia não bloqueia o cadastro; o backend valida no envio
    } finally {
        if (idDaRequisicao === requisicaoAtual) {
            verificandoCpfCnpj.value = false
        }
    }
}

function formularioValido() {
    return form.nome && form.cpfCnpj && form.telefone && form.email && !cpfCnpjDuplicado.value
}

function salvar() {
    tentouSalvar.value = true

    if (!formularioValido())
        return

    if (props.pessoaEmEdicao)
        emit('atualizar', props.pessoaEmEdicao.id, { ...form })
    else
        emit('cadastrar', { ...form })
}

function cancelar() {
    emit('cancelar')
}
</script>

<template>

    <form novalidate @submit.prevent="salvar">

        <div class="row">

            <div class="col-12 mb-3">
                <label for="nome" class="form-label">Nome Completo</label>
                <input id="nome" v-model="form.nome" type="text" class="form-control"
                    :class="{ 'is-invalid': tentouSalvar && !form.nome }" placeholder="Nome completo" />
                <div class="invalid-feedback">Nome é obrigatório</div>
            </div>

            <div class="col-md-6 mb-3">
                <label for="cpfCnpj" class="form-label">CPF/CNPJ</label>
                <input id="cpfCnpj" v-model="form.cpfCnpj" v-maska="mascaraCpfCnpj" type="text" class="form-control"
                    :class="{ 'is-invalid': (tentouSalvar && !form.cpfCnpj) || cpfCnpjDuplicado }"
                    placeholder="000.000.000-00" @input="agendarVerificacaoCpfCnpj" />
                <div class="form-text" v-if="verificandoCpfCnpj">Verificando...</div>
                <div class="invalid-feedback" v-if="cpfCnpjDuplicado">Este CPF/CNPJ já está cadastrado</div>
                <div class="invalid-feedback" v-else>CPF/CNPJ é obrigatório</div>
            </div>

            <div class="col-md-6 mb-3">
                <label for="telefone" class="form-label">Telefone</label>
                <input id="telefone" v-model="form.telefone" v-maska="mascaraTelefone" type="text" class="form-control"
                    :class="{ 'is-invalid': tentouSalvar && !form.telefone }" placeholder="(00) 00000-0000" />
                <div class="invalid-feedback">Telefone é obrigatório</div>
            </div>

            <div class="col-12 mb-3">
                <label for="email" class="form-label">E-mail</label>
                <input id="email" v-model="form.email" type="email" class="form-control"
                    :class="{ 'is-invalid': tentouSalvar && !form.email }" placeholder="nome@exemplo.com" />
                <div class="invalid-feedback">E-mail é obrigatório</div>
            </div>

        </div>

        <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary">
                {{ pessoaEmEdicao ? 'Salvar' : 'Cadastrar' }}
            </button>
            <button type="button" class="btn btn-outline-secondary" @click="cancelar">
                Cancelar
            </button>
        </div>

    </form>

</template>
