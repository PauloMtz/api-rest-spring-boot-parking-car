package com.parking;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.parking.web.dto.UsuarioCreateDTO;
import com.parking.web.dto.UsuarioResponseDTO;
import com.parking.web.dto.UsuarioSenhaDTO;
import com.parking.web.exception.ErrorMessage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/usuarios/usuarios-insert.sql", 
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", 
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsuarioIT {
    
    @Autowired
    WebTestClient webTestClient;

    @Test
    public void createUsuario_comEmailESenhaValidos_retornoUsuarioCriadoStatus201() {
        UsuarioResponseDTO responseBody = webTestClient
            .post()
            .uri("/api/v2/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioCreateDTO("tody@mail.com", "123456"))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(UsuarioResponseDTO.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("tody@mail.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void createUsuario_comEmailInvalido_retornoErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v2/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioCreateDTO("", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v2/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioCreateDTO("@com", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUsuario_comSenhaInvalida_retornoErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v2/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioCreateDTO("tody@mail.com", ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v2/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioCreateDTO("tody@mail.com", "12345678901"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUsuario_comEmailRepetido_retornoErrorMessageStatus409() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v2/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioCreateDTO("jose.teste@mail.com", "123456"))
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void buscaUsuario_comIdExistente_retornoUsuarioStatus200() {
        UsuarioResponseDTO responseBody = webTestClient
            .get()
            .uri("/api/v2/usuarios/100")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "pedro.teste@mail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(UsuarioResponseDTO.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(100);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("pedro.teste@mail.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("ADMIN");

        responseBody = webTestClient
            .get()
            .uri("/api/v2/usuarios/101")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "pedro.teste@mail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(UsuarioResponseDTO.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("maria.teste@mail.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");

        responseBody = webTestClient
            .get()
            .uri("/api/v2/usuarios/101")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "maria.teste@mail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(UsuarioResponseDTO.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("maria.teste@mail.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void buscaUsuario_clienteBuscaOutroCliente_retornoErrorMessageStatus403() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v2/usuarios/102")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "maria.teste@mail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void buscaUsuario_comIdInexistente_retornoErrorMessageStatus404() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v2/usuarios/0")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "pedro.teste@mail.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void editarSenha_comDadosValidos_retornoStatus204() {
        webTestClient
            .patch()
            .uri("/api/v2/usuarios/100")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "pedro.teste@mail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioSenhaDTO("123456", "abc123", "abc123"))
            .exchange()
            .expectStatus().isNoContent();

        webTestClient
            .patch()
            .uri("/api/v2/usuarios/101")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "maria.teste@mail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioSenhaDTO("123456", "abc123", "abc123"))
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    public void editarSenha_comUsuariosDiferentes_retornoErrorMessageStatus403() {
        ErrorMessage responseBody = webTestClient
            .patch()
            .uri("/api/v2/usuarios/0")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "pedro.teste@mail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioSenhaDTO("123456", "abc123", "abc123"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

        responseBody = webTestClient
            .patch()
            .uri("/api/v2/usuarios/0")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "maria.teste@mail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioSenhaDTO("123456", "abc123", "abc123"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void editarSenha_comDadosInvalidos_retornoErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .patch()
            .uri("/api/v2/usuarios/100")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "pedro.teste@mail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioSenhaDTO("", "", ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .patch()
            .uri("/api/v2/usuarios/100")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "pedro.teste@mail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioSenhaDTO("senha@123", "12345678901", "abc123456789"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void editarSenha_comSenhaInvalida_retornoErrorMessageStatus400() {
        ErrorMessage responseBody = webTestClient
            .patch()
            .uri("/api/v2/usuarios/100")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "pedro.teste@mail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioSenhaDTO("123456", "abc123", "000000"))
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        responseBody = webTestClient
            .patch()
            .uri("/api/v2/usuarios/100")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "pedro.teste@mail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UsuarioSenhaDTO("senha@123", "123456", "123456"))
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void listaUsuarios_retornoUsuariosStatus200() {
        List<UsuarioResponseDTO> responseBody = webTestClient
            .get()
            .uri("/api/v2/usuarios")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "pedro.teste@mail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(UsuarioResponseDTO.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.size()).isEqualTo(3);
    }

    @Test
    public void listarUsuarios_ComUsuarioSemPermissao_RetornarErrorMessageComStatus403() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v2/usuarios/102")
            .headers(JwtAuthentication
                .getHeaderAuthorization(webTestClient, "maria.teste@mail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }
}
