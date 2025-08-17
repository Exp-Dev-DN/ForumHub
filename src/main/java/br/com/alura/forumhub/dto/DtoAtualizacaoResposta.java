package br.com.alura.forumhub.dto;

import jakarta.validation.constraints.NotBlank;

public record DtoAtualizacaoResposta(
        @NotBlank(message = "A mensagem não pode ficar em branco na atualização.")
        String mensagem) {
}
