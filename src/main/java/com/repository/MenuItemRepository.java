package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.MenuEntity;
import com.entity.MenuItemEntity;
import java.util.List;
import java.util.Optional;


public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Integer>{
	Optional<List<MenuItemEntity>> findByMenu(MenuEntity menu);
}
