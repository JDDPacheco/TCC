package com.josepacheco.tcc.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converte erros de entrada (validação do @Valid e JSON mal formado) em respostas 400
 * no mesmo formato do erro padrão do Spring, com uma mensagem legível em "message".
 * Assim o frontend pode exibir sempre error.response.data.message.
 */
@RestControllerAdvice
public class TratadorDeErros {

    private static final Logger log = LoggerFactory.getLogger(TratadorDeErros.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException excecao, HttpServletRequest requisicao) {
        String mensagem = excecao.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .distinct()
                .collect(Collectors.joining(" "));
        return montarResposta(mensagem, requisicao);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> tratarCorpoInvalido(HttpMessageNotReadableException excecao, HttpServletRequest requisicao) {
        log.warn("Corpo inválido em {}: {}", requisicao.getRequestURI(), excecao.getMostSpecificCause().getMessage());
        return montarResposta("O corpo da requisição é inválido ou está mal formatado (verifique números e datas).", requisicao);
    }

    // Parâmetro de URL com tipo errado (ex.: data fora do formato aaaa-mm-dd, id não numérico)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> tratarParametroInvalido(MethodArgumentTypeMismatchException excecao, HttpServletRequest requisicao) {
        return montarResposta("O parâmetro '" + excecao.getName() + "' é inválido: " + excecao.getValue() + ".", requisicao);
    }

    private ResponseEntity<Map<String, Object>> montarResposta(String mensagem, HttpServletRequest requisicao) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", OffsetDateTime.now());
        corpo.put("status", HttpStatus.BAD_REQUEST.value());
        corpo.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        corpo.put("message", mensagem);
        corpo.put("path", requisicao.getRequestURI());
        return ResponseEntity.badRequest().body(corpo);
    }
}
