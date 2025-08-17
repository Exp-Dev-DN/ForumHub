package br.com.alura.forumhub.exception;

public class ValidacaoTokenException extends RuntimeException {
    public ValidacaoTokenException(String message) {
        super(message);
    }
}