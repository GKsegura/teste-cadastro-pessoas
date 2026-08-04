package br.com.gksegura.cadastropessoas.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfOuCnpjValidator implements ConstraintValidator<CpfOuCnpj, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.isBlank()) {
            return true;
        }

        String digitos = valor.replaceAll("\\D", "");

        return switch (digitos.length()) {
            case 11 -> cpfValido(digitos);
            case 14 -> cnpjValido(digitos);
            default -> false;
        };
    }

    private boolean cpfValido(String cpf) {
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        int[] numeros = cpf.chars().map(c -> c - '0').toArray();

        int primeiroDigito = calcularDigito(numeros, 9, 10);
        if (primeiroDigito != numeros[9]) {
            return false;
        }

        int segundoDigito = calcularDigito(numeros, 10, 11);
        return segundoDigito == numeros[10];
    }

    private boolean cnpjValido(String cnpj) {
        if (cnpj.chars().distinct().count() == 1) {
            return false;
        }

        int[] numeros = cnpj.chars().map(c -> c - '0').toArray();
        int[] pesosPrimeiroDigito = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
        int[] pesosSegundoDigito = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

        int primeiroDigito = calcularDigitoComPesos(numeros, pesosPrimeiroDigito);
        if (primeiroDigito != numeros[12]) {
            return false;
        }

        int segundoDigito = calcularDigitoComPesos(numeros, pesosSegundoDigito);
        return segundoDigito == numeros[13];
    }

    private int calcularDigito(int[] numeros, int quantidadeDigitos, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < quantidadeDigitos; i++) {
            soma += numeros[i] * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private int calcularDigitoComPesos(int[] numeros, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += numeros[i] * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
