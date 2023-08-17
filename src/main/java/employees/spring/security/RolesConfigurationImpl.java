package employees.spring.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import telran.spring.beersheba.security.RolesConfiguration;

@Configuration
public class RolesConfigurationImpl implements RolesConfiguration{
	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests(requests -> requests.requestMatchers(HttpMethod.GET).authenticated()
				.anyRequest().hasRole("ADMIN"));
	}
}
