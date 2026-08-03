<script setup>
import Button from 'primevue/button'
import Column from 'primevue/column'
import ConfirmDialog from 'primevue/confirmdialog'
import DataTable from 'primevue/datatable'
import { useConfirm } from 'primevue/useconfirm'

defineProps({
    pessoas: Array,
    carregando: Boolean
})

const emit = defineEmits(['excluir', 'editar'])

const confirm = useConfirm()

function confirmarExclusao(pessoa) {
    confirm.require({
        message: `Tem certeza que deseja excluir "${pessoa.nome}"?`,
        header: 'Confirmar exclusão',
        icon: 'pi pi-exclamation-triangle',
        acceptLabel: 'Excluir',
        rejectLabel: 'Cancelar',
        acceptProps: { severity: 'danger' },
        accept: () => emit('excluir', pessoa.id)
    })
}
</script>

<template>

    <DataTable :value="pessoas" :loading="carregando" class="tabela">

        <template #empty>
            <p class="vazio">Nenhuma pessoa cadastrada ainda.</p>
        </template>

        <Column field="nome" header="Nome" />
        <Column field="email" header="E-mail" />
        <Column field="idade" header="Idade" />

        <Column header="Ações">
            <template #body="{ data }">
                <Button label="Excluir" severity="danger" outlined size="small" class="ml-2"
                    @click="confirmarExclusao(data)" />
                <Button label="Editar" severity="warning" outlined size="small" class="ml-2"
                    @click="$emit('editar', data)" />
            </template>
        </Column>

    </DataTable>

    <ConfirmDialog />

</template>
