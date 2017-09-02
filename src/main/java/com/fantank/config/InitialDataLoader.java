package com.fantank.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fantank.model.Offering;
import com.fantank.model.Privilege;
import com.fantank.model.Role;
import com.fantank.model.User;
import com.fantank.repository.OfferingRepository;
import com.fantank.repository.PrivilegeRepository;
import com.fantank.repository.RoleRepository;
import com.fantank.repository.UserRepository;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private OfferingRepository offeringRepository;

    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // == create initial privileges
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        // == create initial roles
        final List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege);
        final List<Privilege> userPrivileges = Arrays.asList(readPrivilege, passwordPrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_INVESTOR", userPrivileges);

        if(userRepository.findByEmail("admin@bcinnovationsonline.com") == null) {
	        final Role adminRole = roleRepository.findByName("ROLE_ADMIN");
	        final User user = new User();
	        user.setFirstName("Admin");
	        user.setLastName("Admin");
	        user.setPassword(passwordEncoder.encode("test"));
	        user.setEmail("admin@bcinnovationsonline.com");
	        user.setRoles(Arrays.asList(adminRole));
	        user.setEnabled(true);
	        userRepository.save(user);
    	}

        if(offeringRepository.findByOfferingId("pi40LCw9QripXEowWqR2hA") == null) {
        	Offering chance = new Offering();
        	chance.setId("chancetherapper");
        	chance.setOfferingId("pi40LCw9QripXEowWqR2hA");
        	offeringRepository.save(chance);
        }
       
        alreadySetup = true;
    }

    @Transactional
    private final Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    private final Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }

}
