package com.croquis.vacation.repository;

import com.croquis.vacation.domain.UserInfo;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserInfo, String> {
}
