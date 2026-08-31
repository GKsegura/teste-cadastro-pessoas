import axios from 'axios'

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL
})

export function listarPessoas({ busca, page = 0, size = 10 } = {}) {
    return api.get('/pessoas', { params: { busca: busca || undefined, page, size } })
}

export function buscarPessoaPorId(id) {
    return api.get(`/pessoas/${id}`)
}

export function cadastrarPessoa(pessoa) {
    return api.post('/pessoas', pessoa)
}

export function verificarCpfCnpjExistente(cpfCnpj, idIgnorar) {
    return api.get('/pessoas/existe-cpf-cnpj', { params: { cpfCnpj, idIgnorar } })
}

export function excluirPessoa(id) {
    return api.delete(`/pessoas/${id}`)
}

export function atualizarPessoa(id, pessoa) {
    return api.put(`/pessoas/${id}`, pessoa)
}