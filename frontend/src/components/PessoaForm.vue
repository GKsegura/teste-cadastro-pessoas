<script setup>
import { reactive, ref, watch } from 'vue'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import Button from 'primevue/button'

const props = defineProps({
    pessoaEmEdicao: {
        type: Object,
        default: null
    }
})

const emit = defineEmits(['cadastrar', 'atualizar', 'cancelar-edicao'])

const form = reactive({
    nome: '',
    email: '',
    idade: null
})

const tentouSalvar = ref(false)

watch(() => props.pessoaEmEdicao, (pessoa) => {
    Object.assign(form, pessoa
        ? { nome: pessoa.nome, email: pessoa.email, idade: pessoa.idade }
        : { nome: '', email: '', idade: null })
    tentouSalvar.value = false
})

function formularioValido() {
    return form.nome && form.email && form.idade
}

function salvar() {
    tentouSalvar.value = true

    if (!formularioValido())
        return

    if (props.pessoaEmEdicao)
        emit('atualizar', props.pessoaEmEdicao.id, { ...form })
    else
        emit('cadastrar', { ...form })

    Object.assign(form, { nome: '', email: '', idade: null })
    tentouSalvar.value = false
}

function cancelar() {
    emit('cancelar-edicao')
}
</script>

<template>

    <form class="formulario" @submit.prevent="salvar">

        <div class="campo">
            <InputText v-model="form.nome" placeholder="Nome" :invalid="tentouSalvar && !form.nome" />
            <small v-if="tentouSalvar && !form.nome" class="erro-campo">Nome é obrigatório</small>
        </div>

        <div class="campo">
            <InputText v-model="form.email" type="email" placeholder="E-mail" :invalid="tentouSalvar && !form.email" />
            <small v-if="tentouSalvar && !form.email" class="erro-campo">E-mail é obrigatório</small>
        </div>

        <div class="campo">
            <InputNumber v-model="form.idade" placeholder="Idade" :min="0" :invalid="tentouSalvar && !form.idade" />
            <small v-if="tentouSalvar && !form.idade" class="erro-campo">Idade é obrigatória</small>
        </div>

        <Button type="submit" :label="pessoaEmEdicao ? 'Salvar' : 'Cadastrar'" />
        <Button v-if="pessoaEmEdicao" type="button" label="Cancelar" severity="secondary" outlined @click="cancelar" />

    </form>

</template>
