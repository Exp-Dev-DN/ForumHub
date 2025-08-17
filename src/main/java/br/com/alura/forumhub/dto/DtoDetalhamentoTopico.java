package br.com.alura.forumhub.dto;

import br.com.alura.forumhub.model.StatusTopico;
import br.com.alura.forumhub.model.Topico;

import java.time.LocalDateTime;

public record DtoDetalhamentoTopico(
        Long id,
        String titulo,
        String mensagem,
        LocalDateTime dataCriacao,
        StatusTopico status,
        String autor,
        String curso)
{
    public DtoDetalhamentoTopico(Topico topico) {
        this(topico.getId(), topico.getTitulo(), topico.getMensagem(), topico.getDataCriacao(), topico.getStatus(), topico.getAutor(), topico.getCurso());
    }
}
