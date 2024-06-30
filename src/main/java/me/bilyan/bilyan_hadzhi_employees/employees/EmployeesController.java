package me.bilyan.bilyan_hadzhi_employees.employees;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.lang.invoke.MethodHandles;

@Controller
@RequestMapping(value = "/employees")
public class EmployeesController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EmployeesWhoWorkedTogetherService employeesWhoWorkedTogetherService;

    @Autowired
    public EmployeesController(EmployeesWhoWorkedTogetherService employeesWhoWorkedTogetherService) {
        this.employeesWhoWorkedTogetherService = employeesWhoWorkedTogetherService;
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String getEmployeesWhoHaveWorkedTogether(Model model,
                                                    @RequestParam("file") MultipartFile file,
                                                    @RequestParam("dateFormat") String dateFormat) {
        var result = employeesWhoWorkedTogetherService.getEmployeesWhoWorkedTogetherFromFile(file, dateFormat);
        model.addAttribute("data", result);

        return "employees";
    }

    @ExceptionHandler({Exception.class})
    public ModelAndView handleException(HttpServletRequest request, Exception exception) {
        LOGGER.error("Exception was raised at {}", request.getRequestURL(), exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("exceptionMessage", exception.getMessage());
        modelAndView.setViewName("error");

        return modelAndView;
    }
}
