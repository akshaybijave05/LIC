package com.becoder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.becoder.model.Role;
import com.becoder.model.UserDtls;

public interface RoleRepository extends JpaRepository < Role,Integer>{

	
}
