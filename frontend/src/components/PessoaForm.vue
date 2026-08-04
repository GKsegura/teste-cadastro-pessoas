<script setup>
import { reactive, ref, watch } from 'vue'

import { formatarCpfCnpj, formatarTelefone } from '@/utils/mascaras'

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

watch(() => props.pessoaEmEdicao, (pessoa) => {
    Object.assign(form, pessoa
        ? { nome: pessoa.nome, cpfCnpj: pessoa.cpfCnpj, telefone: pessoa.telefone, email: pessoa.email }
        : { nome: '', cpfCnpj: '', telefone: '', email: '' })
    tentouSalvar.value = false
}, { immediate: true })

function aplicarMascaraCpfCnpj(evento) {
    form.cpfCnpj = formatarCpfCnpj(evento.target.value)
}

function aplicarMascaraTelefone(evento) {
    form.telefone = formatarTelefone(evento.target.value)
}

function formularioValido() {
    return form.nome && form.cpfCnpj && form.telefone && form.email
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
                <input id="cpfCnpj" :value="form.cpfCnpj" type="text" class="form-control"
                    :class="{ 'is-invalid': tentouSalvar && !form.cpfCnpj }" placeholder="000.000.000-00"
                    maxlength="18" @input="aplicarMascaraCpfCnpj" />
                <div class="invalid-feedback">CPF/CNPJ é obrigatório</div>
            </div>

            <div class="col-md-6 mb-3">
                <label for="telefone" class="form-label">Telefone</label>
                <input id="telefone" :value="form.telefone" type="text" class="form-control"
                    :class="{ 'is-invalid': tentouSalvar && !form.telefone }" placeholder="(00) 00000-0000"
                    maxlength="15" @input="aplicarMascaraTelefone" />
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
