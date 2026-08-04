import { createRouter, createWebHistory } from 'vue-router'

import ListagemPage from '@/pages/ListagemPage.vue'
import PessoaFormPage from '@/pages/PessoaFormPage.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'listagem',
            component: ListagemPage
        },
        {
            path: '/pessoas/novo',
            name: 'pessoa-nova',
            component: PessoaFormPage
        },
        {
            path: '/pessoas/:id/editar',
            name: 'pessoa-editar',
            component: PessoaFormPage,
            props: true
        }
    ]
})

export default router
