package com.socially.user.kernel.infrastructure.right.adapter.persistence;

import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {}
