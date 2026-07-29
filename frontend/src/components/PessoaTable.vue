<script setup>

defineProps({
    pessoas: Array
})

const emit = defineEmits(['excluir'])

</script>

<template>

    <table class="tabela">

        <thead>

            <tr>

                <th>Nome</th>
                <th>E-mail</th>
                <th>Idade</th>
                <th>Ações</th>

            </tr>

        </thead>

        <tbody>

            <tr v-for="pessoa in pessoas" :key="pessoa.id">

                <td>{{ pessoa.nome }}</td>
                <td>{{ pessoa.email }}</td>
                <td>{{ pessoa.idade }}</td>

                <td>

                    <button class="btn-excluir" @click="emit('excluir', pessoa.id)">

                        Excluir

                    </button>

                </td>

            </tr>

        </tbody>

    </table>

    <p v-if="pessoas.length === 0" class="vazio">

        Nenhuma pessoa cadastrada ainda.

    </p>

</template>