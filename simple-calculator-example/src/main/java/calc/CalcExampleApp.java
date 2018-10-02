package calc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@SpringBootApplication
public class CalcExampleApp {

    public static void main(String[] args) {
        SpringApplication.run(CalcExampleApp.class, args);
    }
}

@Controller
@RequestMapping("/calculate")
class Calculator {

    @GetMapping
    public String showCalculatorPage() {
        return "calc";
    }

    @GetMapping(params = {"n1", "n2", "op"})
    public String calcWithUrlParameters(
            BigDecimal n1,
            BigDecimal n2,
            Operator op,
            Model model) {

        BigDecimal result = op.apply(n1, n2);
        model.addAttribute("resultGet", result);

        return showCalculatorPage();
    }

    @PostMapping
    public String calcWithPostRequestBody(
            @RequestParam BigDecimal n1,
            @RequestParam BigDecimal n2,
            @RequestParam Operator op,
            Model model) {

        BigDecimal result = op.apply(n1, n2);
        model.addAttribute("resultPost", result);

        return showCalculatorPage();
    }

    enum Operator implements BiFunction<BigDecimal, BigDecimal, BigDecimal> {

        PLUS {
            @Override
            public BigDecimal apply(BigDecimal n1, BigDecimal n2) {
                return n1.add(n2);
            }
        },

        MINUS {
            @Override
            public BigDecimal apply(BigDecimal n1, BigDecimal n2) {
                return n1.subtract(n2);
            }
        },

        MULTIPLY {
            @Override
            public BigDecimal apply(BigDecimal n1, BigDecimal n2) {
                return n1.multiply(n2);
            }
        },

        DIVIDE {
            @Override
            public BigDecimal apply(BigDecimal n1, BigDecimal n2) {
                return n1.divide(n2, 10, BigDecimal.ROUND_CEILING);
            }
        },
    }

}
