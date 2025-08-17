package br.com.alura.forumhub.dto;

import br.com.alura.forumhub.model.StatusTopico;

public record DtoAtualizacaoTopico(
        String titulo,
        String mensagem,
        StatusTopico status) {
}
