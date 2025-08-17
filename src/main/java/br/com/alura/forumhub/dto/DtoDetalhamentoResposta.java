package br.com.alura.forumhub.dto;

import br.com.alura.forumhub.model.Resposta;

import java.time.LocalDateTime;

public record DtoDetalhamentoResposta(
        Long id,
        String mensagem,
        String nomeTopico,
        LocalDateTime dataCriacao,
        String nomeAutor,
        Boolean solucao
) {
    public DtoDetalhamentoResposta(Resposta resposta) {
        this(
                resposta.getId(),
                resposta.getMensagem(),
                resposta.getTopico().getTitulo(),
                resposta.getDataCriacao(),
                resposta.getAutor().getUsername(),
                resposta.getSolucao()
        );
    }
}
