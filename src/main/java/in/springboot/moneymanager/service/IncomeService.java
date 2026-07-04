package in.springboot.moneymanager.service;

import in.springboot.moneymanager.dto.ExpenseDTO;
import in.springboot.moneymanager.dto.IncomeDTO;
import in.springboot.moneymanager.entity.CategoryEntity;
import in.springboot.moneymanager.entity.ExpenseEntity;
import in.springboot.moneymanager.entity.IncomeEntity;
import in.springboot.moneymanager.entity.ProfileEntity;
import in.springboot.moneymanager.repository.CategoryRepository;
import in.springboot.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    public IncomeDTO addIncome(IncomeDTO expenseDTO){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        IncomeEntity newIncome = toEntity(expenseDTO,profile,category);
        newIncome = incomeRepository.save(newIncome);
        return toDTO(newIncome);

    }

    // Retrieve incomes for Current User for Current Month
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> incomeEntities =  incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return incomeEntities.stream().map(this::toDTO).toList();
    }

    // delete income by id for current user
    public void deleteIncomeForCurrentUser(Long incomeId){
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity incomeToDelete = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if(!incomeToDelete.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Not authorized to delete this income");
        }
        incomeRepository.delete(incomeToDelete);
    }

    // get top 5 latest Incomes for the current user
    public List<IncomeDTO> getLatest5IncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> incomes = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return incomes.stream().map(this::toDTO).toList();
    }

    // get total sum of incomes of user
    public BigDecimal getTotalIncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalIncomeByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> incomeEntities = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
                profile.getId(),
                startDate,
                endDate,
                keyword,
                sort
        );
        return incomeEntities.stream().map(this::toDTO).toList();
    }

    // helper methods
    private IncomeEntity toEntity(IncomeDTO incomeDTO, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getName())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity income){
        return IncomeDTO.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .categoryId(income.getCategory() != null ? income.getCategory().getId() : null)
                .categoryName(income.getCategory() != null ? income.getCategory().getName() : "N/A")
                .amount(income.getAmount())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }
}
