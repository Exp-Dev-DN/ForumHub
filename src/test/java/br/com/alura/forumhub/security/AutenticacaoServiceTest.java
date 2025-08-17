package br.com.alura.forumhub.security;

import static org.mockito.Mockito.when;
import br.com.alura.forumhub.model.Usuario;
import br.com.alura.forumhub.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Test
    @DisplayName("Deveria retornar UserDetails quando o usuário existe")
    void loadUserByUsername_cenario1() {
        // Arrange
        String loginExistente = "fulano.tal";
        Usuario usuarioMock = new Usuario(1L, loginExistente, "senha123");

        when(usuarioRepository.findByLogin(loginExistente)).thenReturn(Optional.of(usuarioMock));

        // Act
        UserDetails userDetails = autenticacaoService.loadUserByUsername(loginExistente);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(loginExistente);
    }

    @Test
    @DisplayName("Deveria lançar UsernameNotFoundException quando o usuário não existe")
    void loadUserByUsername_cenario2() {
        // Arrange
        String loginInexistente = "ninguem.aqui";

        when(usuarioRepository.findByLogin(loginInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> autenticacaoService.loadUserByUsername(loginInexistente));
    }
}