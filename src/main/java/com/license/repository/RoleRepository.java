package com.license.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.license.model.Role;
import com.license.model.UserDtls;

public interface RoleRepository extends JpaRepository < Role,Integer>{

	
}
