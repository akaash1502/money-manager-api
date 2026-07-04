package in.springboot.moneymanager.service;

import in.springboot.moneymanager.dto.ExpenseDTO;
import in.springboot.moneymanager.dto.IncomeDTO;
import in.springboot.moneymanager.entity.CategoryEntity;
import in.springboot.moneymanager.entity.ExpenseEntity;
import in.springboot.moneymanager.entity.ProfileEntity;
import in.springboot.moneymanager.repository.CategoryRepository;
import in.springboot.moneymanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.analysis.function.Exp;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    public ExpenseDTO addExpense(ExpenseDTO expenseDTO){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        ExpenseEntity newExpense = toEntity(expenseDTO,profile,category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);

    }

    // Retrieve all expenses for the current profile
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> expenseEntities =  expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return expenseEntities.stream().map(this::toDTO).toList();
    }

    // delete expense by id for current user
    public void deleteExpenseForCurrentUser(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity expenseToDelete = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if(!expenseToDelete.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Not authorized to delete this expense");
        }
        expenseRepository.delete(expenseToDelete);
    }

    // get top 5 latest expenses for the current user
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> expenses = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return expenses.stream().map(this::toDTO).toList();
    }

    // get total sum of expenses of user
    public BigDecimal getTotalExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    //filter Expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> expenseEntities = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
                profile.getId(),
                startDate,
                endDate,
                keyword,
                sort
        );
        return expenseEntities.stream().map(this::toDTO).toList();
    }

    // for notifications
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId,LocalDate date){
        List<ExpenseEntity> expenses = expenseRepository.findByProfileIdAndDate(profileId,date);
        return expenses.stream().map(this::toDTO).toList();
    }

    // helper methods
    private ExpenseEntity toEntity(ExpenseDTO expenseDTO, ProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity expense){
        return ExpenseDTO.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : "N/A")
                .amount(expense.getAmount())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }


}
