package com.smartjobportal.service;

import com.smartjobportal.model.User;
import com.smartjobportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        if (!StringUtils.hasText(user.getRole())) {
            user.setRole("candidate");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(Long id, User update) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setName(update.getName());
                    existing.setEmail(update.getEmail());
                    existing.setRole(update.getRole());
                    existing.setSkills(update.getSkills());
                    existing.setExperienceYears(update.getExperienceYears());
                    existing.setPreferredRole(update.getPreferredRole());
                    existing.setEmploymentStatus(update.getEmploymentStatus());
                    existing.setHighestQualification(update.getHighestQualification());
                    existing.setSpecialization(update.getSpecialization());
                    existing.setGraduationYear(update.getGraduationYear());
                    existing.setPhoneNumber(update.getPhoneNumber());

                    existing.setLocation(update.getLocation());
                    existing.setLinkedInProfileUrl(update.getLinkedInProfileUrl());
                    existing.setGithubPortfolioUrl(update.getGithubPortfolioUrl());
                    existing.setPortfolioWebsite(update.getPortfolioWebsite());
                    if (update.getPassword() != null && !update.getPassword().isBlank()) {
                        existing.setPassword(passwordEncoder.encode(update.getPassword()));
                    }
                    return userRepository.save(existing);
                })
                .orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRoleIgnoreCase(role);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
