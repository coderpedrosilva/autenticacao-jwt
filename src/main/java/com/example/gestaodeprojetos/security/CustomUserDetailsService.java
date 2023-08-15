package com.example.gestaodeprojetos.security;

import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.gestaodeprojetos.model.Usuario;
import com.example.gestaodeprojetos.service.UsuarioService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        // Usuario usuario = getUser(() -> usuarioService.obterPorEmail(email));
        // return usuario;

        return usuarioService.obterPorEmail(email).get();
    }

    public UserDetails obterUsuarioPorId(Long id) {

        return usuarioService.obterPorId(id).get();
    }

    /*
     * private Usuario getUser(Supplier<Optional<Usuario>> supplier) {
     * return supplier.get().orElseThrow(() -> new
     * UsernameNotFoundException("Usuário não encontrado"));
     * }
     */

}
