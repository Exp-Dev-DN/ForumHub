package br.com.alura.forumhub.controller;

import br.com.alura.forumhub.dto.*;
import br.com.alura.forumhub.exception.ValidacaoException;
import br.com.alura.forumhub.model.Resposta;
import br.com.alura.forumhub.model.Usuario;
import br.com.alura.forumhub.repository.RespostaRepository;
import br.com.alura.forumhub.repository.TopicoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/topicos/{idTopico}/respostas")
public class RespostaController {

    @Autowired
    private RespostaRepository respostaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DtoDetalhamentoResposta> criarResposta(
            @PathVariable Long idTopico,
            @RequestBody @Valid DtoCadastroResposta dados,
            @AuthenticationPrincipal Usuario autor,
            UriComponentsBuilder uriBuilder) {

        var topico = topicoRepository.findById(idTopico)
                .orElseThrow(() -> new EntityNotFoundException("Tópico com id " + idTopico + " não encontrado."));

        var resposta = new Resposta(null, dados.mensagem(), topico, LocalDateTime.now(), autor, false);
        respostaRepository.save(resposta);

        var uri = uriBuilder.path("/topicos/{idTopico}/respostas/{idResposta}").buildAndExpand(idTopico, resposta.getId()).toUri();
        return ResponseEntity.created(uri).body(new DtoDetalhamentoResposta(resposta));
    }

    @GetMapping
    public ResponseEntity<List<DtoListagemResposta>> listarRespostas(@PathVariable Long idTopico) {
        if (!topicoRepository.existsById(idTopico)) {
            throw new EntityNotFoundException("Tópico com id " + idTopico + " não encontrado.");
        }

        var respostas = respostaRepository.findByTopicoId(idTopico);

        var listaDto = respostas.stream()
                .map(DtoListagemResposta::new)
                .toList();

        return ResponseEntity.ok(listaDto);
    }

    @PutMapping("/{idResposta}")
    @Transactional
    public ResponseEntity<DtoDetalhamentoResposta> atualizarResposta(
            @PathVariable Long idTopico,
            @PathVariable Long idResposta,
            @RequestBody @Valid DtoAtualizacaoResposta dados,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        var resposta = respostaRepository.findById(idResposta)
                .orElseThrow(() -> new EntityNotFoundException("Resposta com id " + idResposta + " não encontrada."));

        if (!resposta.getTopico().getId().equals(idTopico)) {
            throw new ValidacaoException("Esta resposta não pertence ao tópico informado.");
        }

        if (!resposta.getAutor().equals(usuarioLogado)) {
            throw new ValidacaoException("Apenas o autor original pode editar esta resposta.");
        }

        resposta.atualizarInformacoes(dados.mensagem());

        return ResponseEntity.ok(new DtoDetalhamentoResposta(resposta));
    }

    @DeleteMapping("/{idResposta}")
    @Transactional
    public ResponseEntity<Void> deletarResposta(
            @PathVariable Long idTopico,
            @PathVariable Long idResposta,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        var resposta = respostaRepository.findById(idResposta)
                .orElseThrow(() -> new EntityNotFoundException("Resposta com id " + idResposta + " não encontrada."));

        if (!resposta.getTopico().getId().equals(idTopico)) {
            throw new ValidacaoException("Esta resposta não pertence ao tópico informado.");
        }

        var autorDoTopico = resposta.getTopico().getAutor();
        if (!resposta.getAutor().equals(usuarioLogado) && !autorDoTopico.equals(usuarioLogado.getUsername())) {
            throw new ValidacaoException("Apenas o autor da resposta ou do tópico podem excluir.");
        }

        respostaRepository.delete(resposta);

        return ResponseEntity.noContent().build();
    }
}
