package com.audigo.audigo_back.service.admin;

import com.audigo.audigo_back.entity.admin.AdminsMenusEntity;
import com.audigo.audigo_back.repository.admin.AdminsMenusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 메뉴 관리 서비스
 * - 메뉴 CRUD
 * - 메뉴 목록 조회
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminMenuService {

    private final AdminsMenusRepository menusRepository;

    /**
     * 메뉴 등록
     */
    @Transactional
    public void createMenu(String menuCd, String depth1, String depth2, String depth3, String menuUrl) {
        // 메뉴 코드 중복 확인
        if (menusRepository.existsByMenuCd(menuCd)) {
            throw new IllegalArgumentException("이미 존재하는 메뉴 코드입니다.");
        }

        AdminsMenusEntity menu = new AdminsMenusEntity();
        menu.setMenuCd(menuCd);
        menu.setDepth1(depth1);
        menu.setDepth2(depth2);
        menu.setDepth3(depth3);
        menu.setMenuUrl(menuUrl);
        menu.setStts("1");

        menusRepository.save(menu);
        log.info("메뉴 등록 성공: menuCd={}", menuCd);
    }

    /**
     * 메뉴 수정
     */
    @Transactional
    public void updateMenu(String menuCd, String depth1, String depth2, String depth3, String menuUrl) {
        AdminsMenusEntity menu = menusRepository.findByMenuCd(menuCd)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        if (depth1 != null) menu.setDepth1(depth1);
        if (depth2 != null) menu.setDepth2(depth2);
        if (depth3 != null) menu.setDepth3(depth3);
        if (menuUrl != null) menu.setMenuUrl(menuUrl);

        menusRepository.save(menu);
        log.info("메뉴 수정 성공: menuCd={}", menuCd);
    }

    /**
     * 메뉴 삭제 (비활성화)
     */
    @Transactional
    public void deleteMenu(String menuCd) {
        AdminsMenusEntity menu = menusRepository.findByMenuCd(menuCd)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        menu.setStts("0");
        menusRepository.save(menu);
        log.info("메뉴 삭제 성공: menuCd={}", menuCd);
    }

    /**
     * 활성 메뉴 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActiveMenus() {
        List<AdminsMenusEntity> menus = menusRepository.findByStts("1");

        return menus.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 모든 메뉴 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllMenus() {
        List<AdminsMenusEntity> menus = menusRepository.findAll();

        return menus.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 메뉴 상세 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMenu(String menuCd) {
        AdminsMenusEntity menu = menusRepository.findByMenuCd(menuCd)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        return convertToMap(menu);
    }

    /**
     * 1차 메뉴별 하위 메뉴 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMenusByDepth1(String depth1) {
        List<AdminsMenusEntity> menus = menusRepository.findByDepth1(depth1);

        return menus.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * Entity를 Map으로 변환
     */
    private Map<String, Object> convertToMap(AdminsMenusEntity menu) {
        Map<String, Object> map = new HashMap<>();
        map.put("amIdx", menu.getAmIdx().toString());
        map.put("menuCd", menu.getMenuCd());
        map.put("depth1", menu.getDepth1());
        map.put("depth2", menu.getDepth2());
        map.put("depth3", menu.getDepth3());
        map.put("menuUrl", menu.getMenuUrl());
        map.put("stts", menu.getStts());
        map.put("cdt", menu.getCdt());
        return map;
    }
}
