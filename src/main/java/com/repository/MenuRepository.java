package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.MenuEntity;

public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {

}
