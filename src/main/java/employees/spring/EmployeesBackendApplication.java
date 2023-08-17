package employees.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(
		  basePackages = {"telran", "employees.spring"}
		)
public class EmployeesBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeesBackendApplication.class, args);
	}

}
