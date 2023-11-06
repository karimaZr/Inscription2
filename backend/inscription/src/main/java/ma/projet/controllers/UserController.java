package ma.projet.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ma.projet.entities.Role;
import ma.projet.entities.User;
import ma.projet.repository.RoleRepository;
import ma.projet.services.UserService;
@RestController
@RequestMapping("/api/user")
public class UserController {
		@Autowired
		private UserService userservice;
		@Autowired
		private RoleRepository roleRepository;
		

		@GetMapping("")
		public List<User> getAllUser() {
			return userservice.findAll();
		}
		

		@GetMapping("/{id}")
		public User getById(@PathVariable Long id) {
			return userservice.findById(id);

		}

		@PostMapping("")
		public User createUser(@RequestBody User user) {
			Role role = roleRepository.findById(user.getRoles().get(0).getId()).get();
			System.out.println(role.getName());
			user.getRoles().add(role);
			return userservice.create(user);
		}

		@PutMapping("/{id}")
		public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody User User) {

			if (User == null) {
				return new ResponseEntity<Object>("User avec ID " + id + " n exite pas", HttpStatus.BAD_REQUEST);
			} else {
				
				return ResponseEntity.ok(userservice.update(User));
				
			}
		}

		@DeleteMapping("/{id}")
		public ResponseEntity delete(@PathVariable Long id) {
			User User = userservice.findById(id);
			
			if (User == null) {
				return new ResponseEntity<Object>("User avec ID " + id + " n exite pas", HttpStatus.BAD_REQUEST);
			} else {
				userservice.delete(User);
				return ResponseEntity.ok(" supression avec succes ");

			}
		}
	}


