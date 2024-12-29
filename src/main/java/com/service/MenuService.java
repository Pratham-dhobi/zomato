package com.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.entity.MenuEntity;
import com.repository.MenuRepository;

@Service
public class MenuService {

	@Autowired
	MenuRepository menuRepository;
	
	public HttpStatus softDeleteMenuService(Integer menuId) {
		Optional<MenuEntity> op = menuRepository.findById(menuId);
		
		if(op.isPresent()) {
			MenuEntity menu = op.get();
			menu.setActive(0);
			menuRepository.save(menu);
			return HttpStatus.OK;
		}else {
			return HttpStatus.NOT_FOUND;
		}
	}
}
