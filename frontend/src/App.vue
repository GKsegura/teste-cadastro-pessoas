<script setup>
import { onMounted, ref } from 'vue'
import { useToast } from 'vue-toastification'
import { cadastrarPessoa, excluirPessoa, listarPessoas } from './services/pessoaService'

const toast = useToast()

const nome = ref('')
const email = ref('')
const idade = ref('')

const pessoas = ref([])

async function cadastrar() {
  try {
    await cadastrarPessoa({
      nome: nome.value,
      email: email.value,
      idade: idade.value
    })

    nome.value = ''
    email.value = ''
    idade.value = ''

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
  if (!confirm('Tem certeza que deseja excluir esta pessoa?')) return

  try {
    await excluirPessoa(id)
    toast.success('Pessoa excluída com sucesso!')
    await carregarPessoas()
  } catch (erro) {
    toast.error('Erro ao excluir pessoa')
  }
}

async function carregarPessoas() {
  try {
    const resposta = await listarPessoas()
    pessoas.value = resposta.data
  } catch (erro) {
    toast.error('Erro ao carregar pessoas')
  }
}

onMounted(() => {
  carregarPessoas();
});
</script>

<template>
  <div class="container">
    <h1>Cadastro de Pessoas</h1>

    <form class="formulario" @submit.prevent="cadastrar">
      <input v-model="nome" placeholder="Nome" />
      <input v-model="email" type="email" placeholder="E-mail" />
      <input v-model.number="idade" type="number" placeholder="Idade" />
      <button type="submit">Cadastrar</button>
    </form>

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
            <button class="btn-excluir" @click="excluir(pessoa.id)">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="pessoas.length === 0" class="vazio">Nenhuma pessoa cadastrada ainda.</p>
    <footer class="assinatura">
      Desenvolvido por <a href="https://gksegura.netlify.app" target="_blank" rel="noopener">José Segura</a>
      · <a href="https://github.com/GKsegura/teste-cadastro-pessoas" target="_blank" rel="noopener">GitHub</a>
    </footer>
  </div>
</template>

<style scoped>
.container {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 16px;
  font-family: 'Segoe UI', system-ui, sans-serif;
  color: #1f2937;
}

h1 {
  text-align: center;
  color: #424141;
  margin-bottom: 32px;
}

.formulario {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
}

.formulario input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.2s;
}

.formulario input:focus {
  border-color: #00C6B9;
}

.formulario button {
  padding: 10px 24px;
  background-color: #00C6B9;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.formulario button:hover {
  background-color: #00A89D;
}

.tabela {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.tabela th {
  background-color: #424141;
  text-align: left;
  padding: 12px 16px;
  font-size: 14px;
  color: #FFFFFF;
}

.tabela td {
  padding: 12px 16px;
  border-top: 1px solid #e5e7eb;
  font-size: 15px;
}

.tabela tbody tr:hover {
  background-color: #f9fafb;
}

.btn-excluir {
  padding: 6px 14px;
  background-color: transparent;
  color: #dc2626;
  border: 1px solid #dc2626;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-excluir:hover {
  background-color: #dc2626;
  color: white;
}

.vazio {
  text-align: center;
  color: #6b7280;
  margin-top: 24px;
  font-style: italic;
}

.assinatura {
  text-align: center;
  margin-top: 48px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
  font-size: 13px;
  color: #9ca3af;
}

.assinatura a {
  color: #00C6B9;
  text-decoration: none;
  font-weight: 600;
}

.assinatura a:hover {
  text-decoration: underline;
}
</style>