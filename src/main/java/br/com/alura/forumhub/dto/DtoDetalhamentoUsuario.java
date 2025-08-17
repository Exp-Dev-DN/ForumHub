package br.com.alura.forumhub.dto;

import br.com.alura.forumhub.model.Usuario;

public record DtoDetalhamentoUsuario(
        Long id,
        String login
) {
    public DtoDetalhamentoUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getLogin());
    }
}