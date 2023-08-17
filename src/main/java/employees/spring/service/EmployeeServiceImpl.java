package employees.spring.service;

import static employees.spring.api.EmployeesConfig.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.springframework.stereotype.Service;

import employees.spring.model.Employee;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService, EmployeesPersistance {
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	ReadLock readLock = lock.readLock();
	WriteLock writeLock = lock.writeLock();
	private Map<Long, Employee> mapEmployee = new HashMap<Long, Employee>();
	
	@SuppressWarnings("unlikely-arg-type")
	private @NotEmpty Integer generateId() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int randomId;
		do {
			randomId = random.nextInt(MIN_ID, MAX_ID);
		} while (mapEmployee.containsKey(randomId));
		return randomId;
	}
	
	@Override
	public Employee addEmployee(Employee employee) {
		if (employee.getId() == 0)
			employee.setId(generateId());
		writeLock.lock();
		try {
			Employee res = mapEmployee.putIfAbsent(employee.getId(), employee);
			if (res != null) {
				throw new RuntimeException("Employee with id " + employee.getId() + " already exists");
			}
			return employee;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public  List<Employee> getEmployees() {
		readLock.lock();
		try {
			return new ArrayList<Employee>(mapEmployee.values());
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void deleteEmployee(Long id) {
		writeLock.lock();
		try {
			Employee empl = mapEmployee.remove(id);
			if (empl == null) {
				throw new RuntimeException("Map contained no mapping for the key " + id);
			}
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Employee updateEmployee(Employee empl) {
		readLock.lock();
		writeLock.lock();
		try {
			if (!mapEmployee.containsKey(empl.getId())) {
				throw new RuntimeException("No ad with id " + empl.getId() + " was found");
			}
			Employee emplFound = mapEmployee.put(empl.getId(), empl);			
			return emplFound;
		} finally {
			writeLock.unlock();
			readLock.unlock();

		}
	}

	@PreDestroy
	void storeEmployees() {
		store(mapEmployee.values().stream().toList());
	}
	
	@Override
	public void store(List<Employee> listEmployees) {
		try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
			outputStream.writeObject(listEmployees);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		log.info("Employees are saved to file \"{}\"", fileName);
	}

	@PostConstruct
	private void restoreEmployees() {
		restore().forEach(e -> mapEmployee.put(e.getId(), e));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Employee> restore() {
		List<Employee> res = new ArrayList<Employee>();
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
			var input = inputStream.readObject();
			log.info("Employees are restored from file \"{}\"", fileName);
			res = (List<Employee>) input;
			
		} catch (FileNotFoundException e) {
			log.warn("No file \"{}\" was found - no advertisment data was restored", fileName);
		} catch (Exception e) {
			log.warn("Service cannot restore advertisments: ", e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		return res;
	}

}
