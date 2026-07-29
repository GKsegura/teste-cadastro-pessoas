<script setup>
import { ref } from 'vue'

const emit = defineEmits(['cadastrar'])

const nome = ref('')
const email = ref('')
const idade = ref('')

function salvar() {

    emit('cadastrar', {
        nome: nome.value,
        email: email.value,
        idade: idade.value
    })

    nome.value = ''
    email.value = ''
    idade.value = ''

}
</script>

<template>

    <form class="formulario" @submit.prevent="salvar">

        <input v-model="nome" placeholder="Nome">

        <input v-model="email" type="email" placeholder="E-mail">

        <input v-model.number="idade" type="number" placeholder="Idade">

        <button>
            Cadastrar
        </button>

    </form>

</template>