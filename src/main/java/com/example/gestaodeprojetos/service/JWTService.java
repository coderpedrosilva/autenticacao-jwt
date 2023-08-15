package com.example.gestaodeprojetos.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.gestaodeprojetos.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTService {

    // É a chave secreta utilizada pelo JWT para codificar e decodificar o token
    private static final String chavePrivadaJWT = "secretKey";

    /**
     * mètodo para gerar o token JWT
     * 
     * @param authentication // autenticação do usuário
     * @return // retornar um token JWT
     */
    public String gerarToken(Authentication authentication) {

        // Aqui pode variar de acordo com a sua regra de negócio
        int tempoExpiracao = 86400000; // 1 dia em milissegundos

        // Aqui estou criando uma data de expiração para o token com base no tempo de
        // expiração
        // Ele pega a data atual e soma mais u dia em milissegundoss
        Date dataExpiracao = new Date(new Date().getTime() + tempoExpiracao);

        // Aqui estou pegando o usuário atual da autenticação
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Aqui ele pega todos os dados e retorna um token bonito do JWT
        return Jwts.builder()
                .setSubject(usuario.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(dataExpiracao)
                .signWith(SignatureAlgorithm.HS512, chavePrivadaJWT)
                .compact();
    }

    /**
     * Método para retornar o id do usuário dono do token
     * 
     * @param token //Token do usuário
     * @return //Id do usuário
     */
    public Optional<Long> obterIdDoUsuario(String token) {
        try {
            // Retorna as permissões do token
            Claims claims = parse(token).getBody();

            // Retorna o id de dentro do token se encontrar, caso contrário retorna null
            return Optional.ofNullable(Long.parseLong(claims.getSubject()));

        } catch (Exception e) {
            // Se não encontrar nada, devolve um optional null
            return Optional.empty();
        }
    }

    // Método que sabe descobrir de dentro do token com base na chave privada qual
    // as permissões (claims) do usuário
    private Jws<Claims> parse(String token) {
        return Jwts.parser().setSigningKey(chavePrivadaJWT).parseClaimsJws(token);
    }
}
