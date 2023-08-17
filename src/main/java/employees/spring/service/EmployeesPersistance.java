package employees.spring.service;

import java.util.List;

import employees.spring.model.Employee;

public interface EmployeesPersistance {
	void store(List<Employee> listEmployees);
	List<Employee> restore();
}
