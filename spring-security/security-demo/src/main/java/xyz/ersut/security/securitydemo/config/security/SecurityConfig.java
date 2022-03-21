package xyz.ersut.security.securitydemo.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xyz.ersut.security.securitydemo.config.security.jwt.MyUserDetailsService;
import xyz.ersut.security.securitydemo.config.security.jwt.filter.JwtAuthFilter;
import xyz.ersut.security.securitydemo.config.security.openapi.filter.OpenAPIFilter;
import xyz.ersut.security.securitydemo.config.security.openapi.provider.OpenApiAuthenticationProvider;

@Configuration
//开启注解式权限
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    @Autowired
    private OpenAPIFilter openAPIFilter;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    //配置 用户读取的位置（数据库还是缓存或者内存）
    @Bean
    public UserDetailsService userDetailsService(){
        return new MyUserDetailsService();
    }

    //密码的加密解密
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    //配置 spring Security 的规则
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                //对于登录接口 允许匿名访问(anonymous)
                .antMatchers("/user/login","/").anonymous()
                //对于 /hello 接口,无论是否登录都可以登陆(permitAll)
                .antMatchers("/hello").permitAll()
                //其他接口都需要认证
                .anyRequest().authenticated();

        //异常处理（如果有使用spring的全局异常并捕获了Exception异常，那么AccessDeniedException和AuthenticationException也需要捕获）
        http
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        //将jwt认证过滤器加入Security过滤器链中，并放在 UsernamePasswordAuthenticationFilter 之前
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .authenticationProvider(new OpenApiAuthenticationProvider())
                .addFilterBefore(openAPIFilter, UsernamePasswordAuthenticationFilter.class);

        //允许跨域
        http.cors();
    }

    //暴漏 AuthenticationManager
    @Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
	    return super.authenticationManagerBean();
	}

}
