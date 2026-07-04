package in.springboot.moneymanager.Controller;

import in.springboot.moneymanager.dto.ExpenseDTO;
import in.springboot.moneymanager.dto.IncomeDTO;
import in.springboot.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO incomeDTO){
        IncomeDTO newIncome = incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newIncome);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getExpenses(){
        List<IncomeDTO> incomeDTOS = incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.ok(incomeDTOS);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id){
        incomeService.deleteIncomeForCurrentUser(id);
        return ResponseEntity.noContent().build();
    }

}
