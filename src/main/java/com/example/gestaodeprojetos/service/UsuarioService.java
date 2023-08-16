package com.example.gestaodeprojetos.service;

import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.gestaodeprojetos.model.Usuario;
import com.example.gestaodeprojetos.repository.UsuarioRepository;
import com.example.gestaodeprojetos.security.JWTService;
import com.example.gestaodeprojetos.view.model.usuario.LoginResponse;

@Service
public class UsuarioService {

    private static final String headerPrefix = "Bearer ";

    @Autowired
    private UsuarioRepository repositorioUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public List<Usuario> obterTodos() {
        return repositorioUsuario.findAll();
    }

    public Optional<Usuario> obterPorId(Long id) {
        return repositorioUsuario.findById(id);
    }

    public Optional<Usuario> obterPorEmail(String email) {
        return repositorioUsuario.findByEmail(email);
    }

    public Usuario adicionar(Usuario usuario) {
        usuario.setId(null);

        if (obterPorEmail(usuario.getEmail()).isPresent()) {
            throw new InputMismatchException("Já existe um usuário com o e-mail " + usuario.getEmail());
        }

        // Codificando a senha para não ficar pública, gerando um hash
        String senha = passwordEncoder.encode(usuario.getSenha());

        usuario.setSenha(senha);

        return repositorioUsuario.save(usuario);
    }

    public LoginResponse logar(String email, String senha) {

        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, senha, Collections.emptyList()));

        // Aqui eu passo a noa autenticação para o Spring Security cuidar pra mim
        SecurityContextHolder.getContext().setAuthentication(autenticacao);

        // Gero o token do usuário para devolver a ele
        // Bearer.asf1rr22iueyeui.uysysgyqiuhuhayaya.iijiajaaaa
        String token = headerPrefix + jwtService.gerarToken(autenticacao);

        Usuario usuario = repositorioUsuario.findByEmail(email).get();

        return new LoginResponse(token, usuario); // deveria ser apenas usuario, verificar se o nome está
                                                  // correto, ou se é senha

    }
}
