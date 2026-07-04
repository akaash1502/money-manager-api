package in.springboot.moneymanager.Controller;

import in.springboot.moneymanager.dto.ExpenseDTO;
import in.springboot.moneymanager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO){
        ExpenseDTO newExpense = expenseService.addExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newExpense);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpenses(){
        List<ExpenseDTO> expenseDTOS = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(expenseDTOS);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        expenseService.deleteExpenseForCurrentUser(id);
        return ResponseEntity.noContent().build();
    }
}
