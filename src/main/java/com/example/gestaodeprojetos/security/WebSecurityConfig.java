package com.example.gestaodeprojetos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Aqui informo que é uma classe de segurança do Web Security
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    /*
     * Método que devolve a instancia do objeto que sabe devolver o nosso padrão de
     * codificação
     * Isso não tem nada a ver com o JWT
     * Aqui será usado para codificar a senha do usuário gerando um hash
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Método padrão para configurar o nosso custom com o nosso método de codificar
    // senha
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    // Método padrão: Esse método é obrigatório para conseguirmos trabalhar com
    // aautenticação no login
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // Método que tem a configuração global de acessos e permissões por rotas
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Parte padrão da configuração, por enqquanto ignorar
        http
                .cors().and().csrf().disable()
                .exceptionHandling()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()

                // Daqui pra baixo é onde nós vamos fazer nossas validações
                // Aqui vamos informar as rotas que não precisam de autenticação

                .antMatchers(HttpMethod.POST, "/api/usuarios", "/api/usuarios/login")
                .permitAll() // Informa que todos podem acesar,não precisa de autenticação

                .anyRequest()
                .authenticated(); // As demais requisições devem ser autenticadas

        // Aqui informamos que antes qualquer requisição http, o sistema deve usar o
        // nosso filtro jwtAuthenticationFilter
        // http.addFilterBefore(jwtAuthenticationFilter,
        // UsernamePasswordAuthenticationFilter.class);
    }

}
