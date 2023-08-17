package employees.spring.service;

import java.util.List;

import employees.spring.model.Employee;

public interface EmployeeService {
    Employee addEmployee(Employee employee);
    List<Employee> getEmployees();
    void deleteEmployee(Long id);
    Employee updateEmployee(Employee empl);
	Employee getEmployee(long id);
}
