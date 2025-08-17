package br.com.alura.forumhub.security;

import br.com.alura.forumhub.exception.ValidacaoTokenException;
import br.com.alura.forumhub.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private static final String SECRET_KEY = "minha-senha-super-secreta-para-o-teste";
    private static final String ISSUER = "API ForumHub";

    @Test
    @DisplayName("Deveria gerar um token JWT válido")
    void gerarToken_deveGerarTokenValido() {
        ReflectionTestUtils.setField(tokenService, "secret", SECRET_KEY);

        var usuario = new Usuario();
        usuario.setLogin("fulano.de.tal@example.com");

        String tokenGerado = tokenService.gerarToken(usuario);

        var algoritmo = Algorithm.HMAC256(SECRET_KEY);
        String subject = JWT.require(algoritmo)
                .withIssuer(ISSUER)
                .build()
                .verify(tokenGerado)
                .getSubject();

        assertThat(subject).isEqualTo("fulano.de.tal@example.com");
    }

    @Test
    @DisplayName("Deveria retornar o subject (login) de um token JWT válido")
    void getSubject_comTokenValido_deveRetornarSubject() {
        ReflectionTestUtils.setField(tokenService, "secret", SECRET_KEY);

        String tokenValido = JWT.create()
                .withIssuer(ISSUER)
                .withSubject("usuario.teste@example.com")
                .withExpiresAt(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")))
                .sign(Algorithm.HMAC256(SECRET_KEY));

        String subject = tokenService.getSubject(tokenValido);

        assertThat(subject).isEqualTo("usuario.teste@example.com");
    }

    @Test
    @DisplayName("Deveria lançar ValidacaoTokenException para um token inválido")
    void getSubject_comTokenInvalido_deveLancarExcecao() {
        ReflectionTestUtils.setField(tokenService, "secret", SECRET_KEY);
        String tokenInvalido = "um.token.jwt.completamente.invalido";

        var exception = assertThrows(ValidacaoTokenException.class, () -> tokenService.getSubject(tokenInvalido));

        assertThat(exception.getMessage()).isEqualTo("Token JWT inválido ou expirado!");
    }
}