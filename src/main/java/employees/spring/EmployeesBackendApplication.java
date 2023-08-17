package employees.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(
		  basePackages = {"telran.spring.beersheba", "employees.spring"}
		)
@SpringBootApplication
public class EmployeesBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeesBackendApplication.class, args);
	}

}
