package com.devgwon.growthsimulator.monster.service;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.service.DeveloperProfileService;
import com.devgwon.growthsimulator.growth.domain.GrowthCategory;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.growth.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.monster.domain.Monster;
import com.devgwon.growthsimulator.monster.domain.MonsterDifficulty;
import com.devgwon.growthsimulator.monster.domain.MonsterStatus;
import com.devgwon.growthsimulator.monster.domain.MonsterType;
import com.devgwon.growthsimulator.monster.repository.MonsterRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonsterService {

    private static final int DEFAULT_DAMAGE = 10;

    private final MonsterRepository monsterRepository;
    private final DeveloperProfileService profileService;
    private final GrowthCategoryRepository categoryRepository;
    private final GrowthSubCategoryRepository subCategoryRepository;

    public MonsterService(
            MonsterRepository monsterRepository,
            DeveloperProfileService profileService,
            GrowthCategoryRepository categoryRepository,
            GrowthSubCategoryRepository subCategoryRepository
    ) {
        this.monsterRepository = monsterRepository;
        this.profileService = profileService;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<MonsterView> findMonstersForDefaultProfile() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        return monsterRepository.findByProfileAndStatusNotOrderByCreatedAtDesc(profile, MonsterStatus.ARCHIVED).stream()
                .map(MonsterView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Monster getMonster(Long id) {
        return monsterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("몬스터를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public MonsterView getView(Long id) {
        return MonsterView.from(getMonster(id));
    }

    @Transactional
    public Monster create(MonsterCreateRequest request) {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        validateCreate(profile, request);
        MonsterDifficulty difficulty = request.getDifficulty() == null ? MonsterDifficulty.NORMAL : request.getDifficulty();
        int maxHp = resolveMaxHp(request.getMaxHp(), difficulty);
        Monster monster = new Monster(
                profile,
                normalizeRequired(request.getName(), "몬스터 이름을 입력해 주세요."),
                normalizeCode(request.getCode()),
                normalize(request.getDescription()),
                request.getMonsterType() == null ? MonsterType.ETC : request.getMonsterType(),
                difficulty,
                maxHp,
                resolveRewardExp(request.getRewardExp()),
                findCategory(request.getRelatedGrowthCategoryId()),
                findSubCategory(request.getRelatedGrowthSubCategoryId())
        );
        return monsterRepository.save(monster);
    }

    @Transactional
    public void update(Long id, MonsterUpdateRequest request) {
        Monster monster = getMonster(id);
        validateUpdate(monster, request);
        MonsterDifficulty difficulty = request.getDifficulty() == null ? MonsterDifficulty.NORMAL : request.getDifficulty();
        monster.update(
                normalizeRequired(request.getName(), "몬스터 이름을 입력해 주세요."),
                normalizeCode(request.getCode()),
                normalize(request.getDescription()),
                request.getMonsterType() == null ? MonsterType.ETC : request.getMonsterType(),
                difficulty,
                resolveMaxHp(request.getMaxHp(), difficulty),
                resolveRewardExp(request.getRewardExp()),
                findCategory(request.getRelatedGrowthCategoryId()),
                findSubCategory(request.getRelatedGrowthSubCategoryId())
        );
    }

    @Transactional
    public MonsterAttackResult attack(Long id, MonsterAttackRequest request) {
        Monster monster = getMonster(id);
        int damage = request == null || request.getDamage() == null ? DEFAULT_DAMAGE : request.getDamage();
        monster.attack(damage);
        boolean defeated = monster.getStatus() == MonsterStatus.DEFEATED;
        String message = defeated
                ? monster.getName() + " 몬스터를 처치했어요. 보상 EXP는 아직 표시용으로만 남겨둘게요."
                : monster.getName() + " 몬스터에게 " + damage + " 데미지를 줬어요.";
        return new MonsterAttackResult(
                monster.getId(),
                monster.getName(),
                damage,
                monster.getCurrentHp(),
                monster.getMaxHp(),
                defeated,
                message
        );
    }

    @Transactional
    public void archive(Long id) {
        Monster monster = getMonster(id);
        monster.archive();
    }

    @Transactional(readOnly = true)
    public DashboardMonsterSummary getDashboardSummary() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        List<MonsterStatus> battleStatuses = List.of(MonsterStatus.ACTIVE, MonsterStatus.WEAKENED);
        return new DashboardMonsterSummary(
                monsterRepository.countByProfileAndStatusIn(profile, battleStatuses),
                monsterRepository.findFirstByProfileAndStatusInOrderByCurrentHpAsc(profile, battleStatuses)
                        .map(MonsterView::from)
                        .orElse(null),
                monsterRepository.findFirstByProfileAndStatusOrderByDefeatedAtDesc(profile, MonsterStatus.DEFEATED)
                        .map(MonsterView::from)
                        .orElse(null)
        );
    }

    public MonsterCreateRequest newCreateRequest() {
        MonsterCreateRequest request = new MonsterCreateRequest();
        request.setDifficulty(MonsterDifficulty.NORMAL);
        request.setMonsterType(MonsterType.ETC);
        request.setMaxHp(MonsterDifficulty.NORMAL.getDefaultMaxHp());
        request.setRewardExp(0);
        return request;
    }

    private void validateCreate(DeveloperProfile profile, MonsterCreateRequest request) {
        if (monsterRepository.existsByProfileAndCode(profile, normalizeCode(request.getCode()))) {
            throw new IllegalArgumentException("이미 사용 중인 몬스터 코드입니다.");
        }
        normalizeRequired(request.getName(), "몬스터 이름을 입력해 주세요.");
    }

    private void validateUpdate(Monster monster, MonsterUpdateRequest request) {
        DeveloperProfile profile = monster.getProfile();
        String code = normalizeCode(request.getCode());
        if (!monster.getCode().equals(code) && monsterRepository.existsByProfileAndCode(profile, code)) {
            throw new IllegalArgumentException("이미 사용 중인 몬스터 코드입니다.");
        }
        normalizeRequired(request.getName(), "몬스터 이름을 입력해 주세요.");
    }

    private GrowthCategory findCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("성장 대분류를 찾을 수 없습니다."));
    }

    private GrowthSubCategory findSubCategory(Long subCategoryId) {
        if (subCategoryId == null) {
            return null;
        }
        return subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("성장 중분류를 찾을 수 없습니다."));
    }

    private int resolveMaxHp(Integer maxHp, MonsterDifficulty difficulty) {
        int resolved = maxHp == null ? difficulty.getDefaultMaxHp() : maxHp;
        if (resolved <= 0) {
            throw new IllegalArgumentException("최대 HP는 1 이상이어야 합니다.");
        }
        return resolved;
    }

    private int resolveRewardExp(Integer rewardExp) {
        int resolved = rewardExp == null ? 0 : rewardExp;
        if (resolved < 0) {
            throw new IllegalArgumentException("보상 EXP는 0 이상이어야 합니다.");
        }
        return resolved;
    }

    private String normalizeRequired(String text, String message) {
        String normalized = normalize(text);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String normalizeCode(String code) {
        return normalizeRequired(code, "몬스터 코드를 입력해 주세요.").toUpperCase();
    }

    private String normalize(String text) {
        return text == null ? "" : text.trim();
    }
}
