package com.billing.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.billing.dto.UserDto;
import com.billing.entity.User;
import com.billing.exception.ResourceNotFoundException;
import com.billing.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDto createUser(UserDto userDto) {
		User user = modelMapper.map(userDto, User.class);
		user.setRoles("ROLE_USER");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setId(UUID.randomUUID().toString());
		user.setActive(true);
		User saveduser = userRepository.save(user);
		UserDto savedUserDto = modelMapper.map(saveduser, UserDto.class);
		return savedUserDto;
	}

	@Override
	public List<UserDto> getAllUsers() {
		List<User> users = userRepository.findAll();
		return users.stream().filter(User::isActive) // Filter only active users
				.map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());
	}

	@Override
	public UserDto getUserById(String userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist by id " + userId));
		UserDto dtoUser = modelMapper.map(user, UserDto.class);
		return dtoUser;
	}

	@Override
	public UserDto getUserByUsername(String userName) {
	    Optional<User> optionalUser = userRepository.findByUserName(userName);
	    User user = optionalUser.orElseThrow(() -> new ResourceNotFoundException("User not found by "+userName));
	    UserDto dtoUser = modelMapper.map(user, UserDto.class);
	    return dtoUser;
	}

	@Override
	public UserDto updateUser(UserDto user) {
		User existingUser = userRepository.findById(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist by id " + user.getId()));
		existingUser.setFirstName(user.getFirstName());
		existingUser.setLastName(user.getLastName());
		existingUser.setEmail(user.getEmail());
		User updatedUser = userRepository.save(existingUser);
		return modelMapper.map(updatedUser, UserDto.class);
	}

	@Override
	public void deleteUser(String userId) {
		UserDto userDto = this.getUserById(userId);
		userDto.setActive(false);
		User user = modelMapper.map(userDto, User.class);
		userRepository.save(user);
	}

//	@Override
//	public User validateUser(String userName, String password) {
//////		User user = userRepository.findByUsername(userName);
//////		if (user != null && user.getPassword().equals(password)) {
//////			return user;
//////		}
//////		return null;
////		
//		User user = userRepository.findByUsername(userName).get();
//	    if (user != null && user.getPassword().equals(password)) {
//	        return user;
//	    }
//	    else {
//		    return null;
//	    }
//	}
}