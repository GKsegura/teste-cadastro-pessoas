import { createApp } from 'vue'
import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'

import PrimeVue from 'primevue/config'
import ConfirmationService from 'primevue/confirmationservice'
import Aura from '@primeuix/themes/aura'
import { definePreset } from '@primeuix/themes'

import App from './App.vue'

import '@/assets/css/styles.css'

// Paleta Nexum: teal como cor primária, grafite/branco vêm do preset padrão.
const NexumPreset = definePreset(Aura, {
    semantic: {
        primary: {
            50: '#ebfefd',
            100: '#d3fdfa',
            200: '#a3fff9',
            300: '#66fff5',
            400: '#1afff0',
            500: '#00c6b9',
            600: '#00a398',
            700: '#00857c',
            800: '#006b64',
            900: '#07504b',
            950: '#043431'
        }
    }
})

const app = createApp(App)

app.use(Toast, {
    position: 'bottom-right',
    timeout: 3000
})

app.use(PrimeVue, {
    theme: {
        preset: NexumPreset,
        options: {
            darkModeSelector: false
        }
    },
    license: import.meta.env.VITE_PRIMEUI_LICENSE
})
app.use(ConfirmationService)

app.mount('#app')